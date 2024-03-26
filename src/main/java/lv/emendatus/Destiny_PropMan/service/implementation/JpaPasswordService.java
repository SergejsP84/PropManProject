package lv.emendatus.Destiny_PropMan.service.implementation;

import lv.emendatus.Destiny_PropMan.domain.entity.Manager;
import lv.emendatus.Destiny_PropMan.domain.entity.Tenant;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.UserType;
import lv.emendatus.Destiny_PropMan.service.interfaces.PasswordService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class JpaPasswordService implements PasswordService {
    private final JpaTenantService tenantService;
    private final JpaManagerService managerService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final Logger LOGGER = LogManager.getLogger(JpaPropertyService.class);
    public JpaPasswordService(JpaTenantService tenantService, JpaManagerService managerService, BCryptPasswordEncoder passwordEncoder) {
        this.tenantService = tenantService;
        this.managerService = managerService;
        this.passwordEncoder = passwordEncoder;
    }
    @Override
    public void changePassword(String login, UserType userType, String newPassword, String reEnterNewPassword) {
        String encodedPassword = passwordEncoder.encode(newPassword);
        if (!validatePassword(newPassword)) {
            LOGGER.warn("Invalid password format");
            // TODO: Throw a custom exception for invalid password format
            return;
        } else if (!newPassword.equals(reEnterNewPassword)) {
            LOGGER.warn("Entered passwords do not match!");
            // TODO: make a PasswordsNotMatch exception for this situation
        } else if (userType.equals(UserType.TENANT)) {
            Tenant tenant = tenantService.getTenantByLogin(login);
            tenant.setPassword(encodedPassword);
            tenantService.addTenant(tenant);
        } else if (userType.equals(UserType.MANAGER)) {
            Manager manager = managerService.getManagerByLogin(login);
            manager.setPassword(encodedPassword);
            managerService.addManager(manager);
        }
    }
    @Override
    public void resetPassword(String email, UserType userType, String newPassword, String reEnterNewPassword) {
        String encodedPassword = passwordEncoder.encode(newPassword);
        if (!validatePassword(newPassword)) {
            LOGGER.warn("Invalid password format");
            // TODO: Throw a custom exception for invalid password format
            return;
        } else if (!newPassword.equals(reEnterNewPassword)) {
            LOGGER.warn("Entered passwords do not match!");
            // TODO: make a PasswordsNotMatch exception for this situation
        } else if (userType.equals(UserType.TENANT)) {
            Tenant tenant = tenantService.getTenantByEmail(email);
            tenant.setPassword(encodedPassword);
            tenantService.addTenant(tenant);
        } else if (userType.equals(UserType.MANAGER)) {
            Manager manager = managerService.getManagerByEmail(email);
            manager.setPassword(encodedPassword);
            managerService.addManager(manager);
        }
    }

    // AUXILIARY METHOD
    private boolean validatePassword(String password) {
        if (password.length() < 8) {
            return false;
        }
        return password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$");
    }
}
