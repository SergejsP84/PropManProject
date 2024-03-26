package lv.emendatus.Destiny_PropMan.service.implementation;

import lv.emendatus.Destiny_PropMan.domain.dto.reservation.ConfirmationDTO;
import lv.emendatus.Destiny_PropMan.domain.dto.reservation.ErrorDTO;
import lv.emendatus.Destiny_PropMan.domain.dto.reservation.ReservationCancellationDTO;
import lv.emendatus.Destiny_PropMan.domain.dto.reservation.ReservationRequestDTO;
import lv.emendatus.Destiny_PropMan.domain.entity.Booking;
import lv.emendatus.Destiny_PropMan.domain.entity.NumericalConfig;
import lv.emendatus.Destiny_PropMan.domain.entity.Property;
import lv.emendatus.Destiny_PropMan.domain.entity.TenantPayment;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.BookingStatus;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.PropertyStatus;
import lv.emendatus.Destiny_PropMan.repository.interfaces.BookingRepository;
import lv.emendatus.Destiny_PropMan.repository.interfaces.PropertyRepository;
import lv.emendatus.Destiny_PropMan.service.interfaces.ReservationService;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class JpaReservationService implements ReservationService {
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private PropertyRepository propertyRepository;
    private final Logger LOGGER = LogManager.getLogger(JpaPropertyService.class);
    @Autowired
    private JpaBookingService bookingService;
    @Autowired
    private JpaTenantService tenantService;
    @Autowired
    private JpaTenantPaymentService paymentService;
    @Autowired
    private JpaNumericalConfigService configService;
    @Override
    public ConfirmationDTO makeReservation(ReservationRequestDTO reservationRequest) {
        Long propertyId = reservationRequest.getPropertyId();
        Long tenantId = reservationRequest.getTenantId();
        Timestamp startDate = reservationRequest.getStartDate();
        Timestamp endDate = reservationRequest.getEndDate();
        Long returnPropertyId = 0L;
        Timestamp returnStartDate = Timestamp.valueOf(LocalDateTime.now());
        Timestamp returnEndDate = Timestamp.valueOf(LocalDateTime.now());
        Optional<Property> property = propertyRepository.findById(propertyId);
        boolean everythingAllRight = true;

        if (property.isPresent()) {

            if (!property.get().getStatus().equals(PropertyStatus.AVAILABLE)) {
                everythingAllRight = false;
                System.out.println("Sorry, this property is not available for booking for the time being!");
                LOGGER.log(Level.INFO, "This property is currently unavailable for booking.");
                return new ConfirmationDTO("Reservation failed, this property is currently marked as unavailable!");
            }

            if (tenantService.getTenantById(tenantId).isEmpty()) {
                everythingAllRight = false;
                System.out.println("Cannot find the tenant!");
                LOGGER.log(Level.ERROR, "Could not find the specified tenant.");
                return new ConfirmationDTO("Reservation failed, tenant not found!");
            }

            if (!(bookingService.
                    getBookingsByDateRangeWithOverlaps(startDate.toLocalDateTime().toLocalDate(),
                            endDate.toLocalDateTime().toLocalDate())).isEmpty()) {
                List<Booking> existingBookings = bookingService.
                        getBookingsByDateRangeWithOverlaps(startDate.toLocalDateTime().toLocalDate(),
                                endDate.toLocalDateTime().toLocalDate());
                List<Booking> specificPropertyBookings = new ArrayList<>();
                for (Booking booking : existingBookings) {
                    if (booking.getProperty().getId().equals(propertyId)) specificPropertyBookings.add(booking);
                }
                for (Booking booking : specificPropertyBookings) {
                    if (!booking.getStatus().equals(BookingStatus.CANCELLED)) {
                        everythingAllRight = false;
                        LOGGER.log(Level.ERROR, "This property is not available for the desired period!");
                        return new ConfirmationDTO("Reservation failed, this property is not available for the desired period!");
                    }
                }
            }

            if (everythingAllRight) {
                Booking newBooking = new Booking();
                newBooking.setProperty(property.get());
                newBooking.setTenantId(tenantId);
                newBooking.setStartDate(startDate);
                newBooking.setEndDate(endDate);
                newBooking.setPaid(false);
                newBooking.setStatus(BookingStatus.PENDING_APPROVAL);
                bookingRepository.save(newBooking);
                Long bookingId = newBooking.getId();
                returnPropertyId = property.get().getId();
                returnStartDate = newBooking.getStartDate();
                returnEndDate = newBooking.getEndDate();
                TenantPayment payment = new TenantPayment();
                payment.setTenant(tenantService.getTenantById(tenantId).get());
                payment.setFeePaidToManager(false);
                payment.setReceivedFromTenant(false);
                Double amount = bookingService.calculateTotalPrice(bookingId);
                payment.setAmount(amount);
                payment.setAssociatedPropertyId(propertyId);
                payment.setManagerId(property.get().getManager().getId());
                payment.setAssociatedBookingId(bookingId);
                int paymentPeriodDaysSetOrDefault = 8;
                for (NumericalConfig config : configService.getSystemSettings()) {
                    if (config.getName().equals("PaymentPeriodDays")) paymentPeriodDaysSetOrDefault = config.getValue().intValue();
                }
                Timestamp receiptDue = Timestamp.valueOf(LocalDateTime.now());
                if (bookingService.calculateDaysDifference(newBooking.getStartDate()) < paymentPeriodDaysSetOrDefault) {
                    receiptDue = Timestamp.valueOf(LocalDateTime.now().plusDays(1));
                    System.out.println("Late booking - must be paid within 24 hours!");
                } else receiptDue = Timestamp.valueOf(startDate.toLocalDateTime().minusDays(7));
                payment.setReceiptDue(receiptDue);
                int interestChargedByTheSystemSetOrDefault = 10;
                for (NumericalConfig config : configService.getSystemSettings()) {
                    if (config.getName().equals("SystemInterestRate")) interestChargedByTheSystemSetOrDefault = config.getValue().intValue();
                }
                payment.setManagerPayment(amount - (amount / 100 * interestChargedByTheSystemSetOrDefault));
                paymentService.addTenantPayment(payment);
            }

        } else {
            System.out.println("Property not found!");
            LOGGER.log(Level.ERROR, "No property with the specified ID exists in the database.");
            return new ConfirmationDTO("Reservation failed, property not found!");
        }

            if (everythingAllRight) {
            return new ConfirmationDTO("Successfully booked property " + returnPropertyId + " for the period of " + returnStartDate + " through " + returnEndDate + "!");
            } else {
            LOGGER.log(Level.ERROR, "Unidentified error.");
            return new ConfirmationDTO("An unidentified error has occurred");
            }
    }

    @Override
    public ResponseEntity<String> cancelReservation(ReservationCancellationDTO cancellationRequest) {
        Long reservationId = cancellationRequest.getReservationId();
        Optional<Booking> bookingOptional = bookingRepository.findById(reservationId);
        if (bookingOptional.isPresent()) {
            Booking booking = bookingOptional.get();
                booking.setStatus(BookingStatus.CANCELLED);
                bookingRepository.save(booking);
                return ResponseEntity.ok("Reservation cancelled successfully");
        } else {
            LOGGER.log(Level.ERROR, "The system could not find the specified booking.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Reservation not found");
        }
    }
}
