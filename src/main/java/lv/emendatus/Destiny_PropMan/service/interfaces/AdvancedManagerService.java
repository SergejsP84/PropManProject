package lv.emendatus.Destiny_PropMan.service.interfaces;

import lv.emendatus.Destiny_PropMan.domain.dto.managerial.FinancialStatementDTO;
import lv.emendatus.Destiny_PropMan.domain.dto.managerial.ManagerProfileDTO;
import lv.emendatus.Destiny_PropMan.domain.dto.managerial.ManagerPropertyDTO;
import lv.emendatus.Destiny_PropMan.domain.dto.managerial.ManagerReservationDTO;
import lv.emendatus.Destiny_PropMan.domain.dto.reservation.BookingDTO_Reservation;
import lv.emendatus.Destiny_PropMan.domain.entity.Bill;

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

    public void submitClaimfromManager(Long bookingId, String description);

    void closeBookingByManager(Long bookingId);

    FinancialStatementDTO generateFinancialStatement(LocalDate periodStart, LocalDate periodEnd, Long managerId);

    public List<Bill> getUnpaidBillsForProperty(Long propertyId);

    public List<Bill> getUnpaidBillsForManager(Long managerId);
}
