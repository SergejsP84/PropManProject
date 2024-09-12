package lv.emendatus.Destiny_PropMan.service.implementation;

import lv.emendatus.Destiny_PropMan.domain.entity.Admin;
import lv.emendatus.Destiny_PropMan.domain.entity.Manager;
import lv.emendatus.Destiny_PropMan.domain.entity.Tenant;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.UserType;
import lv.emendatus.Destiny_PropMan.domain.tokens.PasswordResetToken;
import lv.emendatus.Destiny_PropMan.exceptions.EntityNotFoundException;
import lv.emendatus.Destiny_PropMan.exceptions.InvalidPasswordFormatException;
import lv.emendatus.Destiny_PropMan.exceptions.PasswordMismatchException;
import lv.emendatus.Destiny_PropMan.exceptions.TokenExpiredOrNotFoundException;
import lv.emendatus.Destiny_PropMan.service.interfaces.PasswordService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class JpaPasswordService implements PasswordService {
    private final JpaTenantService tenantService;
    private final JpaManagerService managerService;
    private final JpaAdminAccountsService adminAccountsService;
    private final JpaTokenService tokenService;
    private final JpaEmailService emailService;
    private final JpaPasswordResetTokenService passwordResetTokenService;

    private final BCryptPasswordEncoder passwordEncoder;
    private final Logger LOGGER = LogManager.getLogger(JpaPropertyService.class);
    public JpaPasswordService(JpaTenantService tenantService, JpaManagerService managerService, JpaAdminAccountsService adminAccountsService, JpaTokenService tokenService, JpaEmailService emailService, JpaPasswordResetTokenService passwordResetTokenService, BCryptPasswordEncoder passwordEncoder) {
        this.tenantService = tenantService;
        this.managerService = managerService;
        this.adminAccountsService = adminAccountsService;
        this.tokenService = tokenService;
        this.emailService = emailService;
        this.passwordResetTokenService = passwordResetTokenService;
        this.passwordEncoder = passwordEncoder;
    }

    @Value("${PROPMAN_PLATFORM_NAME}")
    private String platformName;

    @Override
    @Transactional
    public void changePassword(String login, UserType userType, String newPassword, String reEnterNewPassword, Principal principal) {
        String authenticatedUserName = principal.getName();
        if (authenticatedUserName.equals(login)) {
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
            } else if (userType.equals(UserType.ADMIN)) {
                Optional<Admin> optionalAdmin = adminAccountsService.findByLogin(login);
                if (optionalAdmin.isPresent()) {
                    Admin admin = optionalAdmin.get();
                    admin.setPassword(encodedPassword);
                    adminAccountsService.addAdmin(admin);
                } else {
                    throw new EntityNotFoundException("Could not find the sought admin");
                }
            }
        } else {
            throw new AccessDeniedException("Cannot change other users' passwords.");
        }
    }
    @Override
    @Transactional
    public void resetPassword(String email, UserType userType, String newPassword, String reEnterNewPassword) {
        if (!validatePassword(newPassword)) {
            LOGGER.warn("Invalid password format");
            throw new InvalidPasswordFormatException("Invalid password format");
        } else if (!newPassword.equals(reEnterNewPassword)) {
            LOGGER.warn("Entered passwords do not match!");
            throw new PasswordMismatchException("Entered passwords do not match!");
        }
        String confirmationToken = tokenService.generateToken();
        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(5);
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(confirmationToken);
        resetToken.setEmail(email);
        resetToken.setCreationTime(LocalDateTime.now());
        resetToken.setUserType(userType);
        passwordResetTokenService.addPasswordResetToken(resetToken);
        if (userType.equals(UserType.TENANT)) {
            Tenant tenant = tenantService.getTenantByEmail(email);
            tenant.setConfirmationToken(confirmationToken);
            tenant.setExpirationTime(expirationTime);
            tenant.setTemporaryPassword(passwordEncoder.encode(newPassword));
            tenantService.addTenant(tenant);
        } else if (userType.equals(UserType.MANAGER)) {
            Manager manager = managerService.getManagerByEmail(email);
            manager.setConfirmationToken(confirmationToken);
            manager.setExpirationTime(expirationTime);
            manager.setTemporaryPassword(passwordEncoder.encode(newPassword));
            managerService.addManager(manager);
        }
        String resetLink = "http://localhost:8080/password/complete-reset?token=" + confirmationToken;
        String emailBody = "To reset your password, click the following link: " + resetLink;
        try {
            emailService.sendEmail(email, "Password Reset Request at " + platformName, emailBody);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    public void completePasswordReset(String token) {
        PasswordResetToken passwordResetToken = passwordResetTokenService.findByToken(token);
        if (passwordResetToken == null || passwordResetTokenService.isExpired(passwordResetToken)) {
            LOGGER.warn("Token missing or expired!");
            throw new TokenExpiredOrNotFoundException("Password reset token is missing or expired.");
        }
        UserType userType = passwordResetToken.getUserType();
        String email = passwordResetToken.getEmail();
        if (userType.equals(UserType.TENANT)) {
            Tenant tenant = tenantService.getTenantByEmail(email);
            String encodedPassword = tenant.getTemporaryPassword();
            tenant.setPassword(encodedPassword);
            tenant.setTemporaryPassword(null);
            tenantService.addTenant(tenant);
            LOGGER.info("Password changed successfully!");
        } else if (userType.equals(UserType.MANAGER)) {
            Manager manager = managerService.getManagerByEmail(email);
            String encodedPassword = manager.getTemporaryPassword();
            manager.setPassword(encodedPassword);
            manager.setTemporaryPassword(null);
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
