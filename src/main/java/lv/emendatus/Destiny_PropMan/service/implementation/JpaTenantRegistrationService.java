package lv.emendatus.Destiny_PropMan.service.implementation;


import lv.emendatus.Destiny_PropMan.domain.dto.profile.CardUpdateDTO;
import lv.emendatus.Destiny_PropMan.domain.dto.registration.TenantRegistrationDTO;
import lv.emendatus.Destiny_PropMan.domain.entity.NumericalConfig;
import lv.emendatus.Destiny_PropMan.domain.entity.Tenant;
import lv.emendatus.Destiny_PropMan.domain.entity.TokenResetter;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.UserType;
import lv.emendatus.Destiny_PropMan.exceptions.*;
import lv.emendatus.Destiny_PropMan.service.interfaces.TenantRegistrationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.*;
import javax.mail.MessagingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class JpaTenantRegistrationService implements TenantRegistrationService {
    private final JpaTenantService tenantService;
    private final JpaManagerService managerService;
    private final JpaTokenService tokenService;
    private final JpaEmailService emailService;
    private final JpaTokenResetService resetService;
    private final JpaCurrencyService currencyService;
    private final JpaAdminAccountsService adminAccountsService;
    public final JpaNumericDataMappingService numericDataMappingService;
    public final JpaNumericalConfigService configService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final Logger LOGGER = LogManager.getLogger(JpaTenantRegistrationService.class);
    private static final String AES_SECRET_KEY = System.getenv("AES_SECRET_KEY");
    public JpaTenantRegistrationService(JpaTenantService tenantService, JpaManagerService managerService, JpaTokenService tokenService, JpaEmailService emailService, JpaTokenResetService resetService, JpaCurrencyService currencyService, JpaAdminAccountsService adminAccountsService, JpaNumericDataMappingService numericDataMappingService, JpaNumericalConfigService configService, BCryptPasswordEncoder passwordEncoder) {
        this.tenantService = tenantService;
        this.managerService = managerService;
        this.tokenService = tokenService;
        this.emailService = emailService;
        this.resetService = resetService;
        this.currencyService = currencyService;
        this.adminAccountsService = adminAccountsService;
        this.numericDataMappingService = numericDataMappingService;
        this.configService = configService;
        this.passwordEncoder = passwordEncoder;
    }
    @Value("${PROPMAN_PLATFORM_NAME}")
    private String platformName;
    @Value("${PROPMAN_MAIL_USERNAME}")
    private String platformMail;
    @Override
    @Transactional
    public void registerTenant(TenantRegistrationDTO registrationDTO) {
        if (!isValidPaymentCardNumber(registrationDTO.getPaymentCardNo())) {
            LOGGER.error("Invalid payment card number for tenant registration");
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
            LOGGER.error("A tenant with this e-mail has already been registered");
            throw new EmailAlreadyExistsException("Tenant with this e-mail exists");
        }
        Tenant tenant = new Tenant();
        tenant.setFirstName(registrationDTO.getFirstName());
        tenant.setLastName(registrationDTO.getLastName());
        tenant.setPhone(registrationDTO.getPhone());
        tenant.setEmail(registrationDTO.getEmail());
        tenant.setIban(registrationDTO.getIban());
        tenant.setLogin(registrationDTO.getLogin());
        try {
        String encodedPassword = passwordEncoder.encode(registrationDTO.getPassword());
        tenant.setPassword(encodedPassword);
        } catch (Exception e) {
            throw new InvalidPasswordFormatException("Invalid password format!");
        };
        tenant.setRating(0F);
        tenant.setActive(false);
        tenant.setTenantPayments(new HashSet<>());
        tenant.setCurrentProperty(null);
        tenant.setLeasingHistories(new ArrayList<>());
        Optional<NumericalConfig> optionalConfig = configService.getNumericalConfigByName("LastRegisteredTenantID");
        Long tenantId = 1L;
        if (optionalConfig.isPresent()) {
            NumericalConfig config = optionalConfig.get();
            tenantId = config.getValue().longValue() + 1;
            Long configId = config.getId();
            config.setValue(Double.valueOf(tenantId));
            configService.updateNumericalConfig(configId, config);
            System.out.println("Set the new Tenant ID to " + tenantId);
        }
        try {
            tenant.setPaymentCardNo(encryptCardNumber(tenantId, UserType.TENANT, registrationDTO.getPaymentCardNo()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        tenant.setCardValidityDate(registrationDTO.getCardValidityDate());
        try {
            tenant.setCvv(encryptCVV(tenantId, UserType.TENANT, registrationDTO.getCvv()).toCharArray());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        List<String> knownIPs = new ArrayList<>();
        tenant.setKnownIps(knownIPs);
        if (registrationDTO.getPreferredCurrency() != null) {
            tenant.setPreferredCurrency(registrationDTO.getPreferredCurrency());
        } else {
            tenant.setPreferredCurrency(currencyService.returnBaseCurrency());
        }
        String confirmationToken = tokenService.generateToken();
        tenant.setConfirmationToken(confirmationToken);
        tenant.setExpirationTime(LocalDateTime.now().plusMinutes(5));
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("TENANT"));
        tenant.setAuthorities(authorities);
        tenantService.addTenant(tenant);
        LOGGER.info("New tenant added: ID" + tenant.getId() + ", First name / surname: " + tenant.getFirstName() + " " + tenant.getLastName());
        try {
            emailService.sendEmail(registrationDTO.getEmail(), "E-mail confirmation link for " + platformName, createConfirmationEmailBody(registrationDTO.getFirstName(), registrationDTO.getLastName(), confirmationToken));
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        TokenResetter resetter = new TokenResetter();
        resetter.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        resetter.setUserId(tenant.getId());
        resetter.setUserType(UserType.TENANT);
        resetService.addResetter(resetter);
    }

    // REDUNDANT - NOT USED IN THE FINAL SETUP
    @Override
    @PreAuthorize("hasAuthority('TENANT')")
    @Transactional
    public void updateTenantPaymentCard(CardUpdateDTO dto) {
        if (dto.getUserType() != UserType.TENANT) {
            throw new IllegalArgumentException("User type must be TENANT.");
        }
        Tenant tenant = tenantService.getTenantById(dto.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("Tenant not found with ID: " + dto.getUserId()));
        try {
            tenant.setPaymentCardNo(encryptCardNumber(tenant.getId(), UserType.TENANT, dto.getNewCardNumber()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        tenant.setCardValidityDate(dto.getNewValidityDate());
        try {
            tenant.setCvv(encryptCVV(tenant.getId(), UserType.TENANT, dto.getNewCvv()).toCharArray());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        tenantService.addTenant(tenant);
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
        return !(tenantService.getTenantByEmail(email) == null);
    }

    public String createConfirmationEmailBody(String firstName, String lastName, String confirmationLink) {
        String greeting = "Hello " + firstName + " " + lastName + ",\n\n";
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
            // implement logics for saving the secret key alongside the encrypted value
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
