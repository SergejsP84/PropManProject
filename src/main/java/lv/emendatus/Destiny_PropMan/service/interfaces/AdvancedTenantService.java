package lv.emendatus.Destiny_PropMan.service.interfaces;

import lv.emendatus.Destiny_PropMan.domain.dto.profile.BookingHistoryDTO;
import lv.emendatus.Destiny_PropMan.domain.dto.profile.TenantDTO_Profile;
import lv.emendatus.Destiny_PropMan.domain.dto.reservation.ConfirmationDTO;
import lv.emendatus.Destiny_PropMan.domain.dto.reservation.ReservationCancellationDTO;
import lv.emendatus.Destiny_PropMan.domain.dto.reservation.ReservationRequestDTO;
import lv.emendatus.Destiny_PropMan.domain.dto.view.*;
import lv.emendatus.Destiny_PropMan.domain.entity.EarlyTerminationRequest;
import lv.emendatus.Destiny_PropMan.domain.entity.TenantPayment;

import java.time.LocalDateTime;
import java.util.List;

public interface AdvancedTenantService {

    // View Property Details
    PropertiesForTenantsDTO getPropertyDetails(Long propertyId, String tenantLogin);


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

    // View payments and bookings
    List<PaymentsViewDTO> viewCompletedPayments(Long tenantId);
    List<PaymentsViewDTO> viewOutstandingPayments(Long tenantId);
    List<BookingsViewDTO> viewTenantsBookings(Long tenantId);

    // Request an early termination of a booking
    void requestEarlyTermination(Long bookingId, LocalDateTime terminationDate, String comment);

    // Pay the booking
    void processPayment(TenantPayment tenantPayment);

    void rateAProperty(Long tenantId, Long bookingId, Integer rating);
}
