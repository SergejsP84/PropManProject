package lv.emendatus.Destiny_PropMan.controllers;

import jakarta.validation.Valid;
import lv.emendatus.Destiny_PropMan.domain.dto.authentication.LoginDTO;
import lv.emendatus.Destiny_PropMan.domain.dto.registration.ManagerRegistrationDTO;
import lv.emendatus.Destiny_PropMan.domain.dto.registration.TenantRegistrationDTO;
import lv.emendatus.Destiny_PropMan.domain.entity.Manager;
import lv.emendatus.Destiny_PropMan.domain.entity.Tenant;
import lv.emendatus.Destiny_PropMan.service.implementation.JpaLoginService;
import lv.emendatus.Destiny_PropMan.service.implementation.JpaManagerRegistrationService;
import lv.emendatus.Destiny_PropMan.service.interfaces.TenantRegistrationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final TenantRegistrationService tenantRegistrationService;
    private final JpaManagerRegistrationService managerRegistrationService;
    private final JpaLoginService loginService;

    public AuthenticationController(TenantRegistrationService tenantRegistrationService, JpaManagerRegistrationService managerRegistrationService, JpaLoginService loginService) {
        this.tenantRegistrationService = tenantRegistrationService;
        this.managerRegistrationService = managerRegistrationService;
        this.loginService = loginService;
    }

    @PostMapping("/register/tenant")
    public ResponseEntity<?> registerTenant(@RequestBody @Valid TenantRegistrationDTO registrationDTO) {
        try {
            tenantRegistrationService.registerTenant(registrationDTO);
            return ResponseEntity.ok("Tenant registered successfully");
//  TODO:      } catch (InvalidPaymentCardNumberException e) {
//            return ResponseEntity.badRequest().body("Invalid payment card number");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while processing the request");
        }
    }

    @PostMapping("/register/manager")
    public ResponseEntity<?> registerManager(@RequestBody @Valid ManagerRegistrationDTO registrationDTO) {
        try {
            managerRegistrationService.registerManager(registrationDTO);
            return ResponseEntity.ok("Manager registered successfully");
//  TODO:      } catch (InvalidPaymentCardNumberException e) {
//            return ResponseEntity.badRequest().body("Invalid payment card number");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while processing the request");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO) {
        Tenant tenant = loginService.authenticateTenant(loginDTO);
        if (tenant != null) {
            return ResponseEntity.ok(tenant);
        }
        Manager manager = loginService.authenticateManager(loginDTO);
        if (manager != null) {
            return ResponseEntity.ok(manager);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid login credentials");
    }



}