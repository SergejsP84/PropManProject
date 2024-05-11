package lv.emendatus.Destiny_PropMan.service.implementation;


import lv.emendatus.Destiny_PropMan.domain.dto.profile.CardUpdateDTO;
import lv.emendatus.Destiny_PropMan.domain.dto.registration.TenantRegistrationDTO;
import lv.emendatus.Destiny_PropMan.domain.entity.Tenant;
import lv.emendatus.Destiny_PropMan.domain.entity.TokenResetter;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.UserType;
import lv.emendatus.Destiny_PropMan.exceptions.*;
import lv.emendatus.Destiny_PropMan.service.interfaces.TenantRegistrationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.mail.MessagingException;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;

@Service
public class JpaTenantRegistrationService implements TenantRegistrationService {
    private final JpaTenantService tenantService;
    private final JpaManagerService managerService;
    private final JpaTokenService tokenService;
    private final JpaEmailService emailService;
    private final JpaTokenResetService resetService;
    private final JpaCurrencyService currencyService;
    private final JpaAdminAccountsService adminAccountsService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final Logger LOGGER = LogManager.getLogger(JpaTenantRegistrationService.class);
    private static final String AES_SECRET_KEY = System.getenv("AES_SECRET_KEY");
    public JpaTenantRegistrationService(JpaTenantService tenantService, JpaManagerService managerService, JpaTokenService tokenService, JpaEmailService emailService, JpaTokenResetService resetService, JpaCurrencyService currencyService, JpaAdminAccountsService adminAccountsService, BCryptPasswordEncoder passwordEncoder) {
        this.tenantService = tenantService;
        this.managerService = managerService;
        this.tokenService = tokenService;
        this.emailService = emailService;
        this.resetService = resetService;
        this.currencyService = currencyService;
        this.adminAccountsService = adminAccountsService;
        this.passwordEncoder = passwordEncoder;
    }
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
        String encodedPassword = passwordEncoder.encode(registrationDTO.getPassword());
        tenant.setPassword(encodedPassword);
        tenant.setRating(0F);
        tenant.setActive(false);
        tenant.setTenantPayments(new HashSet<>());
        tenant.setCurrentProperty(null);
        tenant.setLeasingHistories(new ArrayList<>());
        tenant.setPaymentCardNo(registrationDTO.getPaymentCardNo());
        tenant.setCardValidityDate(registrationDTO.getCardValidityDate());
        try {
            tenant.setCvv(encryptCVV(registrationDTO.getCvv()).toCharArray());
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
        tenantService.addTenant(tenant);
        LOGGER.info("New tenant added: ID" + tenant.getId() + ", First name / surname: " + tenant.getFirstName() + " " + tenant.getLastName());
        try {
            emailService.sendEmail(registrationDTO.getEmail(), "E-mail confirmation link for", createConfirmationEmailBody(registrationDTO.getFirstName(), registrationDTO.getLastName(), confirmationToken));
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        TokenResetter resetter = new TokenResetter();
        resetter.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        resetter.setUserId(tenant.getId());
        resetter.setUserType(UserType.TENANT);
        resetService.addResetter(resetter);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_TENANT') and #dto.userId == principal.id")
    @Transactional
    public void updateTenantPaymentCard(CardUpdateDTO dto) {
        if (dto.getUserType() != UserType.TENANT) {
            throw new IllegalArgumentException("User type must be TENANT.");
        }
        Tenant tenant = tenantService.getTenantById(dto.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("Tenant not found with ID: " + dto.getUserId()));
        tenant.setPaymentCardNo(dto.getNewCardNumber());
        tenant.setCardValidityDate(dto.getNewValidityDate());
        try {
            String encryptedCvv = encryptCVV(dto.getNewCvv());
            tenant.setCvv(encryptedCvv.toCharArray());
        } catch (Exception e) {
            throw new RuntimeException("Error encrypting CVV.", e);
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
        return tenantService.getTenantByLogin(login) == null && managerService.getManagerByLogin(login) == null && adminAccountsService.findByLogin(login).isEmpty();
    }
    private boolean isEmailBusy(String email) {
        return tenantService.getTenantByEmail(email) == null;
    }

    public String createConfirmationEmailBody(String firstName, String lastName, String confirmationLink) {
        String greeting = "Hello " + firstName + " " + lastName + ",\n\n";
        String explanation = "Thank you for registering with our service. To complete your registration and activate your account, please click the following link:\n\n";
        String link = "http://localhost:8080/confirm-registration?token=" + confirmationLink + "\n\n";
        String expiration = "The confirmation link is going to expire in five minutes; should this be the case, please request a new one.";
        String instructions = "If you have any trouble with the link, or if you did not request this registration, please contact our support team at support@example.com.\n\n";
        String closing = "Thank you for choosing our service.\n\nBest regards,\n[Your Company Name]";

        return greeting + explanation + link + expiration + instructions + closing;
    }

    // Encrypt CVV using AES encryption
    public static String encryptCVV(char[] cvv) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        SecretKeySpec secretKey = new SecretKeySpec(AES_SECRET_KEY.getBytes(StandardCharsets.UTF_8), "AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedCVVBytes = cipher.doFinal(new String(cvv).getBytes());
        return Base64.getEncoder().encodeToString(encryptedCVVBytes);
    }

    // Decrypt CVV using AES decryption
    public static String decryptCVV(String encryptedCVV) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        SecretKeySpec secretKey = new SecretKeySpec(AES_SECRET_KEY.getBytes(StandardCharsets.UTF_8), "AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decryptedCVVBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedCVV));
        return new String(decryptedCVVBytes);
    }
}
