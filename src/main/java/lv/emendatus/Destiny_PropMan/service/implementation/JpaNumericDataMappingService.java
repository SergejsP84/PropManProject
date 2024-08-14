package lv.emendatus.Destiny_PropMan.service.implementation;

import lv.emendatus.Destiny_PropMan.domain.entity.NumericDataMapping;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.UserType;
import lv.emendatus.Destiny_PropMan.repository.interfaces.NumericDataMappingRepository;
import lv.emendatus.Destiny_PropMan.service.interfaces.NumericDataMappingService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.*;

@Service
public class JpaNumericDataMappingService implements NumericDataMappingService {
    private final NumericDataMappingRepository repository;
    private final Logger LOGGER = LogManager.getLogger(JpaPropertyService.class);
    private final JpaCardDataSaverService cardDataSaverService;
    @Autowired
    public JpaNumericDataMappingService(NumericDataMappingRepository repository, JpaCardDataSaverService cardDataSaverService) {
        this.repository = repository;
        this.cardDataSaverService = cardDataSaverService;
    }

    @Override
    public List<NumericDataMapping> getAllMappings() {
        if (repository.findAll().size() < 1) return new ArrayList<>();
        return repository.findAll();
    }

    @Override
    public Optional<NumericDataMapping> getMappingById(Long id) {
        return repository.findById(id);
    }
    public SecretKey returnCardNumberSecretKeyById(Long userId, UserType userType) throws IOException {
        NumericDataMapping numericDataMapping = cardDataSaverService.extractNDMFromFile(userType, userId);
//        System.out.println("   (JpaNumericDataMappingService, returnCardNumberSecretKeyById) Extracted a NumericDataMapping: " + numericDataMapping);
        if (numericDataMapping != null) {
//            System.out.println("   (JpaNumericDataMappingService, returnCardNumberSecretKeyById) NDM not null, obtaining a Level 2 map");
            Map<UserType, Map<Boolean, SecretKey>> userTypeMap = numericDataMapping.getMap().get(userId);
            if (userTypeMap != null) {
//                System.out.println("   (JpaNumericDataMappingService, returnCardNumberSecretKeyById) Level 2 map obtained successfully, obtaining a Level 3 map");
                Map<Boolean, SecretKey> booleanMap = userTypeMap.get(userType);
                if (booleanMap != null) {
//                    System.out.println("   (JpaNumericDataMappingService, returnCardNumberSecretKeyById) Level 3 map obtained successfully, obtaining the secret key for the CARD NUMBER");
                    return booleanMap.get(Boolean.TRUE); // Assuming TRUE key for card number
                }
            }
        }
//        System.out.println("   (JpaNumericDataMappingService, returnCardNumberSecretKeyById) cardDataSaverService.extractNDMFromFile returned a NULL when attempting to retrieve a card number for " + userType + " " + userId);
        return null;
    }

    public SecretKey returnCVVSecretKeyById(Long userId, UserType userType) throws IOException {
        NumericDataMapping numericDataMapping = cardDataSaverService.extractNDMFromFile(userType, userId);
//        System.out.println("   (JpaNumericDataMappingService, returnCVVSecretKeyById) Extracted a NumericDataMapping: " + numericDataMapping);
        if (numericDataMapping != null) {
//            System.out.println("   (JpaNumericDataMappingService, returnCVVSecretKeyById) NDM not null, obtaining a Level 2 map");
            Map<UserType, Map<Boolean, SecretKey>> userTypeMap = numericDataMapping.getMap().get(userId);
            if (userTypeMap != null) {
//                System.out.println("   (JpaNumericDataMappingService, returnCVVSecretKeyById) Level 2 map obtained successfully, obtaining a Level 3 map");
                Map<Boolean, SecretKey> booleanMap = userTypeMap.get(userType);
                if (booleanMap != null) {
//                    System.out.println("   (JpaNumericDataMappingService, returnCVVSecretKeyById) Level 3 map obtained successfully, obtaining the secret key for the CVV");
                    SecretKey cvvKey = booleanMap.get(Boolean.FALSE); // Assuming FALSE key for CVV
                    System.out.println("   Retrieved CVV secret key: " + cvvKey);
                    return cvvKey;
                }
            }
        }
//        System.out.println("   (JpaNumericDataMappingService, returnCVVKeyById) cardDataSaverService.extractNDMFromFile returned a NULL when attempting to retrieve a CVV for " + userType + " " + userId);
        return null;
    }

