package lv.emendatus.Destiny_PropMan.service.interfaces;

import lv.emendatus.Destiny_PropMan.domain.dto.profile.BookingHistoryDTO;
import lv.emendatus.Destiny_PropMan.domain.dto.profile.TenantDTO_Profile;
import lv.emendatus.Destiny_PropMan.domain.dto.reservation.ConfirmationDTO;
import lv.emendatus.Destiny_PropMan.domain.dto.reservation.ReservationCancellationDTO;
import lv.emendatus.Destiny_PropMan.domain.dto.reservation.ReservationRequestDTO;
import lv.emendatus.Destiny_PropMan.domain.dto.view.FavoritePropertyDTO;
import lv.emendatus.Destiny_PropMan.domain.dto.view.FavoritePropertyDTO_Profile;
import lv.emendatus.Destiny_PropMan.domain.dto.view.ManagersForTenantsDTO;
import lv.emendatus.Destiny_PropMan.domain.dto.view.PropertiesForTenantsDTO;
import lv.emendatus.Destiny_PropMan.domain.dto.view.ReviewDTO;

import java.util.List;

public interface AdvancedTenantService {

    // View Property Details
    PropertiesForTenantsDTO getPropertyDetails(Long propertyId);

    // Manage Profile
    TenantDTO_Profile getTenantProfile(Long tenantId);

    // Communicate with Hosts
    List<ManagersForTenantsDTO> getManagersForProperty(Long propertyId);


    // Save Favorites


    void saveFavoriteProperty(FavoritePropertyDTO favoritePropertyDTO);
    List<FavoritePropertyDTO_Profile> getFavoriteProperties(Long tenantId);
    void removePropertyFromFavorites(Long tenantId, Long propertyId);

    // More profile thingies
    TenantDTO_Profile getTenantInformation(Long tenantId);
    void updateTenantInformation(Long tenantId, TenantDTO_Profile updatedTenantInfo);
    BookingHistoryDTO viewBookingHistory(Long tenantId);

    // Lob a claim with this one
    void submitClaimfromTenant(Long bookingId, String description);
}
