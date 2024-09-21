package lv.emendatus.Destiny_PropMan.controllers;

import lv.emendatus.Destiny_PropMan.annotation.advanced_tenant_controller.*;
import lv.emendatus.Destiny_PropMan.domain.dto.communication.SubmitClaimDTO;
import lv.emendatus.Destiny_PropMan.domain.dto.profile.BookingHistoryDTO;
import lv.emendatus.Destiny_PropMan.domain.dto.profile.TenantDTO_Profile;
import lv.emendatus.Destiny_PropMan.domain.dto.reservation.ETRequestDTO;
import lv.emendatus.Destiny_PropMan.domain.dto.view.*;
import lv.emendatus.Destiny_PropMan.service.implementation.JpaAdvancedTenantService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/tenant")
public class AdvancedTenantController {

    private final JpaAdvancedTenantService service;

    public AdvancedTenantController(JpaAdvancedTenantService service) {
        this.service = service;
    }

    @GetMapping("/property/details/{propertyId}")
    @TenantFunc_ViewPropertyDetails
    public ResponseEntity<PropertiesForTenantsDTO> viewPropertyDetails(@PathVariable Long propertyId, @AuthenticationPrincipal UserDetails userDetails) {
        String tenantUsername = null;
        if (userDetails != null) {
            tenantUsername = userDetails.getUsername();
        }
        PropertiesForTenantsDTO propertyDetails = service.getPropertyDetails(propertyId, tenantUsername);
        return ResponseEntity.ok(propertyDetails);
    }

    @GetMapping("/getProfile/{tenantId}")
    @TenantFunc_GetProfile
    @PreAuthorize("hasAuthority('TENANT')")
    public ResponseEntity<TenantDTO_Profile> getTenantProfile(@PathVariable Long tenantId, Principal principal) throws Exception {
        TenantDTO_Profile tenantProfile = service.getTenantInformation(tenantId, principal);
        return ResponseEntity.ok(tenantProfile);
    }

    @PutMapping("/updateProfile/{tenantId}")
    @TenantFunc_UpdateProfile
    @PreAuthorize("hasAuthority('TENANT')")
    public ResponseEntity<Void> updateTenantProfile(@PathVariable Long tenantId,
                                                    @RequestBody TenantDTO_Profile updatedTenantInfo,
                                                    Principal principal) throws Exception {
        service.updateTenantInformation(tenantId, updatedTenantInfo, principal);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/getHistory/{tenantId}")
    @PreAuthorize("hasAuthority('TENANT')")
    @TenantFunc_ViewBookingHistory
    public ResponseEntity<BookingHistoryDTO> viewBookingHistory(@PathVariable Long tenantId, Principal principal) {
        BookingHistoryDTO bookingHistory = service.viewBookingHistory(tenantId, principal);
        return ResponseEntity.ok(bookingHistory);
    }

    @PostMapping("/saveFavoriteProperty")
    @PreAuthorize("hasAuthority('TENANT')")
    @TenantFunc_SaveFavoriteProperty
    public ResponseEntity<Void> saveFavoriteProperty(@RequestBody FavoritePropertyDTO favoritePropertyDTO, Principal principal) {
        service.saveFavoriteProperty(favoritePropertyDTO, principal);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/getFavoriteProperties/{tenantId}")
    @PreAuthorize("hasAuthority('TENANT')")
    @TenantFunc_GetFavoriteProperties
    public ResponseEntity<List<FavoritePropertyDTO_Profile>> getFavoriteProperties(@PathVariable Long tenantId, Principal principal) {
        List<FavoritePropertyDTO_Profile> favorites = service.getFavoriteProperties(tenantId, principal);
        return ResponseEntity.ok(favorites);
    }

    @PostMapping("/removeFavoriteProperty/{tenantId}/{propertyId}")
    @PreAuthorize("hasAuthority('TENANT')")
    @TenantFunc_RemoveFavoriteProperty
    public ResponseEntity<Void> removeFavoriteProperty(@PathVariable Long tenantId, @PathVariable Long propertyId, Principal principal) {
        service.removePropertyFromFavorites(tenantId, propertyId, principal);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/completed-payments")
    @PreAuthorize("hasAuthority('TENANT')")
    @TenantFunc_ViewCompletedPayments
    public ResponseEntity<List<PaymentsViewDTO>> viewCompletedPayments(@RequestParam Long tenantId, Principal principal) {
        List<PaymentsViewDTO> completedPayments = service.viewCompletedPayments(tenantId, principal);
        return ResponseEntity.ok(completedPayments);
    }

    @GetMapping("/outstanding-payments")
    @PreAuthorize("hasAuthority('TENANT')")
    @TenantFunc_ViewOutstandingPayments
    public ResponseEntity<List<PaymentsViewDTO>> viewOutstandingPayments(@RequestParam Long tenantId, Principal principal) {
        List<PaymentsViewDTO> outstandingPayments = service.viewOutstandingPayments(tenantId, principal);
        return ResponseEntity.ok(outstandingPayments);
    }

    @GetMapping("/current-bookings")
    @PreAuthorize("hasAuthority('TENANT')")
    @TenantFunc_ViewTenantsBookings
    public ResponseEntity<List<BookingsViewDTO>> viewTenantsBookings(@RequestParam Long tenantId, Principal principal) {
        List<BookingsViewDTO> tenantBookings = service.viewTenantsBookings(tenantId, principal);
        return ResponseEntity.ok(tenantBookings);
    }

    @PostMapping("/requestEarlyTermination")
    @PreAuthorize("hasAuthority('TENANT')")
    @TenantFunc_RequestEarlyTermination
    public ResponseEntity<Void> requestEarlyTermination(
            @RequestBody ETRequestDTO requestDTO,
            Principal principal) {
        service.requestEarlyTermination(requestDTO, principal);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/processPayment/{payment_id}")
    @PreAuthorize("hasAuthority('TENANT')")
    @TenantFunc_ProcessPayment
    public ResponseEntity<Void> processPayment(@PathVariable("payment_id") Long paymentId, Principal principal) {
        service.processPayment(paymentId, principal);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/rate_property")
    @PreAuthorize("hasAuthority('TENANT')")
    @TenantFunc_RateProperty
    public ResponseEntity<Void> rateProperty(
            @RequestBody PropertyRatingDTO dto,
            Principal principal) {
        service.rateAProperty(dto.getTenantId(), dto.getBookingId(), dto.getRating(), principal);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/submit_claim")
    @PreAuthorize("hasAuthority('TENANT')")
    @TenantFunc_SubmitClaim
    public ResponseEntity<Void> submitClaim(
            @RequestBody SubmitClaimDTO submitClaimDTO,
            Principal principal) {
        service.submitClaimFromTenant(submitClaimDTO.getBookingId(), submitClaimDTO.getDescription(), principal);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