    @Override
    @Transactional
    public void saveCardNumberSecretKey(Long id, UserType userType, SecretKey cardNumberSecretKey) {
        System.out.println("    --------------     1) saveCardNumberSecretKey method invoked");
        List<NumericDataMapping> sameIDMaps = new ArrayList<>();
        Map<Long, Map<UserType, Map<Boolean, SecretKey>>> level1Map = new HashMap<>();
        Map<UserType, Map<Boolean, SecretKey>> level2Map = new HashMap<>();
        Map<Boolean, SecretKey> level3Map = new HashMap<>();
        System.out.println("    --------------     2) Empty starting entities initialized");
        System.out.println("    --------------     3) Step 3 excluded");
            System.out.println("    --------------     4a) Triggered the case with no existing maps with this ID");
            level3Map.put(true, cardNumberSecretKey);
            System.out.println("    --------------     5a) Level 3 Map generated: key - " + level3Map.keySet().toString() + ", value - " + level3Map.values().toString());
            level2Map.put(userType, level3Map);
            System.out.println("    --------------     6a) Level 2 Map generated: key - " + level2Map.keySet().toString() + ", value - " + level2Map.values().toString());
            level1Map.put(id, level2Map);
            System.out.println("    --------------     7a) Level 1 Map generated: key - " + level1Map.keySet().toString() + ", value - " + level1Map.values().toString());
            NumericDataMapping mapping = new NumericDataMapping();
            mapping.setMap(level1Map);
            System.out.println("    --------------     8a) Created a new Mapping");
            repository.save(mapping);
            System.out.println("    --------------     9a) Saved the new Mapping to the repository");
            LOGGER.info("Secret key for the card number value set successfully for user with ID {}", id);
            System.out.println("Secret key for the card number value set successfully for user with ID " + id);
    }

    @Override
    @Transactional
    public void saveCVVSecretKey(Long id, UserType userType, SecretKey cVVSecretKey) throws IOException {
        List<NumericDataMapping> sameIDMaps = new ArrayList<>();
        Map<Long, Map<UserType, Map<Boolean, SecretKey>>> level1Map = new HashMap<>();
        Map<UserType, Map<Boolean, SecretKey>> level2Map = new HashMap<>();
        Map<Boolean, SecretKey> level3Map = new HashMap<>();
        System.out.println("   ---   saveCVVSecretKey method: created a new 3-level Map   --- ");
        try {
            int count = getAllMappings().size();
            System.out.println("   ---   At this point the repository contains " + count + " entry");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error accessing repository: " + e.getMessage());
        }
        System.out.println("   ---   Before calling repository.findAll() --- ");
        List<NumericDataMapping> allMappings = repository.findAll();
        System.out.println("   ---   After calling repository.findAll(), size: " + allMappings.size() + " --- ");
        for (NumericDataMapping map : allMappings) {
            if (map.getMap().containsKey(id)) sameIDMaps.add(map);
        }
        System.out.println("   ---   saveCVVSecretKey method: browsed through the repository   --- ");
        if (sameIDMaps.isEmpty()) {
            System.out.println("   ---   saveCVVSecretKey method: OPTION TAKEN: sameIDMaps is empty   --- ");
            level3Map.put(false, cVVSecretKey);
            level2Map.put(userType, level3Map);
            level1Map.put(id, level2Map);
            NumericDataMapping mapping = new NumericDataMapping();
            mapping.setMap(level1Map);
            repository.save(mapping);
            System.out.println("   ---   After repository.save(mapping) --- ");
            if (cardDataSaverService.writeNDMToFile(mapping)) {
                System.out.println("   ---   Attempting to delete the NumericDataMapping after saving it to the file   --- ");
                System.out.println("   ---   saveCVVSecretKey method: deleted the processed NumericDataMapping   --- ");
                LOGGER.info("Secret key for the CVV code set successfully for user with ID {}", id);
                System.out.println("Secret key for the CVV code set successfully for user with ID " + id);
            }
        } else if (sameIDMaps.size() == 1) {
            System.out.println("   ---   saveCVVSecretKey method: OPTION TAKEN: sameIDMaps has 1 entity   --- ");
            NumericDataMapping existingMapping = sameIDMaps.get(0);
            System.out.println("   ---   saveCVVSecretKey method: Fetched the sole existing entity from sameIDMaps   --- ");
            Map<UserType, Map<Boolean, SecretKey>> secondMap = existingMapping.getMap().get(id);
            if (secondMap.containsKey(userType)) {
                Map<Boolean, SecretKey> thirdMap = secondMap.get(userType);
                if (thirdMap.containsKey(false)) {
                    LOGGER.info("A secret key for the CVV code already exists for user with ID {}, please use the updateCVVSecretKey method instead.", id);
                    System.out.println("A secret key for the CVV code value already exists for user with ID " + id + ", please use the updateCVVSecretKey method instead.");
                } else {
                    thirdMap.put(false, cVVSecretKey);
                    secondMap.put(userType, thirdMap);
                    existingMapping.getMap().put(id, secondMap);
                    repository.save(existingMapping);
                    Long mappingId = existingMapping.getId();
                    if (cardDataSaverService.writeNDMToFile(existingMapping)) {
                        System.out.println("   ---   Attempting to delete the NumericDataMapping after saving it to the file   --- ");
                        LOGGER.info("Secret key for the CVV code set successfully for user with ID {}", id);
                        System.out.println("Secret key for the CVV code set successfully for user with ID " + id);
                    }
                    LOGGER.info("Secret key for the CVV code set successfully for user with ID {}", id);
                    System.out.println("Secret key for the CVV code set successfully for user with ID " + id);
                }
            } else {
                level3Map.put(false, cVVSecretKey);
                secondMap.put(userType, level3Map);
                existingMapping.getMap().put(id, secondMap);
                repository.save(existingMapping);
                LOGGER.warn("Secret key for the CVV code set successfully for user with ID {}", id);
                System.out.println("Secret key for the CVV code set successfully for user with ID " + id);
            }
        } else if (sameIDMaps.size() == 2) {
            System.out.println("   ---   saveCVVSecretKey method: OPTION TAKEN: sameIDMaps has 2 entities   --- ");
            LOGGER.info("A secret key for the CVV code already exists for user with ID {}, please use the updateCVVSecretKey method instead.", id);
            System.out.println("A secret key for the CVV code value already exists for user with ID " + id + ", please use the updateCVVSecretKey method instead.");
        } else {
            System.out.println("   ---   saveCVVSecretKey method: OPTION TAKEN: Error in the retrieval logic   --- ");
            LOGGER.error("Error in the retrieval logic: cannot have more than two Maps for the same user ID");
            System.out.println("Error in the retrieval logic: cannot have more than two Maps for the same user ID");
        }
        System.out.println("   ---   saveCVVSecretKey method has COMPLETED ITS OPERATION   --- ");
    }



