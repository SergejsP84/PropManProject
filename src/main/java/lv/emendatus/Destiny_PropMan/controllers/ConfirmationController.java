package lv.emendatus.Destiny_PropMan.controllers;

import lv.emendatus.Destiny_PropMan.annotation.confirmation_controller.ConfirmEmailChangeManager;
import lv.emendatus.Destiny_PropMan.annotation.confirmation_controller.ConfirmEmailChangeTenant;
import lv.emendatus.Destiny_PropMan.annotation.confirmation_controller.ConfirmRegistration;
import lv.emendatus.Destiny_PropMan.domain.entity.Manager;
import lv.emendatus.Destiny_PropMan.domain.entity.Tenant;
import lv.emendatus.Destiny_PropMan.service.implementation.JpaManagerService;
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
    @ConfirmRegistration
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

        return "registration_confirmation"; // This should map to a confirmation page view
    }

    @GetMapping("/ten/confirm-email-change")
    @ConfirmEmailChangeTenant
    public String confirmEmailChangeTenant(@RequestParam("token") String token, Model model) {
        Tenant tenant = tenantService.getTenantByConfirmationToken(token);
            if (tenant != null) {
                if (tenantService.isTokenValid(tenant, token)) {
                    tenant.setEmail(tenant.getTemporaryEmail());
                    System.out.println("Set the user's email to the new address: " + tenant.getTemporaryEmail());
                    tenant.setTemporaryEmail(null);
                    tenant.setConfirmationToken(null);
                    tenantService.addTenant(tenant);
                    model.addAttribute("message", "Your email address has been successfully updated!");
                } else {
                    model.addAttribute("error", "Invalid or expired token. Please try again.");
                }

            }
                return "email_change_confirmation";
    }
    @GetMapping("/man/confirm-email-change")
    @ConfirmEmailChangeManager
    public String confirmEmailChangeManager(@RequestParam("token") String token, Model model) {
        Manager manager = managerService.getManagerByConfirmationToken(token);
        if (manager != null) {
            if (managerService.isTokenValid(manager, token)) {
                manager.setEmail(manager.getTemporaryEmail());
                System.out.println("Set the user's email to the new address: " + manager.getTemporaryEmail());
                manager.setTemporaryEmail(null);
                manager.setConfirmationToken(null);
                managerService.addManager(manager);
                model.addAttribute("message", "Your email address has been successfully updated!");
            } else {
                model.addAttribute("error", "Invalid or expired token. Please try again.");
            }
        }
        return "email_change_confirmation";
    }
}