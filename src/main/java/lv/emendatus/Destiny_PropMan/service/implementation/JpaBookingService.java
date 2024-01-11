package lv.emendatus.Destiny_PropMan.service.implementation;

import lv.emendatus.Destiny_PropMan.domain.entity.Booking;
import lv.emendatus.Destiny_PropMan.domain.entity.Property;
import lv.emendatus.Destiny_PropMan.domain.entity.Tenant;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.BookingStatus;
import lv.emendatus.Destiny_PropMan.repository.interfaces.BookingRepository;
import lv.emendatus.Destiny_PropMan.repository.interfaces.PropertyRepository;
import lv.emendatus.Destiny_PropMan.repository.interfaces.TenantRepository;
import lv.emendatus.Destiny_PropMan.service.interfaces.BookingService;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class JpaBookingService implements BookingService {
    private final Logger LOGGER = LogManager.getLogger(JpaPropertyService.class);
    private final BookingRepository bookingRepository;
    private final PropertyRepository propertyRepository;
    private final TenantRepository tenantRepository;
    public JpaBookingService(BookingRepository bookingRepository, PropertyRepository propertyRepository, TenantRepository tenantRepository) {
        this.bookingRepository = bookingRepository;
        this.propertyRepository = propertyRepository;
        this.tenantRepository = tenantRepository;
    }
    @Override
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }
    @Override
    public Optional<Booking> getBookingById(Long id) {
        return bookingRepository.findById(id);
    }
    @Override
    public void addBooking(Booking booking) {
        bookingRepository.save(booking);
    }

    @Override
    public void deleteBooking(Long id) {
        bookingRepository.deleteById(id);
    }

    @Override
    public Set<Booking> getBookingsByProperty(Property property) {
        return getAllBookings().stream()
                .filter(booking -> {
                    Property bookingProperty = booking.getProperty();
                    return bookingProperty != null && bookingProperty.getId() != null && bookingProperty.getId().equals(property.getId());
                })
                .collect(Collectors.toSet());
    }
    @Override
    public Set<Booking> getBookingsByTenant(Tenant tenant) {
        return getAllBookings().stream()
                .filter(booking -> {
                    Long bookingTenantId = booking.getTenantId();
                    return bookingTenantId != null && bookingTenantId.equals(tenant.getId());
                })
                .collect(Collectors.toSet());
    }

    @Override
    public List<Booking> getBookingsByDateRange(LocalDate startDate, LocalDate endDate) {
        List<Booking> allBookings = bookingRepository.findAll();
        return allBookings.stream()
                .filter(booking -> {
                    Timestamp bookingStartTimestamp = booking.getStartDate();
                    Timestamp bookingEndTimestamp = booking.getEndDate();
                    LocalDate bookingStartDate = bookingStartTimestamp.toLocalDateTime().toLocalDate();
                    LocalDate bookingEndDate = bookingEndTimestamp.toLocalDateTime().toLocalDate();
                    return (bookingStartDate.isEqual(startDate) || bookingStartDate.isAfter(startDate))
                            && (bookingEndDate.isEqual(endDate) || bookingEndDate.isBefore(endDate));
                })
                .collect(Collectors.toList());
    }

    public List<Booking> getBookingsByDateRangeWithOverlaps(LocalDate startDate, LocalDate endDate) {
        List<Booking> allBookings = bookingRepository.findAll();
        return allBookings.stream()
                .filter(booking -> {
                    Timestamp bookingStartTimestamp = booking.getStartDate();
                    Timestamp bookingEndTimestamp = booking.getEndDate();
                    LocalDate bookingStartDate = bookingStartTimestamp.toLocalDateTime().toLocalDate();
                    LocalDate bookingEndDate = bookingEndTimestamp.toLocalDateTime().toLocalDate();
                    return isDateRangeOverlap(startDate, endDate, bookingStartDate, bookingEndDate);
                })
                .collect(Collectors.toList());
    }

    private boolean isDateRangeOverlap(LocalDate start1, LocalDate end1, LocalDate start2, LocalDate end2) {
        return !(end1.isBefore(start2) || start1.isAfter(end2));
    }

    @Override
    public List<Booking> getBookingsByStatus(BookingStatus bookingStatus) {
        List<Booking> allBookings = bookingRepository.findAll();
        return allBookings.stream()
                .filter(booking -> {
                    return booking.getStatus().equals(bookingStatus);
                }).collect(Collectors.toList());
    }

    @Override
    public void updateBookingStatus(Long bookingId, BookingStatus newStatus) {
        bookingRepository.findById(bookingId).ifPresent(booking -> booking.setStatus(newStatus));
    }

    @Override
    public Double calculateTotalPrice(Long bookingId) {
        Optional<Booking> optionalBooking = bookingRepository.findById(bookingId);
        if (optionalBooking.isPresent()) {
            Booking booking = optionalBooking.get();
            long numberOfDays = 1 + ChronoUnit.DAYS.between(booking.getStartDate().toLocalDateTime(), booking.getEndDate().toLocalDateTime());
            double totalPrice = 0.0;
            // months
            if (numberOfDays >= 30) {
                long fullMonths = numberOfDays / 30;
                totalPrice += fullMonths * booking.getProperty().getPricePerMonth();
                numberOfDays %= 30;
            }
            //weeks
            if (numberOfDays >= 7) {
                long fullWeeks = numberOfDays / 7;
                totalPrice += fullWeeks * booking.getProperty().getPricePerWeek();
                numberOfDays %= 7;
            }
            //remaining days
            totalPrice += numberOfDays * booking.getProperty().getPricePerDay();
            return totalPrice;
        } else {
            LOGGER.log(Level.ERROR, "No booking with the specified ID exists in the database.");
            // TODO: Handle the case where the booking with the given ID is not found
            return null;
        }
    }
}
