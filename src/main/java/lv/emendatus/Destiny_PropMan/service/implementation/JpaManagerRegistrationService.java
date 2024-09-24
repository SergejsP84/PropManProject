package lv.emendatus.Destiny_PropMan.service.implementation;

import lv.emendatus.Destiny_PropMan.domain.entity.NumericalConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.transaction.annotation.Transactional;
import lv.emendatus.Destiny_PropMan.domain.dto.profile.CardUpdateDTO;
import lv.emendatus.Destiny_PropMan.domain.dto.registration.ManagerRegistrationDTO;
import lv.emendatus.Destiny_PropMan.domain.entity.Manager;
import lv.emendatus.Destiny_PropMan.domain.entity.TokenResetter;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.UserType;
import lv.emendatus.Destiny_PropMan.exceptions.*;
import lv.emendatus.Destiny_PropMan.service.interfaces.ManagerRegistrationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.*;
import javax.mail.MessagingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class JpaManagerRegistrationService implements ManagerRegistrationService {

    private final JpaManagerService managerService;
    private final JpaTenantService tenantService;
    private final JpaTokenService tokenService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JpaEmailService emailService;
    private final JpaTokenResetService resetService;
    private final JpaAdminAccountsService adminAccountsService;
    public final JpaNumericDataMappingService numericDataMappingService;
    public final JpaNumericalConfigService configService;

    private final Logger LOGGER = LogManager.getLogger(JpaTenantRegistrationService.class);

    private static final String AES_SECRET_KEY = System.getenv("AES_SECRET_KEY");

    public JpaManagerRegistrationService(JpaManagerService managerService, JpaTenantService tenantService, JpaTokenService tokenService, BCryptPasswordEncoder passwordEncoder, JpaEmailService emailService, JpaTokenResetService resetService, JpaAdminAccountsService adminAccountsService, JpaNumericDataMappingService numericDataMappingService, JpaNumericalConfigService configService) {
        this.managerService = managerService;
        this.tenantService = tenantService;
        this.tokenService = tokenService;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.resetService = resetService;
        this.adminAccountsService = adminAccountsService;
        this.numericDataMappingService = numericDataMappingService;
        this.configService = configService;
    }

    @Value("${PROPMAN_PLATFORM_NAME}")
    private String platformName;
    @Value("${PROPMAN_MAIL_USERNAME}")
    private String platformMail;

    @Override
    @Transactional
    public void registerManager(ManagerRegistrationDTO registrationDTO) {
        if (!isValidPaymentCardNumber(registrationDTO.getPaymentCardNo())) {
            LOGGER.error("Invalid payment card number for manager registration");
            throw new InvalidPaymentCardNumberException("Invalid payment card number");
        }
        if (!registrationDTO.getPassword().equals(registrationDTO.getReEnterPassword())) {
            LOGGER.error("Entered passwords do not match");
            throw new PasswordMismatchException("Password mismatch");
        }
        if (isLoginBusy(registrationDTO.getLogin())) {
            LOGGER.error("This login already exists");
            throw new LoginAlreadyExistsException("This login already exists");
        }
        if (isEmailBusy(registrationDTO.getEmail())) {
            LOGGER.error("A manager with this e-mail has already been registered");
            throw new EmailAlreadyExistsException("Manager with this e-mail exists");
        }
        Manager manager = new Manager();
        manager.setManagerName(registrationDTO.getManagerName());
        manager.setType(registrationDTO.getType());
        manager.setPhone(registrationDTO.getPhone());
        manager.setEmail(registrationDTO.getEmail());
        manager.setIban(registrationDTO.getIban());
        manager.setLogin(registrationDTO.getLogin());
        manager.setDescription(registrationDTO.getDescription());
        Optional<NumericalConfig> optionalConfig = configService.getNumericalConfigByName("LastRegisteredManagerID");
        Long managerId = 1L;
        if (optionalConfig.isPresent()) {
            NumericalConfig config = optionalConfig.get();
            managerId = config.getValue().longValue() + 1;
            Long configId = config.getId();
            config.setValue(Double.valueOf(managerId));
            configService.updateNumericalConfig(configId, config);
            System.out.println("Set the new Manager ID to " + managerId);
        }
        try {
            manager.setPaymentCardNo(encryptCardNumber(managerId, UserType.MANAGER, registrationDTO.getPaymentCardNo()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        manager.setCardValidityDate(registrationDTO.getCardValidityDate());
        try {
            manager.setCvv(encryptCVV(managerId, UserType.MANAGER, registrationDTO.getCvv()).toCharArray());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        try {
            String encodedPassword = passwordEncoder.encode(registrationDTO.getPassword());
            manager.setPassword(encodedPassword);
        } catch (Exception e) {
            throw new RuntimeException(e);
        };
        manager.setActive(false);
        manager.setProperties(new HashSet<>());
        manager.setJoinDate(Timestamp.valueOf(LocalDateTime.now()));
        List<String> knownIPs = new ArrayList<>();
        manager.setKnownIps(knownIPs);
        String confirmationToken = tokenService.generateToken();
        manager.setConfirmationToken(confirmationToken);
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("MANAGER"));
        manager.setAuthorities(authorities);
        manager.setExpirationTime(LocalDateTime.now().plusMinutes(5));
        managerService.addManager(manager);
        LOGGER.info("New manager added: ID" + manager.getId() + ", Name: " + manager.getManagerName() + ", Description: " + manager.getDescription());
        try {
            emailService.sendEmail(registrationDTO.getEmail(), "E-mail confirmation link for " + platformName, createConfirmationEmailBody(registrationDTO.getManagerName(), confirmationToken));
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        TokenResetter resetter = new TokenResetter();
        resetter.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        resetter.setUserId(manager.getId());
        resetter.setUserType(UserType.MANAGER);
        resetService.addResetter(resetter);
    }


    // REDUNDANT - NOT USED IN THE FINAL SETUP
    @Override
    @PreAuthorize("hasAuthority('MANAGER')")
    @Transactional
    public void updateManagerPaymentCard(CardUpdateDTO dto) {
        if (dto.getUserType() != UserType.MANAGER) {
            throw new IllegalArgumentException("User type must be MANAGER.");
        }
        Manager manager = managerService.getManagerById(dto.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("Manager not found with ID: " + dto.getUserId()));
        try {
            manager.setPaymentCardNo(encryptCardNumber(manager.getId(), UserType.MANAGER, dto.getNewCardNumber()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        manager.setCardValidityDate(dto.getNewValidityDate());
        try {
            manager.setCvv(encryptCVV(manager.getId(), UserType.MANAGER, dto.getNewCvv()).toCharArray());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        managerService.addManager(manager);
    }

    // AUXILIARY METHODS
    // LUHN LOGIC
    private boolean isValidPaymentCardNumber(String paymentCardNo) {
        char[] processing = paymentCardNo.toCharArray();
        List<Character> digitsOnly = new ArrayList<>();
        List<Integer> output = new ArrayList<>();
        for (char c : processing) {
            if (c == '0' || c == '1' || c == '2' || c == '3'
                    || c == '4' || c == '5' || c == '6'
                    || c == '7' || c == '8' || c == '9') {
                digitsOnly.add(c);
            }
        }
        if (digitsOnly.size() < 1) {
            System.out.println("The input contains no digits!");
            LOGGER.error("The input contains no digits!");
        } else {
            for (Character character : digitsOnly) {
                output.add(Integer.parseInt(String.valueOf(character)));
            }
        }
        if (output.size() < 1) return false;
        boolean valid = false;
        List<Integer> processedList = new ArrayList<>();
        for (int i = 0; i < output.size(); i++) {
            if (i % 2 == 0) {
                if (output.get(i) * 2 < 10) {
                    processedList.add(output.get(i) * 2);
                } else {
                    processedList.add(output.get(i) * 2 - 9);
                }
            }
            else {
                processedList.add(output.get(i));
            }
        }
        int checkSum = 0;
        for (Integer digit : processedList) {
            checkSum += digit;
        }
        if (checkSum % 10 == 0) valid = true;
        return valid;
    }

    private boolean isLoginBusy(String login) {
        return !(tenantService.getTenantByLogin(login) == null && managerService.getManagerByLogin(login) == null && adminAccountsService.findByLogin(login).isEmpty());
    }
    private boolean isEmailBusy(String email) {
        for (Manager manager : managerService.getAllManagers()) {
            if (manager.getEmail().equals(email)) return true;
        }
        return false;
    }

    public String createConfirmationEmailBody(String managerName, String confirmationLink) {
        String greeting = "Hello " + managerName + ",\n\n";
        String explanation = "Thank you for registering with our service. To complete your registration and activate your account, please click the following link:\n\n";
        String link = "http://localhost:8080/confirm-registration?token=" + confirmationLink + "\n\n";
        String expiration = "The confirmation link is going to expire in five minutes; should this be the case, please request a new one.";
        String instructions = "If you have any trouble with the link, or if you did not request this registration, please contact our support team at " + platformMail + ".\n\n";
        String closing = "Thank you for choosing our service.\n\nBest regards,\n" + platformName + " team";

        return greeting + explanation + link + expiration + instructions + closing;
    }

    // Encrypt CVV using AES encryption
    public String encryptCVV(Long userId, UserType userType, char[] cvv) throws Exception {
        try {
//            System.out.println("      ---   a) Initiated the encryptCVV method");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
//            System.out.println("      ---   b) Created a Cipher: " + cipher);
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(128); // 128-bit key length
            SecretKey secretKey = keyGenerator.generateKey();
            byte[] aesKeyBytes = secretKey.getEncoded();
//            System.out.println("      ---   c) Generated AES Key (Base64 Encoded): " + Base64.getEncoder().encodeToString(aesKeyBytes));
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
//            System.out.println("      ---   d) Cipher.init triggered successfully");
            byte[] encryptedCVVBytes = cipher.doFinal(new String(cvv).getBytes());
//            System.out.println("      ---   e) Byte array created: " + Arrays.toString(encryptedCVVBytes));
            numericDataMappingService.saveCVVSecretKey(userId, userType, secretKey);
//            System.out.println("      ---   f) Saved the secret key " + secretKey.toString() + " to the database");
            return Base64.getEncoder().encodeToString(encryptedCVVBytes);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        System.out.println("FAILED TO ENCRYPT THE CVV!");
        return Arrays.toString(cvv);
    }
    public String encryptCardNumber(Long userId, UserType userType, String cardNumber) throws Exception {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(128); // 128-bit key length
            SecretKey secretKey = keyGenerator.generateKey();
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedCardNumberBytes = cipher.doFinal(cardNumber.getBytes());
            numericDataMappingService.saveCardNumberSecretKey(userId, userType, secretKey);
            return Base64.getEncoder().encodeToString(encryptedCardNumberBytes);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException | IllegalBlockSizeException e) {
            System.out.println("Error encrypting card number: " + e.getMessage());
        }
        return null;
    }

}
