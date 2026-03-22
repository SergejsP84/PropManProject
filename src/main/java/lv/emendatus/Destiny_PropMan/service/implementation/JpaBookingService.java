package lv.emendatus.Destiny_PropMan.service.implementation;

import lv.emendatus.Destiny_PropMan.domain.entity.*;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.BookingStatus;
import lv.emendatus.Destiny_PropMan.exceptions.BookingNotFoundException;
import lv.emendatus.Destiny_PropMan.repository.interfaces.BookingRepository;
import lv.emendatus.Destiny_PropMan.repository.interfaces.PropertyRepository;
import lv.emendatus.Destiny_PropMan.repository.interfaces.TenantRepository;
import lv.emendatus.Destiny_PropMan.service.interfaces.BookingService;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class JpaBookingService implements BookingService {
    private final Logger LOGGER = LogManager.getLogger(JpaBookingService.class);
    private final BookingRepository bookingRepository;
    private final PropertyRepository propertyRepository;
    private final TenantRepository tenantRepository;
    private final JpaTenantService tenantService;
    private final JpaLeasingHistoryService leasingHistoryService;
    private final JpaNumericalConfigService configService;
    private final JpaPropertyDiscountService propertyDiscountService;

    public JpaBookingService(BookingRepository bookingRepository, PropertyRepository propertyRepository, TenantRepository tenantRepository, JpaTenantService tenantService, JpaLeasingHistoryService leasingHistoryService, JpaNumericalConfigService configService, JpaPropertyDiscountService propertyDiscountService) {
        this.bookingRepository = bookingRepository;
        this.propertyRepository = propertyRepository;
        this.tenantRepository = tenantRepository;
        this.tenantService = tenantService;
        this.leasingHistoryService = leasingHistoryService;
        this.configService = configService;
        this.propertyDiscountService = propertyDiscountService;
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
        return new HashSet<>(bookingRepository.findByProperty_Id(property.getId()));
    }

    @Override
    public Set<Booking> getBookingsByTenant(Tenant tenant) {
        return new HashSet<>(bookingRepository.findByTenantId(tenant.getId()));
    }

    @Override
    public Set<Booking> getBookingsByManager(Manager manager) {
        return new HashSet<>(bookingRepository.findByProperty_Manager_Id(manager.getId()));
    }

    @Override
    public List<Booking> getBookingsByDateRange(LocalDate startDate, LocalDate endDate) {
        Timestamp startTs = Timestamp.valueOf(startDate.atStartOfDay());
        Timestamp endTs = Timestamp.valueOf(endDate.atStartOfDay());
        return bookingRepository.findByDateRange(startTs, endTs);
    }

    public List<Booking> getBookingsByDateRangeWithOverlaps(LocalDate startDate, LocalDate endDate) {
        Timestamp startTs = Timestamp.valueOf(startDate.atStartOfDay());
        Timestamp endTs = Timestamp.valueOf(endDate.atStartOfDay());
        return bookingRepository.findOverlappingBookings(startTs, endTs);
    }

    @Override
    public List<Booking> getBookingsByStatus(BookingStatus bookingStatus) {
        return bookingRepository.findByStatus(bookingStatus);
    }

    @Override
    public void updateBookingStatus(Long bookingId, BookingStatus newStatus) {
        bookingRepository.findById(bookingId).ifPresent(booking -> {
            booking.setStatus(newStatus);
            bookingRepository.save(booking);
        });
    }

    @Override
    public Double calculateTotalPrice(Long bookingId) {
        Optional<Booking> optionalBooking = bookingRepository.findById(bookingId);
        if (optionalBooking.isPresent()) {
            Booking booking = optionalBooking.get();
            LocalDateTime startDate = booking.getStartDate().toLocalDateTime();
            LocalDateTime endDate = booking.getEndDate().toLocalDateTime();
            long numberOfDays = 1 + ChronoUnit.DAYS.between(startDate, endDate);
            double totalPrice = 0.0;
            if (numberOfDays >= 30) {
                long fullMonths = numberOfDays / 30;
                totalPrice += fullMonths * booking.getProperty().getPricePerMonth();
                numberOfDays %= 30;
            }
            if (numberOfDays >= 7) {
                long fullWeeks = numberOfDays / 7;
                totalPrice += fullWeeks * booking.getProperty().getPricePerWeek();
                numberOfDays %= 7;
            }
            if (numberOfDays > 0) {
                totalPrice += numberOfDays * booking.getProperty().getPricePerDay();
            }
            double costPerDay = totalPrice / (1 + ChronoUnit.DAYS.between(startDate, endDate));
            double finalPriceWithDiscountsAndSurcharges = 0.00;
            for (LocalDate date = startDate.toLocalDate(); !date.isAfter(endDate.toLocalDate()); date = date.plusDays(1)) {
                int discountOrSurcharge = propertyDiscountService.getDiscountOrSurchargeForCalculations(booking.getProperty().getId(), startDate.toLocalDate(), endDate.toLocalDate(), date);
                finalPriceWithDiscountsAndSurcharges += (costPerDay + (costPerDay * discountOrSurcharge / 100));
            }
            BigDecimal roundedPrice = BigDecimal.valueOf(finalPriceWithDiscountsAndSurcharges).setScale(2, RoundingMode.HALF_UP);
            return roundedPrice.doubleValue();
        } else {
            LOGGER.log(Level.ERROR, "No booking with the specified ID exists in the database.");
            throw new BookingNotFoundException("No booking found with ID: " + bookingId);
        }
    }

    // AUXILIARY METHOD
    public int calculateDaysDifference(Timestamp endDate) {
        Instant currentInstant = Instant.now();
        LocalDate currentDate = currentInstant.atZone(java.time.ZoneOffset.UTC).toLocalDate();
        Instant endInstant = endDate.toInstant();
        LocalDate endDateLocalDate = endInstant.atZone(java.time.ZoneOffset.UTC).toLocalDate();
        long difference = ChronoUnit.DAYS.between(currentDate, endDateLocalDate);
        return (int) difference;
    }
}
