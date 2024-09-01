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
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;

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
    private final JpaPayoutService payoutService;
    private final JpaPropertyService propertyService;
    private final JpaPropertyDiscountService discountService;
    private final JpaEmailService emailService;
    private final JpaLeasingHistoryService leasingHistoryService;
    private final JpaPropertyLockService lockService;
    public ScheduledTasks(JpaBookingService bookingService, JpaTenantService tenantService, JpaManagerService managerService, JpaTokenService tokenService, JpaTokenResetService resetterService, JpaEarlyTerminationRequestService terminationRequestService, JpaTenantPaymentService paymentService, JpaNumericalConfigService configService, JpaPayoutService payoutService, JpaPropertyService propertyService, JpaPropertyDiscountService discountService, JpaEmailService emailService, JpaLeasingHistoryService leasingHistoryService, JpaPropertyLockService lockService) {
        this.bookingService = bookingService;
        this.tenantService = tenantService;
        this.managerService = managerService;
        this.tokenService = tokenService;
        this.resetterService = resetterService;
        this.terminationRequestService = terminationRequestService;
        this.paymentService = paymentService;
        this.configService = configService;
        this.payoutService = payoutService;
        this.propertyService = propertyService;
        this.discountService = discountService;
        this.emailService = emailService;
        this.leasingHistoryService = leasingHistoryService;
        this.lockService = lockService;
    }

    @Scheduled(cron = "0 0 1 * * *")
    @Transactional
    public void changeBookingStatus() {
        LocalDateTime today = LocalDateTime.now();
        List<Booking> currentBookings = bookingService.getAllBookings();
        for (Booking booking : currentBookings) {
            LocalDateTime startDate = booking.getStartDate().toLocalDateTime();
            LocalDateTime endDate = booking.getEndDate().toLocalDateTime();
            if ((today.isEqual(startDate) || today.isAfter(startDate)) && booking.getStatus().equals(BookingStatus.CONFIRMED)) {
                bookingService.updateBookingStatus(booking.getId(), BookingStatus.CURRENT);
                if (tenantService.getTenantById(booking.getTenantId()).isPresent()) {
                    Tenant tenant = tenantService.getTenantById(booking.getTenantId()).get();
                    tenant.setCurrentProperty(booking.getProperty());
                    tenantService.addTenant(tenant);
                }
                LOGGER.info("Booking {} status set to CURRENT along with the start of the rental period.", booking.getId());
            }
            if (today.isAfter(endDate)) {
                bookingService.updateBookingStatus(booking.getId(), BookingStatus.OVER);
                if (tenantService.getTenantById(booking.getTenantId()).isPresent()) {
                    Tenant tenant = tenantService.getTenantById(booking.getTenantId()).get();
                    tenant.setCurrentProperty(null);
                    tenantService.addTenant(tenant);
                    try {
                        emailService.sendEmail(tenant.getEmail(),
                                "Booking completed",
                                createGoodbyeLetterToTenant(tenant.getFirstName(), tenant.getLastName()));
                    } catch (MessagingException e) {
                        e.printStackTrace();
                    }
                }
                LOGGER.info("Booking {} status set to OVER along with the expiration of the rental period.", booking.getId());
            }
        }
    }

    @Scheduled(cron = "0 0 2 * * *")
    @Transactional
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
    @Transactional
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
                try {
                    if (tenantService.getTenantById(booking.getTenantId()).isPresent()) {
                        emailService.sendEmail(tenantService.getTenantById(booking.getTenantId()).get().getEmail(),
                                "Booking at [Platform Name] cancelled",
                                createCancellationLetterToTenant(tenantService.getTenantById(booking.getTenantId()).get().getFirstName(), tenantService.getTenantById(booking.getTenantId()).get().getLastName(), booking));
                    }
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
                try {
                        emailService.sendEmail(booking.getProperty().getManager().getEmail(),
                                "Booking at [Platform Name] cancelled",
                                createCancellationLetterToManager(booking.getProperty().getManager().getManagerName(), booking));
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @Scheduled(cron = "5 0 0 * * *")
    @Transactional
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

    @Scheduled(cron = "0 0 4 * * *")
    @Transactional
    public void closeBookingsThatAreOver() {
        List<Booking> currentBookings = bookingService.getAllBookings().stream()
                .filter(booking -> booking.getStatus().equals(BookingStatus.OVER)).toList();
        for (Booking booking : currentBookings) {
            if (booking.getEndDate().toLocalDateTime().isAfter(LocalDateTime.now().plusDays(21))) {
                double managerPayment = paymentService.getPaymentByBooking(booking.getId()).getManagerPayment();
                Payout payout = new Payout();
                payout.setBookingId(booking.getId());
                payout.setManagerId(booking.getProperty().getManager().getId());
                payout.setAmount(managerPayment);
                payout.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
                payoutService.addPayout(payout);
                LeasingHistory history = new LeasingHistory();
                Optional<Tenant> optionalTenant = tenantService.getTenantById(booking.getTenantId());
                optionalTenant.ifPresent(history::setTenant);
                history.setPropertyId(booking.getProperty().getId());
                history.setStartDate(booking.getStartDate());
                history.setEndDate(booking.getEndDate());
                leasingHistoryService.addLeasingHistory(history);
                if (optionalTenant.isPresent()) {
                    List<LeasingHistory> histories = optionalTenant.get().getLeasingHistories();
                    histories.add(history);
                    optionalTenant.get().setLeasingHistories(histories);
                    tenantService.addTenant(optionalTenant.get());
                }
                propertyService.removeBookingFromProperty(booking.getProperty().getId(), booking.getId());
                bookingService.deleteBooking(booking.getId());
                }
            if (booking.getStatus().equals(BookingStatus.CANCELLED)) bookingService.deleteBooking(booking.getId());
            }
        }

    @Scheduled(cron = "0 0 5 * * *")
    @Transactional
    public void deleteOutdatedDiscounts() {
        List<PropertyDiscount> expiredPeriodDiscounts = discountService.getAllPropertyDiscounts().stream()
                .filter(discount -> discount.getPeriodEnd().isBefore(LocalDate.now()))
                .filter(discount -> {
                    Property property = propertyService.getPropertyById(discount.getProperty().getId()).orElse(null);
                    if (property == null) {
                        return false; // Property no longer exists
                    }
                    List<Booking> relevantBookings = bookingService.getBookingsByDateRangeWithOverlaps(discount.getPeriodStart(), discount.getPeriodEnd());
                    return relevantBookings.stream().anyMatch(booking -> !(booking.getStatus().equals(BookingStatus.OVER) || booking.getStatus().equals(BookingStatus.CANCELLED)));
                })
                .toList();
        expiredPeriodDiscounts.forEach(discount -> {
            LOGGER.info("Outdated discount or surcharge with ID {} has been automatically removed", discount.getId());
            discountService.deletePropertyDiscount(discount.getId());
        });
    }

    @Scheduled(cron = "0 0 9 * * *")
    public void remindOfPayments() {
        List<TenantPayment> unsettledPayments = paymentService.getUnsettledPayments();
        List<TenantPayment> paymentsDueTomorrow = new ArrayList<>();
        for (TenantPayment payment : unsettledPayments) {
            if (payment.getReceiptDue().toLocalDateTime().equals(LocalDateTime.now().plusDays(1))
            || LocalDateTime.now().isAfter(payment.getReceiptDue().toLocalDateTime().minusDays(1)))
                paymentsDueTomorrow.add(payment);
        }
        for (TenantPayment payment : paymentsDueTomorrow) {
            try {
                emailService.sendEmail(payment.getTenant().getEmail(),
                        "Payment due at [Platform Name]!",
                        createPaymentReminderLetterToTenant(payment.getTenant().getFirstName(), payment.getTenant().getLastName(), payment));
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }
    }

    @Scheduled(cron = "0 5 0 1 * *")
    public void remindOfCardExpiry() {
        List<Tenant> allTenants = tenantService.getAllTenants();
        List<Manager> allManagers = managerService.getAllManagers();
        for (Tenant tenant : allTenants) {
            if (tenant.getCardValidityDate().isBefore(YearMonth.now()))
                try {
                    emailService.sendEmail(tenant.getEmail(),
                            "Your payment card has expired - [Platform Name]!",
                            createCardExpiryLetterToUser(tenant.getFirstName(), tenant.getLastName(), tenant.getPaymentCardNo()));
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
        }
        for (Manager manager : allManagers) {
            if (manager.getCardValidityDate().isBefore(YearMonth.now()))
                try {
                    emailService.sendEmail(manager.getEmail(),
                            "Your payment card has expired - [Platform Name]!",
                            createCardExpiryLetterToUser(manager.getManagerName(), "", manager.getPaymentCardNo()));
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
        }
    }

    @Scheduled(cron = "0 0 11 * * *")
    public void remindOfArrivals() {
        List<Booking> tomorrowsBookings = bookingService.getAllBookings().stream()
                .filter(booking -> booking.getStartDate().toLocalDateTime().toLocalDate().equals(LocalDate.now().plusDays(1))).toList();
        for (Booking booking : tomorrowsBookings) {
            if (tenantService.getTenantById(booking.getTenantId()).isPresent()) {
                try {
                    emailService.sendEmail(booking.getProperty().getManager().getEmail(),
                            "Yor guest is arriving tomorrow!!",
                            createArrivalReminderLetterToManager(
                                    booking.getProperty().getManager().getManagerName(),
                                    tenantService.getTenantById(booking.getTenantId()).get().getFirstName(),
                                    tenantService.getTenantById(booking.getTenantId()).get().getLastName(),
                                    booking.getProperty()));
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Scheduled(cron = "0 30 3 * * 2")
    @Transactional
    public void deleteOutdatedLocksAndStubs() {
        List<PropertyLock> allLocks = lockService.getAllPropertyLocks();
        List<Long> iDsForRemoval = allLocks.stream()
                .map(PropertyLock::getBookingStubId)
                .map(bookingService::getBookingById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(stub -> stub.getEndDate().before(Timestamp.valueOf(LocalDateTime.now())))
                .map(Booking::getId)
                .toList();
        allLocks.stream()
                .filter(lock -> iDsForRemoval.contains(lock.getBookingStubId()))
                .forEach(lock -> lockService.deletePropertyLock(lock.getId()));
        iDsForRemoval.forEach(bookingService::deleteBooking);
    }

    // AUXILIARY METHODS
    public String createCancellationLetterToTenant(String firstName, String lastName, Booking booking) {
        String greeting = "Dear " + firstName + " " + lastName + ",\n\n";
        String info = "We are sorry to inform you that your booking with ID " + booking.getId() + " has been cancelled due to not having been paid by the specified deadline.\n\n";
        String closing = "Still, in comprehension that there might have been circumstances that prevented you from making the payment, we will be glad to see you at our site again! \n\nBest regards,\n[Your Company Name]";
        return greeting + info + closing;
    }

    public String createCancellationLetterToManager(String managerName, Booking booking) {
        String greeting = "Dear " + managerName + ",\n\n";
        String info = "Unfortunately, we had to cancel the booking with ID " + booking.getId() + ", as the tenant failed to pay the rental fee by the specified deadline.\n\n";
        String closing = "Such situations do happen, but are always here to support your efforts! \n\nBest regards,\n[Your Company Name]";
        return greeting + info + closing;
    }

    public String createPaymentReminderLetterToTenant(String firstName, String lastName, TenantPayment payment) {
        String greeting = "Dear " + firstName + " " + lastName + ",\n\n";
        String info = "Please be reminded that you have an outstanding payment of " + payment.getCurrency().getDesignation() + " " + payment.getAmount() + " at [Platform Name], which is due tomorrow. \n\n";
        String instructions = "We kindly ask you to remit this payment by the specified deadline. Otherwise, we will, unfortunately, have to cancel your respective booking. \n\n";
        String closing = "If you have already made the payment, please accept pour apologies and disregards this letter. Looking forward to welcoming you soon!\n\nBest regards,\n[Your Company Name]";
        return greeting + info + instructions + closing;
    }

    public String createArrivalReminderLetterToManager(String managerName, String tenantName, String tenantSurname, Property property) {
        String greeting = "Dear " + managerName + ",\n\n";
        String info = "Your guest " + tenantName + " " + tenantSurname + ", is arriving tomorrow to " + property.getAddress() + ", " + property.getSettlement() + ", " + property.getCountry() + ".\n\n";
        String instructions = "We trust that you will do your best to make the guest feel at home! Please do not forget to arrive or arrange your representative's presence on site at the agreed time. \n\n";
        String closing = "We are proud of having you as a partner!\n\nBest regards,\n[Your Company Name]";
        return greeting + info + instructions + closing;
    }

    public String createGoodbyeLetterToTenant(String firstName, String lastName) {
        String greeting = "Dear " + firstName + " " + lastName + ",\n\n";
        String info = "We hope that your stay was most pleasant, and would really love to offer our services again whenever you may need them!.\n\n";
        String closing = "If you wish, you can leave a review at our website. At any rate, we are eager to remain your loyal partner in many future journeys to come! \n\nBest regards,\n[Your Company Name]";
        return greeting + info + closing;
    }

    public String createCardExpiryLetterToUser(String firstName, String lastName, String cardNumber) {
        String greeting = "Dear " + firstName + " " + lastName + ",\n\n";
        String info = "Please be advised that your payment card No. " + cardNumber + " has expired. In order to maintain access to our platform's services and be able to receive any due refunds and payouts, please log in to your account and enter your new card details.\n\n";
        String closing = "It is GREAT to have you with us! \n\nBest regards,\n[Your Company Name]";
        return greeting + info + closing;
    }
}
