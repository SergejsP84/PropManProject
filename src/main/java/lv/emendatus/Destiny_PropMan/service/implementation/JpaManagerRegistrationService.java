package lv.emendatus.Destiny_PropMan.service.implementation;

import lv.emendatus.Destiny_PropMan.domain.dto.registration.ManagerRegistrationDTO;
import lv.emendatus.Destiny_PropMan.domain.entity.Manager;
import lv.emendatus.Destiny_PropMan.service.interfaces.ManagerRegistrationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
public class JpaManagerRegistrationService implements ManagerRegistrationService {

    private final JpaManagerService managerService;
    private final JpaTenantService tenantService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final Logger LOGGER = LogManager.getLogger(JpaTenantRegistrationService.class);

    public JpaManagerRegistrationService(JpaManagerService managerService, JpaTenantService tenantService, BCryptPasswordEncoder passwordEncoder) {
        this.managerService = managerService;
        this.tenantService = tenantService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void registerManager(ManagerRegistrationDTO registrationDTO) {
        if (!isValidPaymentCardNumber(registrationDTO.getPaymentCardNo())) {
            LOGGER.error("Invalid payment card number for manager registration");
            throw new IllegalArgumentException("Invalid payment card number");
        }
        if (!registrationDTO.getPassword().equals(registrationDTO.getReEnterPassword())) {
            LOGGER.error("Entered passwords do not match");
            // TODO: might replace with a custom exception
            throw new IllegalArgumentException("Password mismatch");
        }
        if (isLoginBusy(registrationDTO.getLogin())) {
            LOGGER.error("This login already exists");
            // TODO: might replace with a custom exception
            throw new IllegalArgumentException("Password mismatch");
        }
        if (isEmailBusy(registrationDTO.getEmail())) {
            LOGGER.error("A manager with this e-mail gas already been registered");
            // TODO: might replace with a custom exception
            throw new IllegalArgumentException("Manager with this e-mail exists");
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
        manager.setActive(true);
        manager.setProperties(new HashSet<>());
        manager.setJoinDate(Timestamp.valueOf(LocalDateTime.now()));
        managerService.addManager(manager);
        LOGGER.info("New manager added: ID" + manager.getId() + ", Name: " + manager.getManagerName() + ", Description: " + manager.getDescription());
    }

    // AUXILIARY METHOD - LUHN LOGIC
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
}
