package lv.emendatus.Destiny_PropMan.service.implementation;


import lv.emendatus.Destiny_PropMan.domain.dto.reservation.ConfirmationDTO;
import lv.emendatus.Destiny_PropMan.domain.dto.reservation.ErrorDTO;
import lv.emendatus.Destiny_PropMan.domain.dto.reservation.ReservationCancellationDTO;
import lv.emendatus.Destiny_PropMan.domain.dto.reservation.ReservationRequestDTO;
import lv.emendatus.Destiny_PropMan.domain.entity.*;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
    @Autowired
    private JpaPropertyService propertyService;
    @Autowired
    private JpaCurrencyService currencyService;
    @Autowired
    private JpaEmailService emailService;
    @Autowired
    private JpaRefundService refundService;
    @Autowired
    private JpaPayoutService payoutService;
    @Override
    @PreAuthorize("hasRole('ROLE_TENANT') and tenantId == principal.id")
    @Transactional
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
            } else {
                if (!tenantService.getTenantById(tenantId).get().isActive()) {
                    everythingAllRight = false;
                    System.out.println("Tenant with ID " + tenantId + " is inactive!");
                    LOGGER.log(Level.WARN, "Failed to make reservation - tenant inactive.");
                    return new ConfirmationDTO("Reservation failed, tenant inactive!");
                }
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
                if (!tenantService.getTenantById(tenantId).get().getPreferredCurrency().equals(currencyService.returnBaseCurrency())) {
                    amount = amount * tenantService.getTenantById(tenantId).get().getPreferredCurrency().getRateToBase();
                }
                payment.setAmount(amount);
                payment.setAssociatedPropertyId(propertyId);
                payment.setManagerId(property.get().getManager().getId());
                payment.setAssociatedBookingId(bookingId);
                payment.setCurrency(tenantService.getTenantById(tenantId).get().getPreferredCurrency());
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
                Set<TenantPayment> payments = tenantService.getTenantById(tenantId).get().getTenantPayments();
                payments.add(payment);
                tenantService.getTenantById(tenantId).get().setTenantPayments(payments);
                tenantService.addTenant(tenantService.getTenantById(tenantId).get());
                Set<Booking> propertyBookings = property.get().getBookings();
                propertyBookings.add(newBooking);
                property.get().setBookings(propertyBookings);
                try {
                    emailService.sendEmail(tenantService.getTenantById(tenantId).get().getEmail(),
                            "Booking made at [Platform Name]",
                            createAcceptanceLetterToTenant(tenantService.getTenantById(tenantId).get().getFirstName(), tenantService.getTenantById(tenantId).get().getLastName(), newBooking));
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
                try {
                    emailService.sendEmail(newBooking.getProperty().getManager().getEmail(),
                            "Booking made at [Platform Name]",
                            createNotificationLetterToManager(newBooking.getProperty().getManager().getManagerName(), newBooking));
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
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
    @PreAuthorize("hasRole('ROLE_TENANT') and tenantId == principal.id")
    @Transactional
    public ResponseEntity<String> cancelReservation(ReservationCancellationDTO cancellationRequest) {
        Long tenantId = cancellationRequest.getTenantId();
        Long reservationId = cancellationRequest.getReservationId();
        Optional<Booking> bookingOptional = bookingRepository.findById(reservationId);
        if (bookingOptional.isPresent()) {
            if (bookingOptional.get().getStartDate().after(Timestamp.valueOf(LocalDateTime.now()))) {
            Booking booking = bookingOptional.get();
            TenantPayment payment = paymentService.getPaymentByBooking(booking.getId());
            if (paymentService.getPaymentByBooking(booking.getId()).isReceivedFromTenant()) {
                int lateCancellationPeriodSetOrDefault = 10;
                for (NumericalConfig config : configService.getSystemSettings()) {
                    if (config.getName().equals("LateCancellationPeriodInDays")) lateCancellationPeriodSetOrDefault = config.getValue().intValue();
                }
                int urgentCancellationPeriodSetOrDefault = 3;
                for (NumericalConfig config : configService.getSystemSettings()) {
                    if (config.getName().equals("UrgentCancellationPeriodInDays")) urgentCancellationPeriodSetOrDefault = config.getValue().intValue();
                }
                if (bookingService.calculateDaysDifference(booking.getStartDate()) < urgentCancellationPeriodSetOrDefault) {
                    int urgentCancellationPenaltyPercentage = 50;
                    for (NumericalConfig config : configService.getSystemSettings()) {
                        if (config.getName().equals("UrgentCancellationPenalty")) urgentCancellationPenaltyPercentage = config.getValue().intValue();
                    }
                    System.out.println("Urgent cancellation - " + urgentCancellationPenaltyPercentage + "% of the payment withheld");
                    Refund refund = new Refund();
                    double refundAmount = payment.getAmount() - (payment.getAmount() / 100 * urgentCancellationPenaltyPercentage);
                    if (refundAmount < 0) {
                        refundAmount = 0.0;
                    } else {
                    refund.setAmount(refundAmount);
                    refund.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
                    refund.setCurrency(payment.getCurrency());
                    refund.setBookingId(booking.getId());
                    refund.setTenantId(booking.getTenantId());
                    refundService.addRefund(refund);
                    }
                    Payout payout = new Payout();
                    payout.setBookingId(booking.getId());
                    int interestChargedByTheSystemSetOrDefault = 10;
                    for (NumericalConfig config : configService.getSystemSettings()) {
                        if (config.getName().equals("SystemInterestRate")) interestChargedByTheSystemSetOrDefault = config.getValue().intValue();
                    }
                    double payoutAmount = payment.getAmount() - refundAmount - (payment.getAmount() / 100 * interestChargedByTheSystemSetOrDefault);
                    if (payoutAmount < 0) {
                        payoutAmount = 0.0;
                    } else {
                        payout.setAmount(payoutAmount);
                        payout.setCurrency(payment.getCurrency());
                        payout.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
                        payout.setManagerId(booking.getProperty().getManager().getId());
                        payout.setBookingId(booking.getId());
                        payoutService.addPayout(payout);
                    }
                } else if (bookingService.calculateDaysDifference(booking.getStartDate()) < lateCancellationPeriodSetOrDefault) {
                    int lateCancellationPenaltyPercentage = 50;
                    for (NumericalConfig config : configService.getSystemSettings()) {
                        if (config.getName().equals("LateCancellationPenalty")) lateCancellationPenaltyPercentage = config.getValue().intValue();
                    }
                    System.out.println("Late cancellation - " + lateCancellationPenaltyPercentage + "% of the payment withheld");
                    Refund refund = new Refund();
                    double refundAmount = payment.getAmount() - (payment.getAmount() / 100 * lateCancellationPenaltyPercentage);
                    if (refundAmount < 0) {
                        refundAmount = 0.0;
                    } else {
                        refund.setAmount(refundAmount);
                        refund.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
                        refund.setCurrency(payment.getCurrency());
                        refund.setBookingId(booking.getId());
                        refund.setTenantId(booking.getTenantId());
                        refundService.addRefund(refund);
                    }
                    Payout payout = new Payout();
                    payout.setBookingId(booking.getId());
                    int interestChargedByTheSystemSetOrDefault = 10;
                    for (NumericalConfig config : configService.getSystemSettings()) {
                        if (config.getName().equals("SystemInterestRate")) interestChargedByTheSystemSetOrDefault = config.getValue().intValue();
                    }
                    double payoutAmount = payment.getAmount() - refundAmount - (payment.getAmount() / 100 * interestChargedByTheSystemSetOrDefault);
                    if (payoutAmount < 0) {
                        payoutAmount = 0.0;
                    } else {
                        payout.setAmount(payoutAmount);
                        payout.setCurrency(payment.getCurrency());
                        payout.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
                        payout.setManagerId(booking.getProperty().getManager().getId());
                        payout.setBookingId(booking.getId());
                        payoutService.addPayout(payout);
                    }
                } else {
                    int regularCancellationPenalty = 0;
                    for (NumericalConfig config : configService.getSystemSettings()) {
                        if (config.getName().equals("RegularCancellationPenalty")) regularCancellationPenalty = config.getValue().intValue();
                    }
                    System.out.println("Regular cancellation - " + regularCancellationPenalty + "% of the payment withheld");
                    Refund refund = new Refund();
                    refund.setAmount(payment.getAmount() - (payment.getAmount() / 100 * regularCancellationPenalty));
                    refund.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
                    refund.setCurrency(payment.getCurrency());
                    refund.setBookingId(booking.getId());
                    refund.setTenantId(booking.getTenantId());
                    refundService.addRefund(refund);
                }
            } else {
                paymentService.deleteTenantPayment(paymentService.getPaymentByBooking(booking.getId()).getId());
                LOGGER.log(Level.INFO, "Deleted TenantPayment with ID " + paymentService.getPaymentByBooking(booking.getId()).getId());
            }
                booking.setStatus(BookingStatus.CANCELLED);
                bookingRepository.save(booking);
                return ResponseEntity.ok("Reservation cancelled successfully");
            } else {
                return ResponseEntity.ok("Cannot cancel a current booking - please request early termination instead.");
            }
        } else {
            LOGGER.log(Level.ERROR, "The system could not find the specified booking.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Reservation not found");
        }
    }

    // AUXILIARY METHODS
    public String createAcceptanceLetterToTenant(String firstName, String lastName, Booking booking) {
        String greeting = "Dear " + firstName + " " + lastName + ",\n\n";
        String info = "You have booked a property for the period of " + booking.getStartDate().toString() + "through " + booking.getEndDate().toString() + ".\n\n";
        String info2 = "Your property manager has been informed of your application, and will respond shortly.";
        String communication = "Meanwhile, you can also contact your manager directly at " + booking.getProperty().getManager().getEmail() + ".\n\n";
        String closing = "Thank you for choosing our service.\n\nBest regards,\n[Your Company Name]";

        return greeting + info + info2 + communication + closing;
    }

    public String createNotificationLetterToManager(String managerName, Booking booking) {
        String greeting = "Dear " + managerName + ",\n\n";
        String info = "[Platform name] is happy to inform you that a tenant has booked one of your properties for the period of " + booking.getStartDate().toString() + "through " + booking.getEndDate().toString() + ".\n\n";
        String instructions = "Please make sure to log in to your platform account as promptly as possible to confirm or reject this booking.";
        String closing = "We are happy to have you as our partner, and looking forward to more mutually lucrative business with you!";

        return greeting + info + instructions + closing;
    }
}
