package lv.emendatus.Destiny_PropMan.service.implementation;

import lv.emendatus.Destiny_PropMan.domain.entity.KeyLink;
import lv.emendatus.Destiny_PropMan.domain.entity.NumericDataMapping;
import lv.emendatus.Destiny_PropMan.domain.entity.NumericalConfig;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.NumConfigType;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.UserType;
import lv.emendatus.Destiny_PropMan.service.interfaces.CardDataSaverService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class JpaCardDataSaverService implements CardDataSaverService {

    private final JpaNumericalConfigService configService;
    private final JpaKeyLinkService keyLinkService;

    public JpaCardDataSaverService(JpaNumericalConfigService configService, JpaKeyLinkService keyLinkService) {
        this.configService = configService;
        this.keyLinkService = keyLinkService;
    }

    @Override
    @Transactional
    public boolean writeNDMToFile(NumericDataMapping mapping) throws IOException {
        System.out.println();
        System.out.println("    !!!!!   writeNDMToFile method INVOKED !!!!! ");
        System.out.println();
        Optional<NumericalConfig> optionalConfig = configService.getNumericalConfigByName("CardDataRecordCounter");
        int currentRecordNumber = 0;
        // Checking for / creating the required NumericalConfig
        if (optionalConfig.isEmpty()) {
            currentRecordNumber = 1;
            NumericalConfig config = new NumericalConfig();
            config.setValue(1.00);
            config.setName("CardDataRecordCounter");
            config.setType(NumConfigType.SYSTEM_SETTING);
            configService.addNumericalConfig(config);
        } else {
            NumericalConfig config = optionalConfig.get();
            currentRecordNumber = config.getValue().intValue() + 1;
            config.setValue((double) currentRecordNumber);
            configService.addNumericalConfig(config);
        }
        // Checking for / creating the required folder
        String folderPath = "Extrastore";
        File folder = new File(folderPath);
        if (!folder.exists()) {
            boolean created = folder.mkdirs();
            if (created) {
                System.out.println("Folder created successfully: " + folder.getAbsolutePath());
            } else {
                throw new IOException("An error has occurred while creating the required folder");
            }
        }
        int fileIndex = 1;
        String fileName = "vault" + (fileIndex + (currentRecordNumber / 100)) + ".dps";
        // Checking for / creating the required file
        File file = new File(folderPath, fileName);
        if (!file.exists()) {
            try {
                boolean created = file.createNewFile();
                if (created) {
                    System.out.println("File created successfully: " + file.getAbsolutePath());
                } else {
                    throw new IOException("An error has occurred while creating the required file");
                }
            } catch (IOException e) {
                System.err.println("An error occurred while creating the file: " + e.getMessage());
            }
        } else {
            System.out.println("Found the required file: " + file.getAbsolutePath());
        }

        // Creating and saving a KeyLink
        Long userId = mapping.getMap().keySet().stream().findFirst().orElse(null);
        UserType userType = mapping.getMap().get(userId).keySet().stream().findFirst().orElse(null);
        KeyLink keyLink = keyLinkService.fetchALink(userId, userType);
        if (keyLink == null) {
            keyLink = new KeyLink();
            keyLink.setFileNumber(fileIndex + (currentRecordNumber / 100));
            System.out.println("File Number: " + keyLink.getFileNumber());
            keyLink.setUserId(userId);
            System.out.println("User ID: " + keyLink.getUserId());
            keyLink.setUserType(userType);
            System.out.println("User Type: " + keyLink.getUserType());
            keyLinkService.addKeyLink(keyLink);
        } else {
            keyLink.setFileNumber(fileIndex + (currentRecordNumber / 100));
            keyLinkService.addKeyLink(keyLink); // Assuming addKeyLink updates if it exists
        }


        // Convert SecretKeys to Base64 strings and store in an intermediate map
        System.out.println();
        System.out.println(" --==::|| Initial string representation of a NumericDataMapping: " + mapping.toString());
        System.out.println();

        System.out.println("Started converting SecretKeys to Base64 strings and storing them in an intermediate map");
        Map<Long, Map<UserType, Map<Boolean, String>>> encodedMap = new HashMap<>();
        for (Map.Entry<Long, Map<UserType, Map<Boolean, SecretKey>>> userEntry : mapping.getMap().entrySet()) {
            Map<UserType, Map<Boolean, String>> userTypeMap = new HashMap<>();
            for (Map.Entry<UserType, Map<Boolean, SecretKey>> typeEntry : userEntry.getValue().entrySet()) {
                Map<Boolean, String> booleanKeyMap = new HashMap<>();
                for (Map.Entry<Boolean, SecretKey> booleanEntry : typeEntry.getValue().entrySet()) {
                    booleanKeyMap.put(booleanEntry.getKey(), encodeSecretKey(booleanEntry.getValue()));
                }
                userTypeMap.put(typeEntry.getKey(), booleanKeyMap);
            }
            encodedMap.put(userEntry.getKey(), userTypeMap);
        }

        System.out.println();
        System.out.println(" --==::|| String representation of an encoded NumericDataMapping (the encodedMap variable): " + encodedMap.toString());
        System.out.println();

        // Replace SecretKey placeholders in the string representation
        System.out.println("Replacing SecretKey placeholders in the String representation");
        String unencryptedMapping = mapping.toString();
        for (Map.Entry<Long, Map<UserType, Map<Boolean, String>>> userEntry : encodedMap.entrySet()) {
            for (Map.Entry<UserType, Map<Boolean, String>> typeEntry : userEntry.getValue().entrySet()) {
                for (Map.Entry<Boolean, String> booleanEntry : typeEntry.getValue().entrySet()) {
                    String placeholder = "javax.crypto.spec.SecretKeySpec@";
                    unencryptedMapping = unencryptedMapping.replaceFirst(
                            placeholder + "[a-fA-F0-9]+",
                            booleanEntry.getValue()
                    );
                }
            }
        }

        System.out.println();
        System.out.println(" --==::|| String representation of an encoded NumericDataMapping (the encodedMap variable) after the SecretKey placeholders have been replaced: " + encodedMap.toString());
        System.out.println();

        System.out.println();
        System.out.println(" --==::|| String representation of the Unencrypted Mapping before encrypting the String and adding metadata (the unencryptedMapping variable): " + unencryptedMapping);
        System.out.println();

        // Encrypt the string and add metadata
        System.out.println("Encrypting the String and adding metadata");
        Random r = new Random();
        StringBuilder sb = new StringBuilder();
        int indexAtTheEnd = 0;
        // Assigning a UserType index - odd for Tenants, even for Managers
        if (keyLink.getUserType().equals(UserType.TENANT)) {
            indexAtTheEnd = r.nextInt(0, 5) * 2 + 1;
        }
        if (keyLink.getUserType().equals(UserType.MANAGER)) {
            indexAtTheEnd = r.nextInt(1, 5) * 2;
        }
        int ASCIIShift = r.nextInt(1, 10); // number that will be added to the ASCII value of each char of the string being encoded
        int extraRandomDigitCount = r.nextInt(5, 10); // number of random digits added "for confusion" between the row itself and the last two characters
        int keyNumberPositionAtPenultimateCharacter = r.nextInt(0, extraRandomDigitCount); // position within the extraRandomDigitCount-long row of random numbers where the ASCIIShift will be stored

        // Encrypting the incoming String into a StringBuilder
        char[] charredString = unencryptedMapping.toCharArray();
        for (char character : charredString) {
            int newCharIndex = (int) character + ASCIIShift;
            if (newCharIndex > 126) {
                newCharIndex = (newCharIndex % 126) + 32; // Ensuring characters remain printable
            }
            sb.append((char) newCharIndex);
        }

        System.out.println();
        System.out.println(" --==::|| The Stringbuilder that is supposed to become the ecnrypted Mapping after encryption, but before adding obfuscators and metadata: " + sb.toString());
        System.out.println();

        // Adding extra numbers to the end, including the actual ASCIIShift, then adding the position of this key number within random numbers
        System.out.println("Adding extra numbers to the end, including the actual ASCIIShift, then adding the position of this key number within random numbers");
        for (int i = 0; i < extraRandomDigitCount; i++) {
            if (i == keyNumberPositionAtPenultimateCharacter) {
                sb.append(ASCIIShift);
            } else sb.append(r.nextInt(0, 10));
        }

        System.out.println();
        System.out.println(" --==::|| The Stringbuilder that is supposed to become the ecnrypted Mapping after encryption, with added obfuscators: " + sb.toString());
        System.out.println();

        // Adding another digit, which will specify the length of the extra digits added before
        System.out.println("Appending metadata");
        sb.append(extraRandomDigitCount);

        // Adding the penultimate digit - position of the ASCIIShift within the "extra numbers" row
        sb.append(keyNumberPositionAtPenultimateCharacter);

        // Adding the last digit - any digit, odd for Tenants, even for Managers
        sb.append(indexAtTheEnd);
        String encodedString = sb.toString();

        System.out.println();
        System.out.println(" --==::|| The Stringbuilder that is supposed to become the ecnrypted Mapping after encryption, with added obfuscators and metadata: " + encodedString);
        System.out.println();

        // Writing the encoded String to the file
        boolean successful = false;
        System.out.println("Writing the encoded String to the file");
        try (FileWriter writer = new FileWriter(file, true)) {
            writer.write(encodedString);
            writer.write(System.lineSeparator());
            successful = true;
            return successful;
        } catch (Exception e) {
            throw new IOException("Failed to save the encoded string to a file!!!");
        }
    }


    @Override
    public NumericDataMapping extractNDMFromFile(UserType userType, Long userId) throws IOException {
        // Finding the required file
        KeyLink keyLink = keyLinkService.fetchALink(userId, userType);
        String fileName = "vault" + keyLink.getFileNumber() + ".dps";
        File file = new File("Extrastore", fileName);
        if (!file.exists()) {
            throw new IOException("The required records file could not be retrieved!");
        }
        List<String> encodedEntries = new ArrayList<>();
        // Reading the file content into a list of strings
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    encodedEntries.add(line);
//                    System.out.println("Retrieved a line: " + line);
                }
            }
        } catch (IOException e) {
            throw new IOException("Failed to read the records file", e);
        }
