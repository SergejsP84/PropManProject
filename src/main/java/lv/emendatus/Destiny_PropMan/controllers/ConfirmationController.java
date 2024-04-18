package lv.emendatus.Destiny_PropMan.controllers;

import lv.emendatus.Destiny_PropMan.domain.entity.Manager;
import lv.emendatus.Destiny_PropMan.domain.entity.Tenant;
import lv.emendatus.Destiny_PropMan.service.implementation.JpaManagerRegistrationService;
import lv.emendatus.Destiny_PropMan.service.implementation.JpaManagerService;
import lv.emendatus.Destiny_PropMan.service.implementation.JpaTenantRegistrationService;
import lv.emendatus.Destiny_PropMan.service.implementation.JpaTenantService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ConfirmationController {

    private final JpaTenantService tenantService;
    private final JpaManagerService managerService;

    public ConfirmationController(JpaTenantService tenantService, JpaManagerService managerService) {
        this.tenantService = tenantService;
        this.managerService = managerService;
    }

    @GetMapping("/confirm-registration")
    public String confirmRegistration(@RequestParam("token") String token, Model model) {
        Tenant tenant = tenantService.getTenantByConfirmationToken(token);
        Manager manager = managerService.getManagerByConfirmationToken(token);
        if (tenant != null) {
            if (tenantService.isTokenValid(tenant, token)) {
                tenant.setActive(true);
                tenantService.addTenant(tenant);
                model.addAttribute("message", "Your account has been successfully activated!");
            } else model.addAttribute("error", "Invalid or expired token. Please try again.");
        } else if (manager != null) {
            if (managerService.isTokenValid(manager, token)) {
                manager.setActive(true);
                managerService.addManager(manager);
                model.addAttribute("message", "Your account has been successfully activated!");
            } else model.addAttribute("error", "Invalid or expired token. Please try again.");
        } else model.addAttribute("error", "Invalid or expired token. Please try again.");

        return "confirmation";
    }
}