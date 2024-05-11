package lv.emendatus.Destiny_PropMan.service.interfaces;

import lv.emendatus.Destiny_PropMan.domain.dto.managerial.*;
import lv.emendatus.Destiny_PropMan.domain.dto.reference.PropertyDTO;
import lv.emendatus.Destiny_PropMan.domain.dto.reservation.BookingDTO_Reservation;
import lv.emendatus.Destiny_PropMan.domain.entity.Bill;
import lv.emendatus.Destiny_PropMan.domain.entity.Booking;
import lv.emendatus.Destiny_PropMan.domain.entity.PropertyDiscount;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

public interface AdvancedManagerService {
    ManagerProfileDTO getManagerProfile(Long managerId);
    void updateManagerProfile(Long managerId, ManagerProfileDTO updatedProfile);

    List<ManagerPropertyDTO> getManagerPropertyPortfolio(Long managerId);

    ManagerReservationDTO viewReservationsForManager(Long managerId);

    ManagerReservationDTO viewReservationsForProperty(Long propertyId);

    List<BookingDTO_Reservation> getBookingsForProperty(Long propertyId);

    List<BookingDTO_Reservation> getBookingsForManager(Long managerId);

    void submitClaimfromManager(Long bookingId, String description);

    void closeBookingByManager(Long bookingId);

    FinancialStatementDTO generateFinancialStatement(LocalDate periodStart, LocalDate periodEnd, Long managerId);

    List<Bill> getUnpaidBillsForProperty(Long propertyId);

    List<Bill> getUnpaidBillsForManager(Long managerId);

    void addProperty(PropertyAdditionDTO propertyDTO);

    PropertyDiscount setDiscountOrSurcharge (PropertyDiscountDTO propertyDiscountDTO);

    void resetDiscountsAndSurcharges(Long propertyId, LocalDate periodStart, LocalDate periodEnd);

    List<Booking> getBookingsPendingApproval (Long managerId);

    void approveBooking (Long bookingId);

    void declineBooking (Long bookingId);

    void acceptEarlyTermination(Long requestId, String reply);

    void declineEarlyTermination(Long requestId, String reply);

    void removeProperty (Long propertyId);
    void uploadPhotos(Long propertyId, MultipartFile[] files);
    void removePhoto(Long propertyId, String photoUrl);

    void makePropertyUnavailable(Long propertyId, LocalDate periodStart, LocalDate periodEnd);
}