//        System.out.println();
//        System.out.println("Obtained ENCODED entries:");
//        for (String string : encodedEntries) {
//            System.out.println(string);
//        }
        // Sieve the entries, leaving only those with the appropriate UserType
        List<Character> soughtRoleTypes = new ArrayList<>();
        if (userType.equals(UserType.TENANT)) {
            for (int i = 1; i < 10; i += 2) soughtRoleTypes.add(Character.forDigit(i, 10));
        }
        if (userType.equals(UserType.MANAGER)) {
            for (int i = 0; i < 10; i += 2) soughtRoleTypes.add(Character.forDigit(i, 10));
        }
        List<String> userRoleEntries = new ArrayList<>();
        for (String entry : encodedEntries) {
            if (!entry.equals("") && soughtRoleTypes.contains(entry.charAt(entry.length()-1))) userRoleEntries.add(entry);
        }
//        System.out.println();
//        System.out.println("Obtained entries that MATCH THE ROLE (the final digit is even for Managers, odd for Tenants):");
//        for (String string : userRoleEntries) {
//            System.out.println(string);
//        }
        // Collecting ASCII shift index positions and obfuscation number length values into a list of size similar to that of soughtRoleTypes
        List<Integer> ASCIIShiftIndexPositions = new ArrayList<>();
        List<Integer> obfuscateNumbersLengthValues = new ArrayList<>();
        for (String entry : userRoleEntries) {
            ASCIIShiftIndexPositions.add(Character.getNumericValue(entry.charAt(entry.length() - 2)));
            obfuscateNumbersLengthValues.add(Character.getNumericValue(entry.charAt(entry.length() - 3)));
        }
