package lv.emendatus.Destiny_PropMan.service.implementation;

import lv.emendatus.Destiny_PropMan.domain.dto.registration.ManagerRegistrationDTO;
import lv.emendatus.Destiny_PropMan.domain.entity.Manager;
import lv.emendatus.Destiny_PropMan.domain.entity.TokenResetter;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.UserType;
import lv.emendatus.Destiny_PropMan.exceptions.EmailAlreadyExistsException;
import lv.emendatus.Destiny_PropMan.exceptions.InvalidPaymentCardNumberException;
import lv.emendatus.Destiny_PropMan.exceptions.LoginAlreadyExistsException;
import lv.emendatus.Destiny_PropMan.exceptions.PasswordMismatchException;
import lv.emendatus.Destiny_PropMan.service.interfaces.ManagerRegistrationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.security.auth.login.LoginException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
public class JpaManagerRegistrationService implements ManagerRegistrationService {

    private final JpaManagerService managerService;
    private final JpaTenantService tenantService;
    private final JpaTokenService tokenService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JpaEmailService emailService;
    private final JpaTokenResetService resetService;
    private final Logger LOGGER = LogManager.getLogger(JpaTenantRegistrationService.class);

    public JpaManagerRegistrationService(JpaManagerService managerService, JpaTenantService tenantService, JpaTokenService tokenService, BCryptPasswordEncoder passwordEncoder, JpaEmailService emailService, JpaTokenResetService resetService) {
        this.managerService = managerService;
        this.tenantService = tenantService;
        this.tokenService = tokenService;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.resetService = resetService;
    }

    @Override
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
        manager.setPaymentCardNo(registrationDTO.getPaymentCardNo());
        String encodedPassword = passwordEncoder.encode(registrationDTO.getPassword());
        manager.setPassword(encodedPassword);
        manager.setActive(false);
        manager.setProperties(new HashSet<>());
        manager.setJoinDate(Timestamp.valueOf(LocalDateTime.now()));
        List<String> knownIPs = new ArrayList<>();
        manager.setKnownIps(knownIPs);
        String confirmationToken = tokenService.generateToken();
        manager.setConfirmationToken(confirmationToken);
        managerService.addManager(manager);
        LOGGER.info("New manager added: ID" + manager.getId() + ", Name: " + manager.getManagerName() + ", Description: " + manager.getDescription());
        try {
            emailService.sendEmail(registrationDTO.getEmail(), "E-mail confirmation link for", createConfirmationEmailBody(registrationDTO.getManagerName(), confirmationToken));
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        TokenResetter resetter = new TokenResetter();
        resetter.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        resetter.setUserId(manager.getId());
        resetter.setUserType(UserType.MANAGER);
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
        return managerService.getManagerByEmail(email) == null;
    }

    public String createConfirmationEmailBody(String managerName, String confirmationLink) {
        String greeting = "Hello " + managerName + ",\n\n";
        String explanation = "Thank you for registering with our service. To complete your registration and activate your account, please click the following link:\n\n";
        String link = "http://localhost:8080/confirm-registration?token=" + confirmationLink + "\n\n";
        String expiration = "The confirmation link is going to expire in five minutes; should this be the case, please request a new one.";
        String instructions = "If you have any trouble with the link, or if you did not request this registration, please contact our support team at support@example.com.\n\n";
        String closing = "Thank you for choosing our service.\n\nBest regards,\n[Your Company Name]";

        return greeting + explanation + link + expiration + instructions + closing;
    }
}
