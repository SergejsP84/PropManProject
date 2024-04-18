package lv.emendatus.Destiny_PropMan.service.implementation;

import lv.emendatus.Destiny_PropMan.domain.dto.registration.TenantRegistrationDTO;
import lv.emendatus.Destiny_PropMan.domain.entity.Tenant;
import lv.emendatus.Destiny_PropMan.domain.entity.TokenResetter;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.UserType;
import lv.emendatus.Destiny_PropMan.exceptions.*;
import lv.emendatus.Destiny_PropMan.service.interfaces.TenantRegistrationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
public class JpaTenantRegistrationService implements TenantRegistrationService {
    private final JpaTenantService tenantService;
    private final JpaManagerService managerService;
    private final JpaTokenService tokenService;
    private final JpaEmailService emailService;
    private final JpaTokenResetService resetService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final Logger LOGGER = LogManager.getLogger(JpaTenantRegistrationService.class);
    public JpaTenantRegistrationService(JpaTenantService tenantService, JpaManagerService managerService, JpaTokenService tokenService, JpaEmailService emailService, JpaTokenResetService resetService, BCryptPasswordEncoder passwordEncoder) {
        this.tenantService = tenantService;
        this.managerService = managerService;
        this.tokenService = tokenService;
        this.emailService = emailService;
        this.resetService = resetService;
        this.passwordEncoder = passwordEncoder;
    }
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
        List<String> knownIPs = new ArrayList<>();
        tenant.setKnownIps(knownIPs);
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
        return tenantService.getTenantByLogin(login) == null && managerService.getManagerByLogin(login) == null;
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

}