//        System.out.println();
//        System.out.println("Obtained ASCII shift index positions and obfuscate number row length values:");
//        int boo = 0;
//        for (String string : userRoleEntries) {
//            boo++;
//            System.out.println("Record " + boo + " ASCII shift index position within the obfuscate row: " + ASCIIShiftIndexPositions.get(boo-1));
//            System.out.println("Record " + boo + " obfuscate number row length value: " + obfuscateNumbersLengthValues.get(boo-1));
//        }
        // Obtaining actual ASCII shift indices for each row and trimming rows to encoded parts only
        List<Integer> ASCIIShiftIndices = new ArrayList<>();
        List<String> trimmedEncodedRows = new ArrayList<>();
        int indexCounter = 0;
        for (String entry : userRoleEntries) {
            char shiftChar = entry.charAt(entry.length() - 3 - obfuscateNumbersLengthValues.get(indexCounter) + ASCIIShiftIndexPositions.get(indexCounter));
            ASCIIShiftIndices.add(Character.getNumericValue(shiftChar));
            trimmedEncodedRows.add(entry.substring(0, entry.length() - 3 - obfuscateNumbersLengthValues.get(indexCounter)));
            indexCounter++;
        }
//        boo = 0;
//        for (Integer shiftIndex : ASCIIShiftIndices) {
//            boo++;
//            System.out.println("Record " + boo + " ASCII shift index: " + ASCIIShiftIndices.get(boo-1));
//        }
//        boo = 0;
//        for (String trimmedRow : trimmedEncodedRows) {
//            boo++;
//            System.out.println("Record " + boo + " trimmed encoded row: " + trimmedEncodedRows.get(boo-1));
//        }
        // Adding successfully decrypted strings to another List
        List<String> successfullyDecryptedStrings = new ArrayList<>();
        indexCounter = 0;
        for (String entry : trimmedEncodedRows) {
            StringBuilder sb = new StringBuilder();
            char[] charredString = entry.toCharArray();
            for (char character : charredString) {
                int newCharIndex = (int) character - ASCIIShiftIndices.get(indexCounter);
                // "De-circling" of ASCII characters
                if (newCharIndex < 32) {
                    newCharIndex = 126 - (32 - newCharIndex); // Wrap-around to stay within printable ASCII characters
                }
                sb.append((char) newCharIndex);
            }
            String resultingString = sb.toString();

//            System.out.println("DECRYPTED A RECORD: " + resultingString);
            if (resultingString.length() >= 22) {
                String firstPart = resultingString.substring(0, 22);
                if (firstPart.equals("NumericDataMapping{id=")) {
                    successfullyDecryptedStrings.add(resultingString);
                }
            }
            indexCounter++;
        }

