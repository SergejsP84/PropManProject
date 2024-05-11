package lv.emendatus.Destiny_PropMan.controllers;

import lv.emendatus.Destiny_PropMan.domain.dto.profile.BookingHistoryDTO;
import lv.emendatus.Destiny_PropMan.domain.dto.profile.TenantDTO_Profile;
import lv.emendatus.Destiny_PropMan.domain.dto.view.*;
import lv.emendatus.Destiny_PropMan.service.interfaces.AdvancedTenantService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tenantservice")
public class AdvancedTenantController {

    private final AdvancedTenantService service;

    public AdvancedTenantController(AdvancedTenantService service) {
        this.service = service;
    }

    @GetMapping("/property/details/{propertyId}")
    public ResponseEntity<PropertiesForTenantsDTO> viewPropertyDetails(@PathVariable Long propertyId, @AuthenticationPrincipal UserDetails userDetails) {
        String tenantId = null;
        if (userDetails != null) {
            tenantId = userDetails.getUsername();
        }
        PropertiesForTenantsDTO propertyDetails = service.getPropertyDetails(propertyId, tenantId);
        return ResponseEntity.ok(propertyDetails);
    }

    @GetMapping("/getProfile/{tenantId}")
    public ResponseEntity<TenantDTO_Profile> getTenantProfile(@PathVariable Long tenantId) {
        TenantDTO_Profile tenantProfile = service.getTenantInformation(tenantId);
        return ResponseEntity.ok(tenantProfile);
    }

    @PutMapping("/updateProfile/{tenantId}")
    public ResponseEntity<Void> updateTenantProfile(@PathVariable Long tenantId,
                                                    @RequestBody TenantDTO_Profile updatedTenantInfo) {
        service.updateTenantInformation(tenantId, updatedTenantInfo);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/getHistory/{tenantId}")
    public ResponseEntity<BookingHistoryDTO> viewBookingHistory(@PathVariable Long tenantId) {
        BookingHistoryDTO bookingHistory = service.viewBookingHistory(tenantId);
        return ResponseEntity.ok(bookingHistory);
    }

    @PostMapping("/saveFavoriteProperty")
    public ResponseEntity<Void> saveFavoriteProperty(@RequestBody FavoritePropertyDTO favoritePropertyDTO) {
        service.saveFavoriteProperty(favoritePropertyDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/getFavoriteProperties/{tenantId}")
    public ResponseEntity<List<FavoritePropertyDTO_Profile>> getFavoriteProperties(@PathVariable Long tenantId) {
        List<FavoritePropertyDTO_Profile> favorites = service.getFavoriteProperties(tenantId);
        return ResponseEntity.ok(favorites);
    }

    @PostMapping("/removeFavoriteProperty/{tenantId}/{propertyId}")
    public ResponseEntity<Void> removeFavoriteProperty(@PathVariable Long tenantId, @PathVariable Long propertyId) {
        service.removePropertyFromFavorites(tenantId, propertyId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/completed-payments")
    public ResponseEntity<List<PaymentsViewDTO>> viewCompletedPayments(@RequestParam Long tenantId) {
        List<PaymentsViewDTO> completedPayments = service.viewCompletedPayments(tenantId);
        return ResponseEntity.ok(completedPayments);
    }

    @GetMapping("/outstanding-payments")
    public ResponseEntity<List<PaymentsViewDTO>> viewOutstandingPayments(@RequestParam Long tenantId) {
        List<PaymentsViewDTO> outstandingPayments = service.viewOutstandingPayments(tenantId);
        return ResponseEntity.ok(outstandingPayments);
    }

    @GetMapping
    public ResponseEntity<List<BookingsViewDTO>> viewTenantsBookings(@RequestParam Long tenantId) {
        List<BookingsViewDTO> tenantBookings = service.viewTenantsBookings(tenantId);
        return ResponseEntity.ok(tenantBookings);
    }
}
