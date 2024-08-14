package lv.emendatus.Destiny_PropMan.service.interfaces;

import lv.emendatus.Destiny_PropMan.domain.dto.managerial.*;
import lv.emendatus.Destiny_PropMan.domain.dto.reference.PropertyDTO;
import lv.emendatus.Destiny_PropMan.domain.dto.reservation.BookingDTO_Reservation;
import lv.emendatus.Destiny_PropMan.domain.entity.Bill;
import lv.emendatus.Destiny_PropMan.domain.entity.Booking;
import lv.emendatus.Destiny_PropMan.domain.entity.PropertyDiscount;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

public interface AdvancedManagerService {
    PublicManagerProfileDTO getManagerProfile(Long managerId);
    void updateManagerProfile(Long managerId, ManagerProfileDTO updatedProfile, Principal principal);

    List<ManagerPropertyDTO> getManagerPropertyPortfolio(Long managerId);

    ManagerReservationDTO viewReservationsForManager(Long managerId, Principal principal);

    ManagerReservationDTO viewReservationsForProperty(Long propertyId, Principal principal);

    List<BookingDTO_Reservation> getBookingsForProperty(Long propertyId, Principal principal);

    List<BookingDTO_Reservation> getBookingsForManager(Long managerId, Principal principal);

    void submitClaimfromManager(Long bookingId, String description);

    void closeBookingByManager(Long bookingId);

    FinancialStatementDTO generateFinancialStatement(LocalDate periodStart, LocalDate periodEnd, Long managerId, Principal principal);

    List<Bill> getUnpaidBillsForProperty(Long propertyId, Principal principal);

    List<Bill> getUnpaidBillsForManager(Long managerId, Principal principal);

    void addProperty(PropertyAdditionDTO propertyDTO);

    PropertyDiscount setDiscountOrSurcharge (PropertyDiscountDTO propertyDiscountDTO, Principal principal);

    void resetDiscountsAndSurcharges(Long propertyId, LocalDate periodStart, LocalDate periodEnd, Principal principal);

    List<Booking> getBookingsPendingApproval (Long managerId, Principal principal);

    void approveBooking (Long bookingId, Principal principal);

    void declineBooking (Long bookingId);

    void acceptEarlyTermination(Long requestId, String reply);

    void declineEarlyTermination(Long requestId, String reply);

    void removeProperty (Long propertyId);
    void uploadPhotos(Long propertyId, MultipartFile[] files);
    void removePhoto(Long propertyId, String photoUrl);

    void makePropertyUnavailable(Long propertyId, LocalDate periodStart, LocalDate periodEnd);
    void unlockProperty(Long propertyId);

    void addBillToProperty(BillAdditionDTO dto, Long propertyId);
    void deleteBillFromProperty(Long billId, Long propertyId);

    void rateATenant(Long tenantId, Long managerId, Long bookingId, Integer rating);
    void updateProperty(Long propertyId, PropertyUpdateDTO propertyDTO, Principal principal);
}