    @Override
    public void deleteMapsByUserID(Long id) {
        List<Long> mappingsForDeletion = new ArrayList<>();
        for (NumericDataMapping mapping : repository.findAll()) {
            if (mapping.getMap().containsKey(id)) mappingsForDeletion.add(mapping.getId());
        }
        if (mappingsForDeletion.isEmpty()) {
            LOGGER.info("No mappings found for user ID {}", id);
            System.out.println("No mappings found for user ID " + id);
        } else {
            for (Long mappingId : mappingsForDeletion) {
                repository.deleteById(mappingId);
            }
            LOGGER.info("Deleted {} mappings for user ID {}", mappingsForDeletion.size(), id);
            System.out.println("Deleted " + mappingsForDeletion.size() + " mappings for user ID " + id);
        }
    }

    @Override
    public void updateCardNumberSecretKey(Long id, UserType userType, SecretKey cardNumberSecretKey) throws IOException {
        List<NumericDataMapping> sameIDMaps = new ArrayList<>();
        for (NumericDataMapping map : repository.findAll()) {
            if (map.getMap().containsKey(id)) sameIDMaps.add(map);
        }
        if (sameIDMaps.isEmpty()) {
            LOGGER.info("No secret key for the card number value exists for user with ID {}, please use the saveCardNumberSecretKey method instead.", id);
            System.out.println("No secret key for the card number value exists for user with ID " + id + ", please use the saveCardNumberSecretKey method instead.");
            return;
        }
        if (sameIDMaps.size() == 1) {
            NumericDataMapping existingMapping = sameIDMaps.get(0);
            Map<UserType, Map<Boolean, SecretKey>> level2Map = existingMapping.getMap().get(id);
            if (level2Map.containsKey(userType)) {
                Map<Boolean, SecretKey> level3Map = level2Map.get(userType);
                if (level3Map.containsKey(true)) {
                    level3Map.put(true, cardNumberSecretKey);
                    level2Map.put(userType, level3Map);
                    existingMapping.getMap().put(id, level2Map);
                    repository.save(existingMapping);
                    LOGGER.info("Secret key for the card number value successfully updated for user with ID {}", id);
                    System.out.println("Secret key for the card number value successfully updated for user with ID " + id);
                } else {
                    LOGGER.info("No secret key for the card number value exists for user with ID {}, please use the saveCardNumberSecretKey method instead.", id);
                    System.out.println("No secret key for the card number value exists for user with ID " + id + ", please use the saveCardNumberSecretKey method instead.");
                }
            } else {
                LOGGER.info("No secret key for the card number value exists for user with ID {}, please use the saveCardNumberSecretKey method instead.", id);
                System.out.println("No secret key for the card number value exists for user with ID " + id + ", please use the saveCardNumberSecretKey method instead.");
            }
        }
        else if (sameIDMaps.size() == 2) {
            NumericDataMapping existingMapping = sameIDMaps.get(0);
            Map<UserType, Map<Boolean, SecretKey>> level2Map = existingMapping.getMap().get(id);
            if (level2Map.containsKey(userType)) {
                Map<Boolean, SecretKey> level3Map = level2Map.get(userType);
                level3Map.put(true, cardNumberSecretKey);
                level2Map.put(userType, level3Map);
                existingMapping.getMap().put(id, level2Map);
                repository.save(existingMapping);
                LOGGER.info("Secret key for the card number value successfully updated for user with ID {}", id);
                System.out.println("Secret key for the card number value successfully updated for user with ID " + id);
            } else {
                LOGGER.info("No secret key for the card number value exists for user with ID {}, please use the saveCardNumberSecretKey method instead.", id);
                System.out.println("No secret key for the card number value exists for user with ID " + id + ", please use the saveCardNumberSecretKey method instead.");
            }
        }
        else {
            LOGGER.error("Error in the retrieval logic: cannot have more than two Maps for the same user ID");
            System.out.println("Error in the retrieval logic: cannot have more than two Maps for the same user ID");
        }
    }

