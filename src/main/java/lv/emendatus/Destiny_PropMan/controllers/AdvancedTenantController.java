package lv.emendatus.Destiny_PropMan.controllers;

import lv.emendatus.Destiny_PropMan.annotation.advanced_tenant_controller.*;
import lv.emendatus.Destiny_PropMan.domain.dto.profile.BookingHistoryDTO;
import lv.emendatus.Destiny_PropMan.domain.dto.profile.TenantDTO_Profile;
import lv.emendatus.Destiny_PropMan.domain.dto.view.*;
import lv.emendatus.Destiny_PropMan.domain.entity.TenantPayment;
import lv.emendatus.Destiny_PropMan.service.implementation.JpaAdvancedTenantService;
import lv.emendatus.Destiny_PropMan.service.interfaces.AdvancedTenantService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/tenantfunction")
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
    public ResponseEntity<TenantDTO_Profile> getTenantProfile(@PathVariable Long tenantId) {
        TenantDTO_Profile tenantProfile = service.getTenantInformation(tenantId);
        return ResponseEntity.ok(tenantProfile);
    }

    @PutMapping("/updateProfile/{tenantId}")
    @TenantFunc_UpdateProfile
    public ResponseEntity<Void> updateTenantProfile(@PathVariable Long tenantId,
                                                    @RequestBody TenantDTO_Profile updatedTenantInfo) {
        service.updateTenantInformation(tenantId, updatedTenantInfo);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/getHistory/{tenantId}")
    @TenantFunc_ViewBookingHistory
    public ResponseEntity<BookingHistoryDTO> viewBookingHistory(@PathVariable Long tenantId) {
        BookingHistoryDTO bookingHistory = service.viewBookingHistory(tenantId);
        return ResponseEntity.ok(bookingHistory);
    }

    @PostMapping("/saveFavoriteProperty")
    @TenantFunc_SaveFavoriteProperty
    public ResponseEntity<Void> saveFavoriteProperty(@RequestBody FavoritePropertyDTO favoritePropertyDTO) {
        service.saveFavoriteProperty(favoritePropertyDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/getFavoriteProperties/{tenantId}")
    @TenantFunc_GetFavoriteProperties
    public ResponseEntity<List<FavoritePropertyDTO_Profile>> getFavoriteProperties(@PathVariable Long tenantId) {
        List<FavoritePropertyDTO_Profile> favorites = service.getFavoriteProperties(tenantId);
        return ResponseEntity.ok(favorites);
    }

    @PostMapping("/removeFavoriteProperty/{tenantId}/{propertyId}")
    @TenantFunc_RemoveFavoriteProperty
    public ResponseEntity<Void> removeFavoriteProperty(@PathVariable Long tenantId, @PathVariable Long propertyId) {
        service.removePropertyFromFavorites(tenantId, propertyId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/completed-payments")
    @TenantFunc_ViewCompletedPayments
    public ResponseEntity<List<PaymentsViewDTO>> viewCompletedPayments(@RequestParam Long tenantId) {
        List<PaymentsViewDTO> completedPayments = service.viewCompletedPayments(tenantId);
        return ResponseEntity.ok(completedPayments);
    }

    @GetMapping("/outstanding-payments")
    @TenantFunc_ViewOutstandingPayments
    public ResponseEntity<List<PaymentsViewDTO>> viewOutstandingPayments(@RequestParam Long tenantId) {
        List<PaymentsViewDTO> outstandingPayments = service.viewOutstandingPayments(tenantId);
        return ResponseEntity.ok(outstandingPayments);
    }

    @GetMapping("/current-bookings")
    @TenantFunc_ViewTenantsBookings
    public ResponseEntity<List<BookingsViewDTO>> viewTenantsBookings(@RequestParam Long tenantId) {
        List<BookingsViewDTO> tenantBookings = service.viewTenantsBookings(tenantId);
        return ResponseEntity.ok(tenantBookings);
    }

    @PostMapping("/requestEarlyTermination")
    @TenantFunc_RequestEarlyTermination
    public ResponseEntity<Void> requestEarlyTermination(
            @RequestParam Long bookingId,
            @RequestParam LocalDateTime terminationDate,
            @RequestParam(required = false) String comment) {
        service.requestEarlyTermination(bookingId, terminationDate, comment);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/processPayment")
    @TenantFunc_ProcessPayment
    public ResponseEntity<Void> processPayment(@RequestBody TenantPayment tenantPayment) {
        service.processPayment(tenantPayment);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/rate_property/{tenant_id}/{booking_id}/{rating}")
    @TenantFunc_RateProperty
    public ResponseEntity<Void> rateProperty(
            @PathVariable("tenant_id") Long tenantId,
            @PathVariable("booking_id") Long bookingId,
            @PathVariable("rating") Integer rating) {
        service.rateAProperty(tenantId, bookingId, rating);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
