package lv.emendatus.Destiny_PropMan.service.implementation;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lv.emendatus.Destiny_PropMan.domain.dto.authentication.LoginDTO;
import lv.emendatus.Destiny_PropMan.domain.entity.Admin;
import lv.emendatus.Destiny_PropMan.domain.entity.Manager;
import lv.emendatus.Destiny_PropMan.domain.entity.Tenant;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.UserRole;
import lv.emendatus.Destiny_PropMan.exceptions.AuthenticationFailedException;
import lv.emendatus.Destiny_PropMan.exceptions.EmailSendingException;
import lv.emendatus.Destiny_PropMan.service.interfaces.LoginService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.mail.MessagingException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;


@Service
public class JpaLoginService implements LoginService {
    private final JpaTenantService tenantService;
    private final JpaManagerService managerService;
    private final JpaAdminAccountsService adminService;
    private final JpaEmailService emailService;
    private final UserDetailsInnerService userDetailsService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final Logger LOGGER = LogManager.getLogger(JpaPropertyService.class);

    public JpaLoginService(JpaTenantService tenantService, JpaManagerService managerService, JpaAdminAccountsService adminService, JpaEmailService emailService, UserDetailsInnerService userDetailsService, BCryptPasswordEncoder passwordEncoder) {
        this.tenantService = tenantService;
        this.managerService = managerService;
        this.adminService = adminService;
        this.emailService = emailService;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Value("${PROPMAN_PLATFORM_NAME}")
    private String platformName;

//    @Override
//    public Tenant authenticateTenant(LoginDTO loginDTO, HttpServletRequest request) {
//        System.out.println("JpaLoginService authenticateTenant method invoked");
//        String clientIpAddress = request.getRemoteAddr();
//        Tenant tenant = tenantService.getTenantByLogin(loginDTO.getUsername());
//        if (tenant != null && passwordEncoder.matches(loginDTO.getPassword(), tenant.getPassword())) {
//            System.out.println("JpaLoginService: Tenant is NOT null, and the password is correct");
//            if (tenant.getKnownIps().contains(clientIpAddress)) {
//                System.out.println("The IP is on the Tenant's list, loading user details");
//                UserDetails userDetails = userDetailsService.loadUserByUsername(loginDTO.getUsername());
//                Collection<? extends GrantedAuthority> authorities = getAuthoritiesForUser(UserRole.TENANT);
//                tenant.setAuthorities(authorities);
//                LOGGER.info("Successful login attempt for Tenant: " + tenant.getLogin() + " at " + LocalDateTime.now());
//                return tenant;
//            } else {
//                System.out.println("The IP is NOT on the Tenant's list, initiating 2FA");
//                initiateTwoFactorAuthentication(tenant.getEmail());
//                LOGGER.info("Two-factor identification requested from Tenant: " + tenant.getLogin() + " at " + LocalDateTime.now());
//                System.out.println("New IP " + clientIpAddress + " detected for tenant " + tenant.getId());
//                return null;
//            }
//        }
//        LOGGER.warn("Failed login attempt for Tenant with username: " + loginDTO.getUsername() + " at " + LocalDateTime.now());
//        throw new AuthenticationFailedException("Authentication failed for the provided credentials");
//    }

    @Override
    public UserDetails authenticateTenant(LoginDTO loginDTO, HttpServletRequest request) {
        System.out.println("JpaLoginService authenticateTenant method invoked");
        String clientIpAddress = request.getRemoteAddr();
        Tenant tenant = tenantService.getTenantByLogin(loginDTO.getUsername());
        if (tenant != null && passwordEncoder.matches(loginDTO.getPassword(), tenant.getPassword())) {
            System.out.println("JpaLoginService: Tenant is NOT null, and the password is correct");
            if (tenant.getKnownIps().contains(clientIpAddress)) {
                System.out.println("The IP is on the Tenant's list, loading user details");
                return userDetailsService.loadUserByUsername(loginDTO.getUsername());
            } else {
                System.out.println("The IP is NOT on the Tenant's list, initiating 2FA");
                initiateTwoFactorAuthentication(tenant.getEmail(), tenant.getLogin());
                LOGGER.info("Two-factor identification requested from Tenant: " + tenant.getLogin() + " at " + LocalDateTime.now());
                return null;
            }
        }
        LOGGER.warn("Failed login attempt for Tenant with username: " + loginDTO.getUsername() + " at " + LocalDateTime.now());
        throw new AuthenticationFailedException("Authentication failed for the provided credentials");
    }

    @Override
    public UserDetails authenticateManager(LoginDTO loginDTO, HttpServletRequest request) {
        System.out.println("JpaLoginService authenticateManager method invoked");
        String clientIpAddress = request.getRemoteAddr();
        Manager manager = managerService.getManagerByLogin(loginDTO.getUsername());
        if (manager != null && passwordEncoder.matches(loginDTO.getPassword(), manager.getPassword())) {
            System.out.println("JpaLoginService: Manager is NOT null, and the password is correct");
            if (manager.getKnownIps().contains(clientIpAddress)) {
                System.out.println("The IP is on the Manager's list, loading user details");
                return userDetailsService.loadUserByUsername(loginDTO.getUsername());
            } else {
                System.out.println("The IP is NOT on the Manager's list, initiating 2FA");
                initiateTwoFactorAuthentication(manager.getEmail(), manager.getLogin());
                LOGGER.info("Two-factor identification requested from Manager: " + manager.getLogin() + " at " + LocalDateTime.now());
                System.out.println("New IP " + clientIpAddress + " detected for manager " + manager.getId());
                return null;
            }
        }
        LOGGER.warn("Failed login attempt for Manager with username: " + loginDTO.getUsername() + " at " + LocalDateTime.now());
        throw new AuthenticationFailedException("Authentication failed for the provided credentials");
    }

    @Override
    public UserDetails authenticateAdmin(LoginDTO loginDTO, HttpServletRequest request) {
        System.out.println("JpaLoginService authenticateAdmin method invoked");
        String clientIpAddress = request.getRemoteAddr();
        Admin admin = adminService.findByLogin(loginDTO.getUsername()).orElse(null);
        if (admin != null && passwordEncoder.matches(loginDTO.getPassword(), admin.getPassword())) {
            System.out.println("JpaLoginService: Admin is NOT null, and the password is correct");
            if (admin.getKnownIps().contains(clientIpAddress)) {
                System.out.println("The IP is on the Admin's list, loading user details");
                LOGGER.info("Successful login attempt for Admin: " + admin.getLogin() + " at " + LocalDateTime.now());
                return userDetailsService.loadUserByUsername(loginDTO.getUsername());
            } else {
                if (!admin.getName().equals("DefaultAdmin")) {
                    System.out.println("The IP is NOT on the Admin's list, initiating 2FA");
                    initiateTwoFactorAuthentication(admin.getEmail(), admin.getLogin());
                    LOGGER.info("Two-factor identification requested from Admin: " + admin.getLogin() + " at " + LocalDateTime.now());
                    System.out.println("New IP " + clientIpAddress + " detected for admin " + admin.getId());
                    return null;
                } else {
                    System.out.println("Successful login attempt for DefaultAdmin at " + LocalDateTime.now());
                    return userDetailsService.loadUserByUsername(loginDTO.getUsername());
                }
            }
        }
        LOGGER.warn("Failed login attempt for Admin with username: " + loginDTO.getUsername() + " at " + LocalDateTime.now());
        throw new AuthenticationFailedException("Authentication failed for the provided credentials");
    }


    private Collection<? extends GrantedAuthority> getAuthoritiesForUser(UserRole userRole) {
        switch (userRole) {
            case TENANT:
                return Collections.singletonList(new SimpleGrantedAuthority("ROLE_TENANT"));
            case MANAGER:
                return Collections.singletonList(new SimpleGrantedAuthority("ROLE_MANAGER"));
            case ADMIN:
                return Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"));
            default:
                return Collections.singletonList(new SimpleGrantedAuthority("GUEST"));
        }
    }

    // AUXILIARY METHODS FOR 2FA
    private void initiateTwoFactorAuthentication(String userEmail, String login) {
        // Generate a random one-time code (OTP)
        String otp = generateRandomOtp();
        // Send the OTP to the user via their preferred communication channel
        sendOtpToUser(otp, userEmail);
        // Store the OTP in a secure location (e.g., session, database) for verification later
        storeOtpForVerification(otp, login);
    }

    private String generateRandomOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    public void sendOtpToUser(String otp, String userEmail) {
        // Can be replaced with SMS or app authentication mechanism in future versions
        try {
            String subject = "Your One-Time Password (OTP) for " + platformName;
            String body = "Your OTP is: " + otp;
            emailService.sendEmail(userEmail, subject, body);
            System.out.println("OTP sent to " + userEmail + " via email.");
        } catch (MessagingException e) {
            LOGGER.error("Failed to send OTP email to " + userEmail, e);
            throw new EmailSendingException("Failed to send OTP email. Please try again later.", e);
        }
    }

//    private void storeOtpForVerification(String otp) {
//        // Retrieve the current authentication object
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        // Check if the authentication object is not null and is authenticated
//        if (authentication != null && authentication.isAuthenticated()) {
//            // Get the user details from the authentication object
//            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
//            // Store the OTP in the user's session
//            ((HttpSession) RequestContextHolder.currentRequestAttributes().resolveReference(RequestAttributes.REFERENCE_SESSION)).setAttribute("otp", otp);
//        }
//    }
    private void storeOtpForVerification(String otp, String username) {
        // Retrieve the current authentication object
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Check if the authentication object is not null and is authenticated
        if (authentication != null && authentication.isAuthenticated()) {
            // Store the OTP in the user's session directly
            HttpSession session = ((HttpSession) RequestContextHolder.currentRequestAttributes()
                    .resolveReference(RequestAttributes.REFERENCE_SESSION));
            session.setAttribute("otp", otp);
            session.setAttribute("otpExpiry", System.currentTimeMillis() + 300000); // OTP expires in 5 minutes
            session.setAttribute("otpAttempts", 0); // Initialize the attempts counter
            session.setAttribute("username", username); // Store username for later use
        } else {
            // Handle cases where authentication is null or not authenticated if needed
            throw new IllegalStateException("No authenticated user found for storing OTP.");
        }
    }


}
