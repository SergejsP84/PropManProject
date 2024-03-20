package lv.emendatus.Destiny_PropMan.scheduled_tasks;

import lv.emendatus.Destiny_PropMan.domain.entity.Booking;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.BookingStatus;
import lv.emendatus.Destiny_PropMan.service.implementation.JpaBookingService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class ScheduledTasks {

    private final JpaBookingService bookingService;

    public ScheduledTasks(JpaBookingService bookingService) {
        this.bookingService = bookingService;
    }

    @Scheduled(cron = "0 0 1 * * *")
    public void changeBookingStatus() {
        LocalDateTime today = LocalDateTime.now();
        List<Booking> currentBookings = bookingService.getAllBookings();
        for (Booking booking : currentBookings) {
            LocalDateTime startDate = booking.getStartDate().toLocalDateTime();
            LocalDateTime endDate = booking.getEndDate().toLocalDateTime();
            if ((today.isEqual(startDate) || today.isAfter(startDate)) && booking.getStatus().equals(BookingStatus.CONFIRMED)) {
                bookingService.updateBookingStatus(booking.getId(), BookingStatus.CURRENT);
            }
            if (today.isAfter(endDate)) {
                bookingService.updateBookingStatus(booking.getId(), BookingStatus.OVER);
            }
        }

    }
}