//        System.out.println();
//        System.out.println("Decrypted strings here:");
//        for (String decryptedString : successfullyDecryptedStrings) {
//            System.out.println(decryptedString);
//        }

        int counter = 0;
        List<NumericDataMapping> mappings = new ArrayList<>();
        for (String decryptedString : successfullyDecryptedStrings) {
            // Finding the end of the ID section
            counter++;
            int endOfIdIndex = decryptedString.indexOf(", map=");
//            System.out.println("Identified the endOfIdIndex for line " + counter);
            if (endOfIdIndex != -1) { // Checking for existence of the section
                // Extracting the NumericDataMapping ID
//                System.out.println("Entered the processing block");
                String idPart = decryptedString.substring("NumericDataMapping{id=".length(), endOfIdIndex);
//                System.out.println("Isolated the ID part: " + idPart);
                Long mappingId = Long.parseLong(idPart.trim());
//                System.out.println("Fetched the mapping ID: " + mappingId);
                // Extracting the map section
                String mapSection = decryptedString.substring(endOfIdIndex + ", map=".length());
//                System.out.println("Extracted the map section: " + mapSection);

// Initializing a new map to hold the parsed data
                Map<Long, Map<UserType, Map<Boolean, SecretKey>>> newMap = new HashMap<>();

// Identifying userIdStartIndex and userIdEndIndex correctly
                int firstOpeningBrace = mapSection.indexOf('{');
                int firstClosingBrace = mapSection.indexOf('}', firstOpeningBrace);
                String userIdPart = mapSection.substring(firstOpeningBrace + 1, firstClosingBrace).split("=")[0].trim();
//                System.out.println("Extracted the userIdPart: " + userIdPart);
                Long extractedUserId = Long.parseLong(userIdPart);
//                System.out.println("User ID for use as key for Level 1 map: " + extractedUserId);
//
//                System.out.println("User type for use as key for Level 2 map: " + userType);

                // extracting the encrypted Secretkey line from under the true key in Level 3 Map (for card number)


                StringBuilder sbForTrue = new StringBuilder();
                for (int i = 0; i < decryptedString.length(); i++) {
                    if (decryptedString.charAt(i) == 't')
                        if (decryptedString.charAt(i+1) == 'r')
                            if (decryptedString.charAt(i+2) == 'u')
                                if (decryptedString.charAt(i+3) == 'e')
                                    if (decryptedString.charAt(i+4) == '=') {
                                        int encryptedCardNumberKeyStartIndex = i + 5;
                                        while (!(decryptedString.charAt(encryptedCardNumberKeyStartIndex) == '}')) {
                                            sbForTrue.append(decryptedString.charAt(encryptedCardNumberKeyStartIndex));
                                            encryptedCardNumberKeyStartIndex++;
                                        }
                                        break;
                                    }
                }
                String encryptedCardNumberKey = sbForTrue.toString();
//                System.out.println("Encrypted SecretKey for card number: " + encryptedCardNumberKey);

                // extracting the encrypted Secretkey from under the false key in Level 3 Map (for CVV)
                StringBuilder sbForFalse = new StringBuilder();
                for (int i = 0; i < decryptedString.length(); i++) {
                    if (decryptedString.charAt(i) == 'f')
                        if (decryptedString.charAt(i+1) == 'a')
                            if (decryptedString.charAt(i+2) == 'l')
                                if (decryptedString.charAt(i+3) == 's')
                                    if (decryptedString.charAt(i+4) == 'e')
                                        if (decryptedString.charAt(i+5) == '=') {
                                            int encryptedCVVKeyStartIndex = i + 6;
                                            while (!(decryptedString.charAt(encryptedCVVKeyStartIndex) == ',')) {
                                                sbForFalse.append(decryptedString.charAt(encryptedCVVKeyStartIndex));
                                                encryptedCVVKeyStartIndex++;
                                            }
                                            break;
                                    }
                }
                String encryptedCVVKey = sbForFalse.toString();
//                System.out.println("Encrypted SecretKey for CVV code: " + encryptedCVVKey);

                // Decode the secret keys
                SecretKey cardNumberKey = decodeSecretKey(encryptedCardNumberKey, "AES");
                SecretKey cvvKey = decodeSecretKey(encryptedCVVKey, "AES");

                // Create the map structure
                Map<Boolean, SecretKey> booleanSecretKeyMap = new HashMap<>();
                booleanSecretKeyMap.put(true, cardNumberKey);
                booleanSecretKeyMap.put(false, cvvKey);

                Map<UserType, Map<Boolean, SecretKey>> userTypeMap = new HashMap<>();
                userTypeMap.put(userType, booleanSecretKeyMap);

                newMap.put(extractedUserId, userTypeMap);

                // Create the NumericDataMapping object
                NumericDataMapping numericDataMapping = new NumericDataMapping();
                numericDataMapping.setId(mappingId);
                numericDataMapping.setMap(newMap);
                mappings.add(numericDataMapping);
//                System.out.println("Added a mapping to the List intended for further processing");
            } else System.out.println("   Error: returned -1 as endOfIdIndex for line " + counter + "!!!");
        }
