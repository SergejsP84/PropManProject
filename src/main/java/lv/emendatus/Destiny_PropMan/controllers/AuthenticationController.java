package lv.emendatus.Destiny_PropMan.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lv.emendatus.Destiny_PropMan.annotation.authentication_controller.*;
import lv.emendatus.Destiny_PropMan.domain.dto.authentication.LoginDTO;
import lv.emendatus.Destiny_PropMan.domain.dto.authentication.UserResponse;
import lv.emendatus.Destiny_PropMan.domain.dto.registration.ManagerRegistrationDTO;
import lv.emendatus.Destiny_PropMan.domain.dto.registration.TenantRegistrationDTO;
import lv.emendatus.Destiny_PropMan.domain.entity.Admin;
import lv.emendatus.Destiny_PropMan.domain.entity.Manager;
import lv.emendatus.Destiny_PropMan.domain.entity.Tenant;
import lv.emendatus.Destiny_PropMan.service.implementation.*;
import lv.emendatus.Destiny_PropMan.util.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final JpaTenantRegistrationService tenantRegistrationService;
    private final JpaManagerRegistrationService managerRegistrationService;
    private final JpaLoginService loginService;
    private final JpaTenantService tenantService;
    private final JpaManagerService managerService;
    private final JpaAdminAccountsService adminAccountsService;


    public AuthenticationController(JpaTenantRegistrationService tenantRegistrationService, JpaManagerRegistrationService managerRegistrationService, JpaLoginService loginService, JpaTenantService tenantService, JpaManagerService managerService, JpaAdminAccountsService adminAccountsService) {
        this.tenantRegistrationService = tenantRegistrationService;
        this.managerRegistrationService = managerRegistrationService;
        this.loginService = loginService;
        this.tenantService = tenantService;
        this.managerService = managerService;
        this.adminAccountsService = adminAccountsService;
    }

    @PostMapping("/register/tenant")
    @Authentication_RegisterTenant
    public ResponseEntity<?> registerTenant(@RequestBody @Valid TenantRegistrationDTO registrationDTO) {
        try {
            tenantRegistrationService.registerTenant(registrationDTO);
            return ResponseEntity.ok("Tenant registered successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while processing the request");
        }
    }

    @PostMapping("/register/manager")
    @Authentication_RegisterManager
    public ResponseEntity<?> registerManager(@RequestBody @Valid ManagerRegistrationDTO registrationDTO) {
        try {
            managerRegistrationService.registerManager(registrationDTO);
            return ResponseEntity.ok("Manager registered successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while processing the request");
        }
    }

