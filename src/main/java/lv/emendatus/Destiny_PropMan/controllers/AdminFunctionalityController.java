package lv.emendatus.Destiny_PropMan.controllers;

import lv.emendatus.Destiny_PropMan.annotation.admin_functionality_controller.*;
import lv.emendatus.Destiny_PropMan.domain.dto.Admin.AdminPayoutDTO;
import lv.emendatus.Destiny_PropMan.domain.dto.Admin.AdminRefundDTO;
import lv.emendatus.Destiny_PropMan.domain.dto.Admin.SetNumConfigDTO;
import lv.emendatus.Destiny_PropMan.domain.dto.managerial.ManagerProfileDTO;
import lv.emendatus.Destiny_PropMan.domain.dto.managerial.PropertyAdditionDTO;
import lv.emendatus.Destiny_PropMan.domain.dto.profile.TenantDTO_Profile;
import lv.emendatus.Destiny_PropMan.domain.dto.registration.ManagerRegistrationDTO;
import lv.emendatus.Destiny_PropMan.domain.dto.registration.TenantRegistrationDTO;
import lv.emendatus.Destiny_PropMan.domain.entity.Payout;
import lv.emendatus.Destiny_PropMan.domain.entity.Refund;
import lv.emendatus.Destiny_PropMan.service.implementation.JpaAdminFunctionalityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminFunctionalityController {

    private final JpaAdminFunctionalityService adminFunctionalityService;

    public AdminFunctionalityController(JpaAdminFunctionalityService adminService) {
        this.adminFunctionalityService = adminService;
    }

    @PutMapping("/toggle_tenant_status/{tenant_id}")
    @AdminFunc_ToggleTenantStatus(path = "/toggle_tenant_status/{tenant_id}")
    public ResponseEntity<Void> toggleTenantStatus(@PathVariable Long tenant_id) {
        adminFunctionalityService.toggleTenantStatus(tenant_id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/toggle_manager_status/{manager_id}")
    @AdminFunc_ToggleManagerStatus(path = "/toggle_manager_status/{manager_id}")
    public ResponseEntity<Void> toggleManagerStatus(@PathVariable Long manager_id) {
        adminFunctionalityService.toggleManagerStatus(manager_id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/register_tenant")
    @AdminFunc_RegisterTenant(path = "/register_tenant")
    public ResponseEntity<String> registerTenantForAdmin(@RequestBody TenantRegistrationDTO registrationDTO) {
        try {
            adminFunctionalityService.registerTenantForAdmin(registrationDTO);
            return ResponseEntity.ok("Tenant registered successfully by admin.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while registering the tenant.");
        }
    }

    @PostMapping("/register_manager")
    @AdminFunc_RegisterManager(path = "/register_manager")
    public ResponseEntity<String> registerManager(@RequestBody ManagerRegistrationDTO registrationDTO) {
        try {
            adminFunctionalityService.registerManager(registrationDTO);
            return ResponseEntity.ok("Manager registered successfully by admin.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while registering the manager.");
        }
    }

    @PutMapping("/update-tenant/{tenantId}")
    @AdminFunc_UpdateTenantProfile
    public ResponseEntity<String> updateTenantInformation(@PathVariable Long tenantId, @RequestBody TenantDTO_Profile updatedTenantInfo) {
        try {
            adminFunctionalityService.updateTenantInformation(tenantId, updatedTenantInfo);
            return ResponseEntity.ok("Tenant information updated successfully by admin.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while updating tenant information.");
        }
    }

    @PutMapping("/update-manager/{managerId}")
    @AdminFunc_UpdateManagerProfile
    public ResponseEntity<String> updateManagerInformation(@PathVariable Long managerId, @RequestBody ManagerProfileDTO updatedProfile) {
        try {
            adminFunctionalityService.updateManagerInformation(managerId, updatedProfile);
            return ResponseEntity.ok("Manager information updated successfully by admin.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while updating manager information.");
        }
    }
    @DeleteMapping("/delete_tenant/{tenantId}")
    @AdminFunc_RemoveTenant
    public ResponseEntity<String> removeTenant(@PathVariable Long tenantId) {
        try {
            adminFunctionalityService.removeTenant(tenantId);
            return ResponseEntity.ok("Tenant with ID " + tenantId + " has been removed.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to remove tenant: " + e.getMessage());
        }
    }
    @DeleteMapping("/delete_manager/{managerId}")
    @AdminFunc_RemoveManager
    public ResponseEntity<String> removeManager(@PathVariable Long managerId) {
        try {
            adminFunctionalityService.removeManager(managerId);
            return ResponseEntity.ok("Manager with ID " + managerId + " has been removed.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to remove manager: " + e.getMessage());
        }
    }

    @PostMapping("/add_property")
    @AdminFunc_AddProperty
    public ResponseEntity<String> addProperty(@RequestBody PropertyAdditionDTO propertyDTO) {
        adminFunctionalityService.addProperty(propertyDTO);
        return ResponseEntity.ok("Property added successfully.");
    }

    @DeleteMapping("/delete_property/{propertyId}")
    @AdminFunc_RemoveProperty
    public ResponseEntity<String> removeProperty(@PathVariable Long propertyId) {
        adminFunctionalityService.removeProperty(propertyId);
        return ResponseEntity.ok("Property removed successfully.");
    }

    @PatchMapping("/resolve_claim/{claimId}")
    @AdminFunc_ResolveClaim
    public ResponseEntity<String> resolveClaim(@PathVariable Long claimId, @RequestBody String resolution) {
        adminFunctionalityService.resolveClaim(claimId, resolution);
        return ResponseEntity.ok("Claim resolved successfully.");
    }

    @GetMapping("/pending_refunds")
    @AdminFunc_ViewPendingRefunds
    public ResponseEntity<List<AdminRefundDTO>> viewPendingRefunds() {
        List<AdminRefundDTO> pendingRefunds = adminFunctionalityService.viewPendingRefunds();
        return ResponseEntity.ok(pendingRefunds);
    }

    @PostMapping("/settle_refund")
    @AdminFunc_SettleRefund
    public ResponseEntity<String> settleRefund(@RequestBody Refund refund) {
        boolean settlementSuccess = adminFunctionalityService.settleRefund(refund);
        if (settlementSuccess) {
            return ResponseEntity.ok("Refund settled successfully.");
        } else {
            return ResponseEntity.badRequest().body("Failed to settle refund.");
        }
    }

    @GetMapping("/pending_payouts")
    @AdminFunc_ViewPendingPayouts
    public ResponseEntity<List<AdminPayoutDTO>> viewPendingPayouts() {
        List<AdminPayoutDTO> pendingPayouts = adminFunctionalityService.viewPendingPayouts();
        return ResponseEntity.ok(pendingPayouts);
    }

    @PostMapping("/settle_payout")
    @AdminFunc_SettlePayout
    public ResponseEntity<String> settlePayout(@RequestBody Payout payout) {
        boolean settlementSuccess = adminFunctionalityService.settlePayout(payout);
        if (settlementSuccess) {
            return ResponseEntity.ok("Payout settled successfully.");
        } else {
            return ResponseEntity.badRequest().body("Failed to settle payout.");
        }
    }

    @PostMapping("/new_payout")
    @AdminFunc_AddNewPayout
    public ResponseEntity<String> addNewPayout(@RequestParam Long bookingId, @RequestParam Long managerId, @RequestParam Double amount, @RequestParam Long currencyId) {
        adminFunctionalityService.createPayout(bookingId, managerId, amount, currencyId);
        return ResponseEntity.ok("Payout added successfully.");
    }

    @PostMapping("/new_refund")
    @AdminFunc_AddNewRefund
    public ResponseEntity<String> addNewRefund(@RequestParam Long bookingId, @RequestParam Long tenantId, @RequestParam Double amount, @RequestParam Long currencyId) {
        adminFunctionalityService.createRefund(bookingId, tenantId, amount, currencyId);
        return ResponseEntity.ok("Refund added successfully.");
    }

    @PostMapping("/add_currency")
    @AdminFunc_AddNewCurrency
    public ResponseEntity<String> addNewCurrency(@RequestParam String designation, @RequestParam Double rateToBase) {
        adminFunctionalityService.addNewCurrency(designation, rateToBase);
        return ResponseEntity.ok("Currency added successfully.");
    }


    @PostMapping("/set_new_base_currency")
    @AdminFunc_SetNewBaseCurrency
    public ResponseEntity<String> setNewBaseCurrency(
            @RequestParam Long newBaseCurrencyId,
            @RequestParam List<Double> ratesForOtherCurrencies) {
        adminFunctionalityService.setNewBaseCurrency(newBaseCurrencyId, ratesForOtherCurrencies);
        return ResponseEntity.ok("Base currency and exchange rates updated successfully.");
    }

    @PostMapping("/set_numerical_configs")
    @AdminFunc_SetNumericalConfigs
    public ResponseEntity<String> setNumericalConfigs(@RequestBody SetNumConfigDTO dto) {
        adminFunctionalityService.setNumericalConfigs(dto);
        return ResponseEntity.ok("Numerical configurations updated successfully.");
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/add_amenity")
    @AdminFunc_AddAmenityToDatabase
    public ResponseEntity<Void> addAmenityToDatabase(@RequestParam String amenityDescription) {
        adminFunctionalityService.addAmenityToDatabase(amenityDescription);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/remove_amenity/{amenityId}")
    @AdminFunc_RemoveAmenity
    public ResponseEntity<Void> removeAmenityFromDatabase(@PathVariable Long amenityId) {
        adminFunctionalityService.removeAmenityFromDatabase(amenityId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
