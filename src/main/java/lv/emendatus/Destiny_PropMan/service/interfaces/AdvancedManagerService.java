package lv.emendatus.Destiny_PropMan.service.interfaces;

import lv.emendatus.Destiny_PropMan.domain.dto.managerial.*;
import lv.emendatus.Destiny_PropMan.domain.dto.reference.PropertyDTO;
import lv.emendatus.Destiny_PropMan.domain.dto.reservation.BookingDTO_Reservation;
import lv.emendatus.Destiny_PropMan.domain.entity.Bill;
import lv.emendatus.Destiny_PropMan.domain.entity.Booking;
import lv.emendatus.Destiny_PropMan.domain.entity.PropertyDiscount;

import java.time.LocalDate;
import java.util.List;

public interface AdvancedManagerService {
    ManagerProfileDTO getManagerProfile(Long managerId);
    void updateManagerProfile(Long managerId, ManagerProfileDTO updatedProfile);

    List<ManagerPropertyDTO> getManagerPropertyPortfolio(Long managerId);

    ManagerReservationDTO viewReservationsForManager(Long managerId);

    public ManagerReservationDTO viewReservationsForProperty(Long propertyId);

    List<BookingDTO_Reservation> getBookingsForProperty(Long propertyId);

    List<BookingDTO_Reservation> getBookingsForManager(Long managerId);

    void submitClaimfromManager(Long bookingId, String description);

    void closeBookingByManager(Long bookingId);

    FinancialStatementDTO generateFinancialStatement(LocalDate periodStart, LocalDate periodEnd, Long managerId);

    public List<Bill> getUnpaidBillsForProperty(Long propertyId);

    public List<Bill> getUnpaidBillsForManager(Long managerId);

    public void addProperty(PropertyAdditionDTO propertyDTO);

    public PropertyDiscount setDiscountOrSurcharge (PropertyDiscountDTO propertyDiscountDTO);

    public void resetDiscountsAndSurcharges(Long propertyId, LocalDate periodStart, LocalDate periodEnd);

    public List<Booking> getBookingsPendingApproval (Long managerId);

    public void approveBooking (Long bookingId);

    public void declineBooking (Long bookingId);

    public void acceptEarlyTermination(Long requestId, String reply);

    public void declineEarlyTermination(Long requestId, String reply);

    void removeProperty (Long propertyId);
}
