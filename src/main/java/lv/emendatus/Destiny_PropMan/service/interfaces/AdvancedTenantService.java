package lv.emendatus.Destiny_PropMan.service.interfaces;

import lv.emendatus.Destiny_PropMan.domain.dto.profile.BookingHistoryDTO;
import lv.emendatus.Destiny_PropMan.domain.dto.profile.TenantDTO_Profile;
import lv.emendatus.Destiny_PropMan.domain.dto.reservation.ConfirmationDTO;
import lv.emendatus.Destiny_PropMan.domain.dto.reservation.ETRequestDTO;
import lv.emendatus.Destiny_PropMan.domain.dto.reservation.ReservationCancellationDTO;
import lv.emendatus.Destiny_PropMan.domain.dto.reservation.ReservationRequestDTO;
import lv.emendatus.Destiny_PropMan.domain.dto.view.*;
import lv.emendatus.Destiny_PropMan.domain.entity.EarlyTerminationRequest;
import lv.emendatus.Destiny_PropMan.domain.entity.TenantPayment;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

public interface AdvancedTenantService {

    // View Property Details
    PropertiesForTenantsDTO getPropertyDetails(Long propertyId, String tenantLogin);


    // Save Favorites
    void saveFavoriteProperty(FavoritePropertyDTO favoritePropertyDTO, Principal principal);
    List<FavoritePropertyDTO_Profile> getFavoriteProperties(Long tenantId, Principal principal);
    void removePropertyFromFavorites(Long tenantId, Long propertyId, Principal principal);

    // More profile thingies
    TenantDTO_Profile getTenantInformation(Long tenantId, Principal principal) throws Exception;
    void updateTenantInformation(Long tenantId, TenantDTO_Profile updatedTenantInfo, Principal principal) throws Exception;
    BookingHistoryDTO viewBookingHistory(Long tenantId, Principal principal);

    // Lob a claim with this one
    void submitClaimFromTenant(Long bookingId, String description, Principal principal);

    // View payments and bookings
    List<PaymentsViewDTO> viewCompletedPayments(Long tenantId, Principal principal);
    List<PaymentsViewDTO> viewOutstandingPayments(Long tenantId, Principal principal);
    List<BookingsViewDTO> viewTenantsBookings(Long tenantId, Principal principal);

    // Request an early termination of a booking
    void requestEarlyTermination(ETRequestDTO dto, Principal principal);

    // Pay the booking
    void processPayment(Long paymentId, Principal principal);

    void rateAProperty(Long tenantId, Long bookingId, Integer rating, Principal principal);
}
