package lv.emendatus.Destiny_PropMan.controllers;

import lv.emendatus.Destiny_PropMan.domain.dto.profile.BookingHistoryDTO;
import lv.emendatus.Destiny_PropMan.domain.dto.profile.TenantDTO_Profile;
import lv.emendatus.Destiny_PropMan.domain.dto.view.FavoritePropertyDTO;
import lv.emendatus.Destiny_PropMan.domain.dto.view.FavoritePropertyDTO_Profile;
import lv.emendatus.Destiny_PropMan.domain.dto.view.PropertiesForTenantsDTO;
import lv.emendatus.Destiny_PropMan.service.interfaces.AdvancedTenantService;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<PropertiesForTenantsDTO> viewPropertyDetails(@PathVariable Long propertyId) {
        PropertiesForTenantsDTO propertyDetails = service.getPropertyDetails(propertyId);
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


}
