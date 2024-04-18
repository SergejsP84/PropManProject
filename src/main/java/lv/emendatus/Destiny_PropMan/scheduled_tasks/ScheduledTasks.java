package lv.emendatus.Destiny_PropMan.scheduled_tasks;

import lv.emendatus.Destiny_PropMan.domain.entity.*;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.BookingStatus;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.ETRequestStatus;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.UserType;
import lv.emendatus.Destiny_PropMan.service.implementation.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class ScheduledTasks {
    private final Logger LOGGER = LogManager.getLogger(JpaPropertyService.class);
    private final JpaBookingService bookingService;
    private final JpaTenantService tenantService;
    private final JpaManagerService managerService;
    private final JpaTokenService tokenService;
    private final JpaTokenResetService resetterService;
    private final JpaEarlyTerminationRequestService terminationRequestService;
    private final JpaTenantPaymentService paymentService;
    private final JpaNumericalConfigService configService;
    public ScheduledTasks(JpaBookingService bookingService, JpaTenantService tenantService, JpaManagerService managerService, JpaTokenService tokenService, JpaTokenResetService resetterService, JpaEarlyTerminationRequestService terminationRequestService, JpaTenantPaymentService paymentService, JpaNumericalConfigService configService) {
        this.bookingService = bookingService;
        this.tenantService = tenantService;
        this.managerService = managerService;
        this.tokenService = tokenService;
        this.resetterService = resetterService;
        this.terminationRequestService = terminationRequestService;
        this.paymentService = paymentService;
        this.configService = configService;
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
                LOGGER.info("Booking {} status set to CURRENT along with the start of the rental period.", booking.getId());
            }
            if (today.isAfter(endDate)) {
                bookingService.updateBookingStatus(booking.getId(), BookingStatus.OVER);
                LOGGER.info("Booking {} status set to OVER along with the expiration of the rental period.", booking.getId());
            }
        }
    }

    @Scheduled(cron = "0 0 2 * * *")
    public void removeProcessedTerminationRequests() {
        List<EarlyTerminationRequest> existingRequests = terminationRequestService.getAllETRequests();
        int delaySetOrDefault = 7;
        for (NumericalConfig config : configService.getSystemSettings()) {
            if (config.getName().equals("ClaimPeriodDays")) delaySetOrDefault = config.getValue().intValue();
        }
        for (EarlyTerminationRequest request : existingRequests) {
            if ((request.getStatus().equals(ETRequestStatus.APPROVED) || request.getStatus().equals(ETRequestStatus.DECLINED))
                    && (request.getProcessedOn().isBefore(LocalDate.now().minusDays(delaySetOrDefault)))) {
                terminationRequestService.deleteETRequest(request.getId());
                LOGGER.info("Request {} has been deleted as having been processed and beyond the claim period.", request.getId());
            }
        }
    }

    @Scheduled(cron = "0 0 3 * * *")
    public void cancelUnpaidBookings() {
        List<Booking> currentBookings = bookingService.getAllBookings().stream()
                .filter(booking -> booking.getStatus().equals(BookingStatus.PENDING_PAYMENT)).toList();
        for (Booking booking : currentBookings) {
            TenantPayment payment = paymentService.getPaymentByBooking(booking.getId());
            if (payment != null && !booking.isPaid()
                    && payment.getReceiptDue() != null
                    && payment.getReceiptDue().toLocalDateTime().isBefore(LocalDateTime.now().plusDays(1))) {
                booking.setStatus(BookingStatus.CANCELLED);
                bookingService.updateBookingStatus(booking.getId(), BookingStatus.CANCELLED);
                LOGGER.info("Booking {} has been cancelled because no payment was received within the due timelines.", booking.getId());
            }
        }
    }

    @Scheduled(cron = "5 0 0 * * *")
    public synchronized void clearExpiredTokens() {
        List<TokenResetter> resetters = resetterService.getAllResetters();
        for (TokenResetter resetter : resetters) {
            LocalDateTime expirationTime = resetter.getCreatedAt().toLocalDateTime().plusMinutes(15);
            if (LocalDateTime.now().isAfter(expirationTime)) {
                if (resetter.getUserType().equals(UserType.TENANT)) {
                    Optional<Tenant> optionalTenant = tenantService.getTenantById(resetter.getUserId());
                    if (optionalTenant.isPresent()) {
                        Tenant tenant = optionalTenant.get();
                        tenant.setConfirmationToken("");
                        tenantService.addTenant(tenant);
                        resetterService.deleteResetter(resetter.getId());
                    } else {
                        LOGGER.warn("Token resetter {} not engaged due to an invalid field value; token and resetter must be cleared manually", resetter.getId());
                    }
                } else if (resetter.getUserType().equals(UserType.MANAGER)) {
                    Optional<Manager> optionalManager = managerService.getManagerById(resetter.getUserId());
                    if (optionalManager.isPresent()) {
                        Manager manager = optionalManager.get();
                        manager.setConfirmationToken("");
                        managerService.addManager(manager);
                        resetterService.deleteResetter(resetter.getId());
                    } else {
                        LOGGER.warn("Token resetter {} not engaged due to an invalid field value; token and resetter must be cleared manually", resetter.getId());
                    }
                }
            }
        }
    }

}