// Selecting and returning the specific NumericDataMapping for the given userId
        for (NumericDataMapping mapping : mappings) {
            if (mapping.getMap().containsKey(userId)) {
                System.out.println("The required mapping has been returned successfully");
                System.out.println(mapping.toString());
                return mapping;
            }
        }

        // If no mapping is found for the given userId, throw an exception or return null
        throw new IOException("No matching NumericDataMapping found for the specified userId.");

    }

    @Override
    @Transactional
    public void removeNDMRecordFromFile(UserType userType, Long userId) throws Exception {
        KeyLink keyLink = keyLinkService.fetchALink(userId, userType);
        String fileName = "vault" + keyLink.getFileNumber() + ".dps";
        File file = new File("Extrastore", fileName);
        if (!file.exists()) {
            throw new IOException("The required records file could not be retrieved!");
        }
        List<String> encodedEntries = new ArrayList<>();
        // Reading the file content into a list of strings
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    encodedEntries.add(line);
//                    System.out.println("Retrieved a line: " + line);
                }
            }
        } catch (IOException e) {
            throw new IOException("Failed to read the records file", e);
        }
        List<Character> soughtRoleTypes = new ArrayList<>();
        if (userType.equals(UserType.TENANT)) {
            for (int i = 1; i < 10; i += 2) soughtRoleTypes.add(Character.forDigit(i, 10));
        }
        if (userType.equals(UserType.MANAGER)) {
            for (int i = 0; i < 10; i += 2) soughtRoleTypes.add(Character.forDigit(i, 10));
        }
        List<String> userRoleEntries = new ArrayList<>();
        for (String entry : encodedEntries) {
            if (!entry.equals("") && soughtRoleTypes.contains(entry.charAt(entry.length()-1))) userRoleEntries.add(entry);
        }

        List<Integer> ASCIIShiftIndexPositions = new ArrayList<>();
        List<Integer> obfuscateNumbersLengthValues = new ArrayList<>();
        for (String entry : userRoleEntries) {
            ASCIIShiftIndexPositions.add(Character.getNumericValue(entry.charAt(entry.length() - 2)));
            obfuscateNumbersLengthValues.add(Character.getNumericValue(entry.charAt(entry.length() - 3)));
        }

        List<Integer> ASCIIShiftIndices = new ArrayList<>();
        List<String> trimmedEncodedRows = new ArrayList<>();
        int indexCounter = 0;
        for (String entry : userRoleEntries) {
            char shiftChar = entry.charAt(entry.length() - 3 - obfuscateNumbersLengthValues.get(indexCounter) + ASCIIShiftIndexPositions.get(indexCounter));
            ASCIIShiftIndices.add(Character.getNumericValue(shiftChar));
            trimmedEncodedRows.add(entry.substring(0, entry.length() - 3 - obfuscateNumbersLengthValues.get(indexCounter)));
            indexCounter++;
        }

        List<String> successfullyDecryptedStrings = new ArrayList<>();
        indexCounter = 0;
        for (String entry : trimmedEncodedRows) {
            StringBuilder sb = new StringBuilder();
            char[] charredString = entry.toCharArray();
            for (char character : charredString) {
                int newCharIndex = (int) character - ASCIIShiftIndices.get(indexCounter);
                if (newCharIndex < 32) {
                    newCharIndex = 126 - (32 - newCharIndex);
                }
                sb.append((char) newCharIndex);
            }
            String resultingString = sb.toString();
            if (resultingString.length() >= 22) {
                String firstPart = resultingString.substring(0, 22);
                if (firstPart.equals("NumericDataMapping{id=")) {
                    successfullyDecryptedStrings.add(resultingString);
                }
            }
            indexCounter++;
        }

        int counter = 0;
        int recordIndexInLists = 0;
        String encryptedStringToRemove = "";
        List<NumericDataMapping> mappings = new ArrayList<>();
        for (String decryptedString : successfullyDecryptedStrings) {
            int endOfIdIndex = decryptedString.indexOf(", map=");
            if (endOfIdIndex != -1) {
                String idPart = decryptedString.substring("NumericDataMapping{id=".length(), endOfIdIndex);
                String mapSection = decryptedString.substring(endOfIdIndex + ", map=".length());
                int firstOpeningBrace = mapSection.indexOf('{');
                int firstClosingBrace = mapSection.indexOf('}', firstOpeningBrace);
                String userIdPart = mapSection.substring(firstOpeningBrace + 1, firstClosingBrace).split("=")[0].trim();
                Long extractedUserId = Long.parseLong(userIdPart);
                if (extractedUserId.equals(userId)) recordIndexInLists = counter;
            }
            counter++;
        }

        if (userRoleEntries.size() == successfullyDecryptedStrings.size()) {
            encryptedStringToRemove = userRoleEntries.get(recordIndexInLists);
            encodedEntries.remove(encryptedStringToRemove);
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                for (String entry : encodedEntries) {
                    writer.write(entry);
                    writer.newLine();
                }
            } catch (IOException e) {
                throw new IOException("Failed to write to the records file", e);
            }
        } else throw new Exception("Error in the removeNDMRecordFromFile method logic: user role entry list and decrypte string list are of different size");
    }

    // AUXILIARY METHODS
    // Convert SecretKey to a Base64 string and vice versa
    public String encodeSecretKey(SecretKey secretKey) {
        return Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }

    // Convert a Base64 string back to a SecretKey
    public SecretKey decodeSecretKey(String encodedKey, String algorithm) {
        byte[] decodedKey = Base64.getDecoder().decode(encodedKey);
        return new SecretKeySpec(decodedKey, algorithm);
    }
}
