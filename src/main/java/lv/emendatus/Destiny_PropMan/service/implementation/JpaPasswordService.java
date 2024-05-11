package lv.emendatus.Destiny_PropMan.service.implementation;

import lv.emendatus.Destiny_PropMan.domain.entity.Manager;
import lv.emendatus.Destiny_PropMan.domain.entity.Tenant;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.UserType;
import lv.emendatus.Destiny_PropMan.domain.tokens.PasswordResetToken;
import lv.emendatus.Destiny_PropMan.exceptions.InvalidPasswordFormatException;
import lv.emendatus.Destiny_PropMan.exceptions.ManagerNotFoundException;
import lv.emendatus.Destiny_PropMan.exceptions.PasswordMismatchException;
import lv.emendatus.Destiny_PropMan.exceptions.TokenExpiredOrNotFoundException;
import lv.emendatus.Destiny_PropMan.service.interfaces.PasswordService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import java.time.LocalDateTime;

@Service
public class JpaPasswordService implements PasswordService {
    private final JpaTenantService tenantService;
    private final JpaManagerService managerService;
    private final JpaTokenService tokenService;
    private final JpaEmailService emailService;
    private final JpaPasswordResetTokenService passwordResetTokenService;

    private final BCryptPasswordEncoder passwordEncoder;
    private final Logger LOGGER = LogManager.getLogger(JpaPropertyService.class);
    public JpaPasswordService(JpaTenantService tenantService, JpaManagerService managerService, JpaTokenService tokenService, JpaEmailService emailService, JpaPasswordResetTokenService passwordResetTokenService, BCryptPasswordEncoder passwordEncoder) {
        this.tenantService = tenantService;
        this.managerService = managerService;
        this.tokenService = tokenService;
        this.emailService = emailService;
        this.passwordResetTokenService = passwordResetTokenService;
        this.passwordEncoder = passwordEncoder;
    }
    @Override
    @Transactional
    public void changePassword(String login, UserType userType, String newPassword, String reEnterNewPassword) {
        String encodedPassword = passwordEncoder.encode(newPassword);
        if (!validatePassword(newPassword)) {
            LOGGER.warn("Invalid password format");
            throw new InvalidPasswordFormatException("Invalid password format");
        } else if (!newPassword.equals(reEnterNewPassword)) {
            LOGGER.warn("Entered passwords do not match!");
            throw new PasswordMismatchException("Entered passwords do not match!");
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
    @Transactional
    public void resetPassword(String email, UserType userType, String newPassword, String reEnterNewPassword) {
        String confirmationToken = tokenService.generateToken();
        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(5);
        if (userType.equals(UserType.TENANT)) {
            Tenant tenant = tenantService.getTenantByEmail(email);
            tenant.setConfirmationToken(confirmationToken);
            tenant.setExpirationTime(expirationTime);
            tenantService.addTenant(tenant);
        } else if (userType.equals(UserType.MANAGER)) {
            Manager manager = managerService.getManagerByEmail(email);
            manager.setConfirmationToken(confirmationToken);
            manager.setExpirationTime(expirationTime);
            managerService.addManager(manager);
        }
        String resetLink = "http://localhost:8080/reset-password?token=" + confirmationToken;
        String emailBody = "To reset your password, click the following link: " + resetLink;
        try {
            emailService.sendEmail(email, "Password Reset Request", emailBody);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    public void completePasswordReset(String token, String newPassword, String reEnterNewPassword) {
        if (!validatePassword(newPassword)) {
            LOGGER.warn("Invalid password format");
            throw new InvalidPasswordFormatException("Invalid password format");
        } else if (!newPassword.equals(reEnterNewPassword)) {
            LOGGER.warn("Entered passwords do not match!");
            throw new PasswordMismatchException("Entered passwords do not match!");
        }
        PasswordResetToken passwordResetToken = passwordResetTokenService.findByToken(token);
        if (passwordResetToken == null || passwordResetTokenService.isExpired(passwordResetToken)) {
            LOGGER.warn("Token missing or expired!");
            throw new TokenExpiredOrNotFoundException("Password reset token is missing or expired.");
        }
        UserType userType = passwordResetToken.getUserType();
        String email = passwordResetToken.getEmail();
        String encodedPassword = passwordEncoder.encode(newPassword);
        if (userType.equals(UserType.TENANT)) {
            Tenant tenant = tenantService.getTenantByEmail(email);
            tenant.setPassword(encodedPassword);
            tenantService.addTenant(tenant);
            LOGGER.info("Password changed successfully!");
        } else if (userType.equals(UserType.MANAGER)) {
            Manager manager = managerService.getManagerByEmail(email);
            manager.setPassword(encodedPassword);
            managerService.addManager(manager);
            LOGGER.info("Password changed successfully!");
        }
        passwordResetTokenService.deletePasswordResetTokenById(passwordResetToken.getId());
    }


    // AUXILIARY METHOD
    private boolean validatePassword(String password) {
        if (password.length() < 8) {
            return false;
        }
        return password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$");
    }

}