    @Override
    public void updateCVVSecretKey(Long id, UserType userType, SecretKey cVVSecretKey) throws IOException {
        List<NumericDataMapping> sameIDMaps = new ArrayList<>();
        for (NumericDataMapping map : repository.findAll()) {
            if (map.getMap().containsKey(id)) sameIDMaps.add(map);
        }
        if (sameIDMaps.isEmpty()) {
            LOGGER.info("No secret key for the CVV code exists for user with ID {}, please use the saveCardNumberSecretKey method instead.", id);
            System.out.println("No secret key for the CVV code exists for user with ID " + id + ", please use the saveCardNumberSecretKey method instead.");
            return;
        }
        if (sameIDMaps.size() == 1) {
            NumericDataMapping existingMapping = sameIDMaps.get(0);
            Map<UserType, Map<Boolean, SecretKey>> level2Map = existingMapping.getMap().get(id);
            if (level2Map.containsKey(userType)) {
                Map<Boolean, SecretKey> level3Map = level2Map.get(userType);
                if (level3Map.containsKey(false)) {
                    level3Map.put(false, cVVSecretKey);
                    level2Map.put(userType, level3Map);
                    existingMapping.getMap().put(id, level2Map);
                    repository.save(existingMapping);
                    Long mappingId = existingMapping.getId();
                    if (cardDataSaverService.writeNDMToFile(existingMapping)) {
//                        deleteNumericDataMappingById(mappingId);
                        LOGGER.info("Secret key for the CVV code successfully updated for user with ID {}", id);
                        System.out.println("Secret key for the CVV code value successfully updated for user with ID " + id);
                    }
                } else {
                    LOGGER.info("No secret key for the CVV code exists for user with ID {}, please use the saveCardNumberSecretKey method instead.", id);
                    System.out.println("No secret key for the CVV code value exists for user with ID " + id + ", please use the saveCardNumberSecretKey method instead.");
                }
            } else {
                LOGGER.info("No secret key for the CVV code exists for user with ID {}, please use the saveCardNumberSecretKey method instead.", id);
                System.out.println("No secret key for the CVV code value exists for user with ID " + id + ", please use the saveCardNumberSecretKey method instead.");
            }
        }
        else if (sameIDMaps.size() == 2) {
            NumericDataMapping existingMapping = sameIDMaps.get(0);
            Map<UserType, Map<Boolean, SecretKey>> level2Map = existingMapping.getMap().get(id);
            if (level2Map.containsKey(userType)) {
                Map<Boolean, SecretKey> level3Map = level2Map.get(userType);
                level3Map.put(false, cVVSecretKey);
                level2Map.put(userType, level3Map);
                existingMapping.getMap().put(id, level2Map);
                repository.save(existingMapping);
                Long mappingId = existingMapping.getId();
                if (cardDataSaverService.writeNDMToFile(existingMapping)) {
//                    deleteNumericDataMappingById(mappingId);
                    LOGGER.info("Secret key for the CVV code successfully updated for user with ID {}", id);
                    System.out.println("Secret key for the CVV code value successfully updated for user with ID " + id);
                }
            } else {
                LOGGER.info("No secret key for the CVV code exists for user with ID {}, please use the saveCardNumberSecretKey method instead.", id);
                System.out.println("No secret key for the CVV code exists for user with ID " + id + ", please use the saveCardNumberSecretKey method instead.");
            }
        }
        else {
            LOGGER.error("Error in the retrieval logic: cannot have more than two Maps for the same user ID");
            System.out.println("Error in the retrieval logic: cannot have more than two Maps for the same user ID");
        }
    }

    @Override
    public void deleteNumericDataMappingById(Long id) {
        repository.deleteById(id);
    }

    @Override
    public void flushEmAll() {
        repository.deleteAll();
    }

//    //AUXILIARY
//    public void saveMappingToFile(NumericDataMapping mapping) {
//
//    }
}
