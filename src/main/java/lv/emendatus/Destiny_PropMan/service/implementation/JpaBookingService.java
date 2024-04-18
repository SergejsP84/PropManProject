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
    private final JpaTenantService tenantService;
    private final JpaLeasingHistoryService leasingHistoryService;
//    private final JpaClaimService claimService;
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
    public Set<Booking> getBookingsByManager(Manager manager) {
        return getAllBookings().stream()
                .filter(booking -> {
                    Long bookingManagerId = booking.getProperty().getManager().getId();
                    return bookingManagerId != null && bookingManagerId.equals(manager.getId());
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
            LocalDateTime startDate = booking.getStartDate().toLocalDateTime();
            LocalDateTime endDate = booking.getEndDate().toLocalDateTime();
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
            double costPerDay = totalPrice / numberOfDays;
            double finalPriceWithDiscountsAndSurcharges = 0.00;
            for (LocalDate date = startDate.toLocalDate(); !date.isAfter(endDate.toLocalDate()); date = date.plusDays(1)) {
                int discountOrSurcharge = propertyDiscountService.getDiscountOrSurchargeForCalculations(booking.getProperty().getId(), startDate.toLocalDate(), endDate.toLocalDate(), date);
                finalPriceWithDiscountsAndSurcharges += (costPerDay + (costPerDay * discountOrSurcharge / 100));
            }
            BigDecimal finalPrice = BigDecimal.valueOf(finalPriceWithDiscountsAndSurcharges);
            BigDecimal roundedPrice = finalPrice.setScale(2, RoundingMode.HALF_UP);
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
        long difference = ChronoUnit.DAYS.between(endDateLocalDate, currentDate);
        return (int) difference;
    }

}