@PostMapping("/login")
@Authentication_Login
public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO, HttpServletRequest request) {
    Tenant tenant = tenantService.getTenantByLogin(loginDTO.getUsername());
    Manager manager = managerService.getManagerByLogin(loginDTO.getUsername());
    if (tenant != null) {
        UserDetails userDetails = loginService.authenticateTenant(loginDTO, request);
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Two-factor authentication required. Please check your email for the OTP.");
        }
        String token = JwtUtil.generateToken(userDetails);
        UserResponse userResponse = new UserResponse(userDetails.getUsername(), userDetails.getAuthorities(), token);
        return ResponseEntity.ok(userResponse);
    }
    if (manager != null) {
        UserDetails userDetails = loginService.authenticateManager(loginDTO, request);
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Two-factor authentication required. Please check your email for the OTP.");
        }
        String token = JwtUtil.generateToken(userDetails);
        UserResponse userResponse = new UserResponse(userDetails.getUsername(), userDetails.getAuthorities(), token);
        return ResponseEntity.ok(userResponse);
    }
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid login credentials");
}


    @PostMapping("/admin-login")
    @Authentication_AdminLogin
    public ResponseEntity<?> adminLogin(@RequestBody LoginDTO loginDTO, HttpServletRequest request) {
        UserDetails userDetails = loginService.authenticateAdmin(loginDTO, request);
        if (userDetails != null) {
            String token = JwtUtil.generateToken(userDetails);
            UserResponse userResponse = new UserResponse(userDetails.getUsername(), userDetails.getAuthorities(), token);
            return ResponseEntity.ok(userResponse);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Two-factor authentication required. Please check your email for the OTP.");
        }
    }
    @PostMapping("/verify-otp-tenant")
    @Authentication_VerifyOtpTenant
    public ResponseEntity<?> verifyOtpTenant(@RequestParam String otp, HttpServletRequest request) {
        HttpSession session = request.getSession();
        // Retrieve the stored OTP, expiry time, and attempt counter from the session
        String storedOtp = (String) session.getAttribute("otp");
        Long expiryTime = (Long) session.getAttribute("otpExpiry");
        Integer attempts = (Integer) session.getAttribute("otpAttempts");
        String username = (String) session.getAttribute("username"); // Retrieve username from session
//        System.out.println("Retrieved the following username from the session: " + username);
        // Check if the OTP is expired
        if (storedOtp != null && expiryTime != null && System.currentTimeMillis() > expiryTime) {
            return ResponseEntity.status(HttpStatus.GONE).body("OTP expired. Please request a new one.");
        }
        // Check if too many incorrect attempts have been made
        if (attempts != null && attempts >= 3) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("Too many incorrect attempts. Please request a new OTP.");
        }
        // Check if the submitted OTP matches the stored OTP
        if (storedOtp != null && storedOtp.equals(otp)) {
            // Clear OTP data from the session to prevent reuse
            session.removeAttribute("otp");
            session.removeAttribute("otpExpiry");
            session.removeAttribute("otpAttempts");
            // Retrieve Tenant from the database using the stored username
            Tenant tenant = tenantService.getTenantByLogin(username);
            if (tenant == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found.");
            }
            // Update the known IPs list
            String currentIpAddress = request.getRemoteAddr();
            List<String> knownIps = tenant.getKnownIps();
            knownIps.add(currentIpAddress);
            tenant.setKnownIps(knownIps);
            tenantService.addTenant(tenant);
            // Set up the authentication in the SecurityContext
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    tenant.getLogin(), null, tenant.getAuthorities()); // Use username instead of principal
            SecurityContextHolder.getContext().setAuthentication(auth);

            return ResponseEntity.ok("OTP verification successful. Tenant logged in.");
        } else {
            // Increment the attempt counter
            session.setAttribute("otpAttempts", (attempts == null ? 0 : attempts) + 1);
            return ResponseEntity.badRequest().body("Invalid OTP. Please try again.");
        }
    }

    @PostMapping("/verify-otp-manager")
    @Authentication_VerifyOtpManager
    public ResponseEntity<?> verifyOtpManager(@RequestParam String otp, HttpServletRequest request) {
        HttpSession session = request.getSession();
        // Retrieve the stored OTP, expiry time, and attempt counter from the session
        String storedOtp = (String) session.getAttribute("otp");
        Long expiryTime = (Long) session.getAttribute("otpExpiry");
        Integer attempts = (Integer) session.getAttribute("otpAttempts");
        String username = (String) session.getAttribute("username"); // Retrieve username from session
        System.out.println("Retrieved the following username from the session: " + username);
        // Check if the OTP is expired
        if (storedOtp != null && expiryTime != null && System.currentTimeMillis() > expiryTime) {
            return ResponseEntity.status(HttpStatus.GONE).body("OTP expired. Please request a new one.");
        }
        // Check if too many incorrect attempts have been made
        if (attempts != null && attempts >= 3) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("Too many incorrect attempts. Please request a new OTP.");
        }
        // Check if the submitted OTP matches the stored OTP
        if (storedOtp != null && storedOtp.equals(otp)) {
            // Clear OTP data from the session to prevent reuse
            session.removeAttribute("otp");
            session.removeAttribute("otpExpiry");
            session.removeAttribute("otpAttempts");
            // Retrieve Manager from the database using the stored username
            Manager manager = managerService.getManagerByLogin(username);
            if (manager == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found.");
            }
            // Update the known IPs list
            String currentIpAddress = request.getRemoteAddr();
            List<String> knownIps = manager.getKnownIps();
            knownIps.add(currentIpAddress);
            manager.setKnownIps(knownIps);
            managerService.addManager(manager);
            // Set up the authentication in the SecurityContext
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    manager.getLogin(), null, manager.getAuthorities()); // Use username instead of principal
            SecurityContextHolder.getContext().setAuthentication(auth);
            return ResponseEntity.ok("OTP verification successful. Manager logged in.");
        } else {
            // Increment the attempt counter
            session.setAttribute("otpAttempts", (attempts == null ? 0 : attempts) + 1);
            return ResponseEntity.badRequest().body("Invalid OTP. Please try again.");
        }
    }

    @PostMapping("/verify-otp-admin")
    @Authentication_VerifyOtpAdmin
    public ResponseEntity<?> verifyOtpAdmin(@RequestParam String otp, HttpServletRequest request) {
        HttpSession session = request.getSession();
        // Retrieve the stored OTP, expiry time, and attempt counter from the session
        String storedOtp = (String) session.getAttribute("otp");
        Long expiryTime = (Long) session.getAttribute("otpExpiry");
        Integer attempts = (Integer) session.getAttribute("otpAttempts");
        String username = (String) session.getAttribute("username"); // Retrieve username from session
        System.out.println("Retrieved the following username from the session: " + username);
        // Check if the OTP is expired
        if (storedOtp != null && expiryTime != null && System.currentTimeMillis() > expiryTime) {
            return ResponseEntity.status(HttpStatus.GONE).body("OTP expired. Please request a new one.");
        }
        // Check if too many incorrect attempts have been made
        if (attempts != null && attempts >= 3) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("Too many incorrect attempts. Please request a new OTP.");
        }
        // Check if the submitted OTP matches the stored OTP
        if (storedOtp != null && storedOtp.equals(otp)) {
            // Clear OTP data from the session to prevent reuse
            session.removeAttribute("otp");
            session.removeAttribute("otpExpiry");
            session.removeAttribute("otpAttempts");
            // Retrieve Admin from the database using the stored username
            Admin admin = adminAccountsService.findByLogin(username).orElse(null);
            if (admin == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found.");
            }
            // Update the known IPs list
            String currentIpAddress = request.getRemoteAddr();
            adminAccountsService.addAnIpForAdmin(username, currentIpAddress);
            // Set up the authentication in the SecurityContext
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    admin.getLogin(), null, admin.getAuthorities()); // Use username instead of principal
            SecurityContextHolder.getContext().setAuthentication(auth);
            return ResponseEntity.ok("OTP verification successful. Admin logged in.");
        } else {
            // Increment the attempt counter
            session.setAttribute("otpAttempts", (attempts == null ? 0 : attempts) + 1);
            return ResponseEntity.badRequest().body("Invalid OTP. Please try again.");
        }
    }


}