package lv.emendatus.Destiny_PropMan.service.interfaces;
import lv.emendatus.Destiny_PropMan.domain.entity.*;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.BookingStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface BookingService {
    List<Booking> getAllBookings();
    Optional<Booking> getBookingById(Long id);
    void addBooking(Booking booking);
    void deleteBooking(Long id);
    Set<Booking> getBookingsByProperty(Property property);
    Set<Booking> getBookingsByTenant(Tenant tenant);
    List<Booking> getBookingsByDateRange(LocalDate startDate, LocalDate endDate);
    public List<Booking> getBookingsByDateRangeWithOverlaps(LocalDate startDate, LocalDate endDate);
    List<Booking> getBookingsByStatus(BookingStatus bookingStatus);
    void updateBookingStatus(Long bookingId, BookingStatus newStatus);
    Double calculateTotalPrice(Long bookingId);
}