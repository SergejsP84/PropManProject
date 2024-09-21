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
import lv.emendatus.Destiny_PropMan.domain.entity.Claim;
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
    @PreAuthorize("hasAuthority('ADMIN')")
    @AdminFunc_ToggleTenantStatus(path = "/toggle_tenant_status/{tenant_id}")
    public ResponseEntity<Void> toggleTenantStatus(@PathVariable Long tenant_id) {
        adminFunctionalityService.toggleTenantStatus(tenant_id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // http://localhost:8080/admin/toggle_tenant_status/9

    @PutMapping("/toggle_manager_status/{manager_id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @AdminFunc_ToggleManagerStatus(path = "/toggle_manager_status/{manager_id}")
    public ResponseEntity<Void> toggleManagerStatus(@PathVariable Long manager_id) {
        adminFunctionalityService.toggleManagerStatus(manager_id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/register_tenant")
    @PreAuthorize("hasAuthority('ADMIN')")
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
    @PreAuthorize("hasAuthority('ADMIN')")
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

    @PutMapping("/update_tenant/{tenantId}")
    @PreAuthorize("hasAuthority('ADMIN')")
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

    @PutMapping("/update_manager/{managerId}")
    @PreAuthorize("hasAuthority('ADMIN')")
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
    @PreAuthorize("hasAuthority('ADMIN')")
    @AdminFunc_RemoveTenant
    public ResponseEntity<String> removeTenant(@PathVariable Long tenantId) {
        try {
            adminFunctionalityService.removeTenant(tenantId);
            return ResponseEntity.ok("Tenant removal method completed.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to remove tenant: " + e.getMessage());
        }
    }
    @DeleteMapping("/delete_manager/{managerId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @AdminFunc_RemoveManager
    public ResponseEntity<String> removeManager(@PathVariable Long managerId) {
        try {
            adminFunctionalityService.removeManager(managerId);
            return ResponseEntity.ok("Manager removal method completed.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to remove manager: " + e.getMessage());
        }
    }

    @PostMapping("/add_property")
    @PreAuthorize("hasAuthority('ADMIN')")
    @AdminFunc_AddProperty
    public ResponseEntity<String> addProperty(@RequestBody PropertyAdditionDTO propertyDTO) {
        adminFunctionalityService.addProperty(propertyDTO);
        return ResponseEntity.ok("Property added successfully.");
    }

    @DeleteMapping("/delete_property/{propertyId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @AdminFunc_RemoveProperty
    public ResponseEntity<String> removeProperty(@PathVariable Long propertyId) {
        adminFunctionalityService.removeProperty(propertyId);
        return ResponseEntity.ok("Property removed successfully.");
    }

    @PatchMapping("/resolve_claim/{claimId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @AdminFunc_ResolveClaim
    public ResponseEntity<String> resolveClaim(@PathVariable Long claimId, @RequestBody String resolution) {
        adminFunctionalityService.resolveClaim(claimId, resolution);
        return ResponseEntity.ok("Claim resolved successfully.");
    }

    @GetMapping("/pending_refunds")
    @PreAuthorize("hasAuthority('ADMIN')")
    @AdminFunc_ViewPendingRefunds
    public ResponseEntity<List<AdminRefundDTO>> viewPendingRefunds() {
        List<AdminRefundDTO> pendingRefunds = adminFunctionalityService.viewPendingRefunds();
        return ResponseEntity.ok(pendingRefunds);
    }

    @PostMapping("/settle_refund")
    @PreAuthorize("hasAuthority('ADMIN')")
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
    @PreAuthorize("hasAuthority('ADMIN')")
    @AdminFunc_ViewPendingPayouts
    public ResponseEntity<List<AdminPayoutDTO>> viewPendingPayouts() {
        List<AdminPayoutDTO> pendingPayouts = adminFunctionalityService.viewPendingPayouts();
        return ResponseEntity.ok(pendingPayouts);
    }

    @PostMapping("/settle_payout")
    @PreAuthorize("hasAuthority('ADMIN')")
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
    @PreAuthorize("hasAuthority('ADMIN')")
    @AdminFunc_AddNewPayout
    public ResponseEntity<String> addNewPayout(@RequestParam Long bookingId, @RequestParam Long managerId, @RequestParam Double amount, @RequestParam Long currencyId) {
        adminFunctionalityService.createPayout(bookingId, managerId, amount, currencyId);
        return ResponseEntity.ok("Payout added successfully.");
    }

    @PostMapping("/new_refund")
    @PreAuthorize("hasAuthority('ADMIN')")
    @AdminFunc_AddNewRefund
    public ResponseEntity<String> addNewRefund(@RequestParam Long bookingId, @RequestParam Long tenantId, @RequestParam Double amount, @RequestParam Long currencyId) {
        adminFunctionalityService.createRefund(bookingId, tenantId, amount, currencyId);
        return ResponseEntity.ok("Refund added successfully.");
    }

    @PostMapping("/add_currency")
    @PreAuthorize("hasAuthority('ADMIN')")
    @AdminFunc_AddNewCurrency
    public ResponseEntity<String> addNewCurrency(@RequestParam String designation, @RequestParam Double rateToBase) {
        adminFunctionalityService.addNewCurrency(designation, rateToBase);
        return ResponseEntity.ok("Currency added successfully.");
    }


    @PostMapping("/set_new_base_currency")
    @PreAuthorize("hasAuthority('ADMIN')")
    @AdminFunc_SetNewBaseCurrency
    public ResponseEntity<String> setNewBaseCurrency(
            @RequestParam Long newBaseCurrencyId) {
        adminFunctionalityService.setNewBaseCurrency(newBaseCurrencyId);
        return ResponseEntity.ok("Base currency, exchange rates and property rental prices updated successfully.");
    }

    @PostMapping("/set_numerical_configs")
    @PreAuthorize("hasAuthority('ADMIN')")
    @AdminFunc_SetNumericalConfigs
    public ResponseEntity<String> setNumericalConfigs(@RequestBody SetNumConfigDTO dto) {
        adminFunctionalityService.setNumericalConfigs(dto);
        return ResponseEntity.ok("Numerical configurations updated successfully.");
    }


    @PostMapping("/add_amenity")
    @PreAuthorize("hasAuthority('ADMIN')")
    @AdminFunc_AddAmenityToDatabase
    public ResponseEntity<Void> addAmenityToDatabase(@RequestParam String amenityDescription) {
        adminFunctionalityService.addAmenityToDatabase(amenityDescription);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/remove_amenity/{amenityId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @AdminFunc_RemoveAmenity
    public ResponseEntity<Void> removeAmenityFromDatabase(@PathVariable Long amenityId) {
        adminFunctionalityService.removeAmenityFromDatabase(amenityId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/delete-review/{reviewId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @AdminFunc_DeleteReview
    public ResponseEntity<Void> deleteReview(@PathVariable Long reviewId) {
        adminFunctionalityService.deleteReview(reviewId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/view-claims")
    @PreAuthorize("hasAuthority('ADMIN')")
    @AdminFunc_ViewClaims
    public ResponseEntity<List<Claim>> viewClaims() {
        List<Claim> claims = adminFunctionalityService.viewClaims();
        return ResponseEntity.ok(claims);
    }

    @PutMapping("/change-rate/{currencyId}/{newRate}")
    @AdminFunc_UpdateCurrencyRate
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> changeCurrencyExchangeRate(@PathVariable Long currencyId, @PathVariable Double newRate) {
        adminFunctionalityService.updateCurrencyRate(currencyId, newRate);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
