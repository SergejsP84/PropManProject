package lv.emendatus.Destiny_PropMan.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lv.emendatus.Destiny_PropMan.annotation.authentication_controller.*;
import lv.emendatus.Destiny_PropMan.domain.dto.authentication.LoginDTO;
import lv.emendatus.Destiny_PropMan.domain.dto.registration.ManagerRegistrationDTO;
import lv.emendatus.Destiny_PropMan.domain.dto.registration.TenantRegistrationDTO;
import lv.emendatus.Destiny_PropMan.domain.entity.Admin;
import lv.emendatus.Destiny_PropMan.domain.entity.Manager;
import lv.emendatus.Destiny_PropMan.domain.entity.Tenant;
import lv.emendatus.Destiny_PropMan.service.implementation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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
        Tenant tenant = loginService.authenticateTenant(loginDTO, request);
        if (tenant != null) {
            return ResponseEntity.ok(tenant);
        }
        Manager manager = loginService.authenticateManager(loginDTO, request);
        if (manager != null) {
            return ResponseEntity.ok(manager);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid login credentials");
    }

    @PostMapping("/admin-login")
    @Authentication_AdminLogin
    public ResponseEntity<?> adminLogin(@RequestBody LoginDTO loginDTO, HttpServletRequest request) {
        System.out.println("!!! - Admin login method INVOKED!");
        Admin admin = loginService.authenticateAdmin(loginDTO, request);
        if (admin != null) {
            return ResponseEntity.ok(admin);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid login credentials");
    }

    @PostMapping("/verify-otp-tenant")
    @Authentication_VerifyOtpTenant
    public ResponseEntity<?> verifyOtpTenant(@RequestParam String otp, HttpServletRequest request) {
        // Retrieve the stored OTP from the user session
        HttpSession session = request.getSession();
        String storedOtp = (String) session.getAttribute("otp");
        // Compare the submitted OTP with the stored OTP
        if (storedOtp != null && storedOtp.equals(otp)) {
            // Clear the OTP from the session to prevent reuse
            session.removeAttribute("otp");
            // Get the authenticated user details
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            // Update the knownIps field with the current IP address
            String login = userDetails.getUsername();
            Tenant tenant = tenantService.getTenantByLogin(login);
            String currentIpAddress = request.getRemoteAddr();
            List<String> knownIps = tenant.getKnownIps();
            knownIps.add(currentIpAddress);
            tenant.setKnownIps(knownIps);
            tenantService.addTenant(tenant);
            // Log in the user (set up the authentication)
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);
            // Return a success response
            return ResponseEntity.ok("OTP verification successful. Tenant logged in.");
        } else {
            // Return a failure response
            return ResponseEntity.badRequest().body("Invalid OTP. Please try again.");
        }
    }

    @PostMapping("/verify-otp-manager")
    @Authentication_VerifyOtpManager
    public ResponseEntity<?> verifyOtpManager(@RequestParam String otp, HttpServletRequest request) {
        HttpSession session = request.getSession();
        String storedOtp = (String) session.getAttribute("otp");
        if (storedOtp != null && storedOtp.equals(otp)) {
            session.removeAttribute("otp");
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String login = userDetails.getUsername();
            Manager manager = managerService.getManagerByLogin(login);
            String currentIpAddress = request.getRemoteAddr();
            List<String> knownIps = manager.getKnownIps();
            knownIps.add(currentIpAddress);
            manager.setKnownIps(knownIps);
            managerService.addManager(manager);
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);
            return ResponseEntity.ok("OTP verification successful. Manager logged in.");
        } else {
            return ResponseEntity.badRequest().body("Invalid OTP. Please try again.");
        }
    }

    @PostMapping("/verify-otp-admin")
    @Authentication_VerifyOtpAdmin
    public ResponseEntity<?> verifyOtpAdmin(@RequestParam String otp, HttpServletRequest request) {
        HttpSession session = request.getSession();
        String storedOtp = (String) session.getAttribute("otp");
        if (storedOtp != null && storedOtp.equals(otp)) {
            session.removeAttribute("otp");
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String login = userDetails.getUsername();
            if (!adminAccountsService.findByLogin(login).isPresent()) {
                throw new RuntimeException("Error fetching the admin credentials");
            } else {
                Admin admin = adminAccountsService.findByLogin(login).get();
                String currentIpAddress = request.getRemoteAddr();
                adminAccountsService.addAnIpForAdmin(login, currentIpAddress);
            }
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);
            return ResponseEntity.ok("OTP verification successful. Admin logged in.");
        } else {
            return ResponseEntity.badRequest().body("Invalid OTP. Please try again.");
        }
    }

}