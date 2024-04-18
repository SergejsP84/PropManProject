package lv.emendatus.Destiny_PropMan.service.implementation;

import lv.emendatus.Destiny_PropMan.domain.dto.Admin.AdminPayoutDTO;
import lv.emendatus.Destiny_PropMan.domain.dto.Admin.AdminRefundDTO;
import lv.emendatus.Destiny_PropMan.domain.dto.managerial.ManagerProfileDTO;
import lv.emendatus.Destiny_PropMan.domain.dto.managerial.PropertyAdditionDTO;
import lv.emendatus.Destiny_PropMan.domain.dto.profile.TenantDTO_Profile;
import lv.emendatus.Destiny_PropMan.domain.dto.registration.ManagerRegistrationDTO;
import lv.emendatus.Destiny_PropMan.domain.dto.registration.TenantRegistrationDTO;
import lv.emendatus.Destiny_PropMan.domain.entity.*;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.BookingStatus;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.ClaimStatus;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.PropertyStatus;
import lv.emendatus.Destiny_PropMan.exceptions.*;
import lv.emendatus.Destiny_PropMan.mapper.ManagerMapper;
import lv.emendatus.Destiny_PropMan.mapper.PropertyCreationMapper;
import lv.emendatus.Destiny_PropMan.mapper.TenantMapper;
import lv.emendatus.Destiny_PropMan.service.interfaces.AdminFunctionalityService;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
public class JpaAdminFunctionalityService implements AdminFunctionalityService {

    public final JpaTenantService tenantService;
    public final JpaManagerService managerService;
    public final JpaBookingService bookingService;
    public final JpaRefundService refundService;
    public final JpaTenantPaymentService paymentService;
    public final JpaLeasingHistoryService leasingHistoryService;
    public final JpaClaimService claimService;
    public final JpaPropertyService propertyService;
    public final JpaNumericalConfigService configService;
    public final JpaThirdPartyPaymentProviderService paymentProviderService;
    public final JpaPayoutService payoutService;

    private final BCryptPasswordEncoder passwordEncoder;
    private final TenantMapper tenantMapper;
    private final ManagerMapper managerMapper;
    private final Logger LOGGER = LogManager.getLogger(JpaPropertyService.class);
    private static final String COMPLETED_REFUNDS_FILE_PATH = "completedRefunds.txt";
    private static final String COMPLETED_PAYOUTS_FILE_PATH = "completedPayouts.txt";



    public JpaAdminFunctionalityService(JpaTenantService tenantService, JpaManagerService managerService, JpaBookingService bookingService, JpaRefundService refundService, JpaTenantPaymentService paymentService, JpaLeasingHistoryService leasingHistoryService, JpaClaimService claimService, JpaPropertyService propertyService, JpaNumericalConfigService configService, JpaThirdPartyPaymentProviderService paymentProviderService, JpaPayoutService payoutService, BCryptPasswordEncoder passwordEncoder, TenantMapper tenantMapper, ManagerMapper managerMapper) {
        this.tenantService = tenantService;
        this.managerService = managerService;
        this.bookingService = bookingService;
        this.refundService = refundService;
        this.paymentService = paymentService;
        this.leasingHistoryService = leasingHistoryService;
        this.claimService = claimService;
        this.propertyService = propertyService;
        this.configService = configService;
        this.paymentProviderService = paymentProviderService;
        this.payoutService = payoutService;
        this.passwordEncoder = passwordEncoder;
        this.tenantMapper = tenantMapper;
        this.managerMapper = managerMapper;
    }


    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void toggleTenantStatus(Long tenantId) {
        Optional<Tenant> tenant = tenantService.getTenantById(tenantId);
        if (tenant.isPresent()) {
            tenant.get().setActive(!tenant.get().isActive());
            tenantService.addTenant(tenant.get());
        } else {
            LOGGER.warn("No tenant with the ID {} found in the database", tenantId);
        }
    }

    @Override
//    @Transactional
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void toggleManagerStatus(Long managerId) {
        System.out.println("toggleManagerStatus method initiated");
        Optional<Manager> manager = managerService.getManagerById(managerId);
        if (manager.isPresent()) {
            System.out.println("Manager " + managerId + "has been found");
            System.out.println("The manager's Active status is " + manager.get().isActive());
            manager.get().setActive(!manager.get().isActive());
            System.out.println("The manager's Active status has been set to " + manager.get().isActive());
            managerService.addManager(manager.get());
            System.out.println("The updated manager has been saved to the database");
            Optional<Manager> manager2 = managerService.getManagerById(managerId);
            if (manager2.isPresent()) {
                System.out.println("Manager re-obtained from the database");
                System.out.println("The retrieved manager's Active status is " + manager2.get().isActive());
            } else {
                System.out.println("Something else is wrong");
            }
        } else {
            LOGGER.warn("No manager with the ID {} found in the database", managerId);
        }
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void registerTenantForAdmin(TenantRegistrationDTO registrationDTO) {
        if (!isValidPaymentCardNumber(registrationDTO.getPaymentCardNo())) {
            LOGGER.error("Invalid payment card number for tenant registration");
            throw new InvalidPaymentCardNumberException("Invalid payment card number");
        }
        if (!registrationDTO.getPassword().equals(registrationDTO.getReEnterPassword())) {
            LOGGER.error("Entered passwords do not match");
            throw new PasswordMismatchException("Passwords do not match");
        }
        if (isLoginBusy(registrationDTO.getLogin())) {
            LOGGER.error("This login already exists");
            throw new LoginAlreadyExistsException("Login already exists");
        }
        if (isEmailBusy(registrationDTO.getEmail())) {
            LOGGER.error("A tenant with this e-mail has already been registered");
            throw new EmailAlreadyExistsException("Tenant with this e-mail exists");
        }
        Tenant tenant = new Tenant();
        tenant.setFirstName(registrationDTO.getFirstName());
        tenant.setLastName(registrationDTO.getLastName());
        tenant.setPhone(registrationDTO.getPhone());
        tenant.setEmail(registrationDTO.getEmail());
        tenant.setIban(registrationDTO.getIban());
        tenant.setLogin(registrationDTO.getLogin());
        String encodedPassword = passwordEncoder.encode(registrationDTO.getPassword());
        tenant.setPassword(encodedPassword);
        tenant.setRating(0F);
        tenant.setActive(true);
        tenant.setTenantPayments(new HashSet<>());
        tenant.setCurrentProperty(null);
        tenant.setLeasingHistories(new ArrayList<>());
        tenant.setPaymentCardNo(registrationDTO.getPaymentCardNo());
        tenant.setConfirmationToken("");
        List<String> knownIPs = new ArrayList<>();
        tenant.setKnownIps(knownIPs);
        tenantService.addTenant(tenant);
        LOGGER.info("New tenant added by an Admin: ID" + tenant.getId() + ", First name / surname: " + tenant.getFirstName() + " " + tenant.getLastName());
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void registerManager(ManagerRegistrationDTO registrationDTO) {
        if (!isValidPaymentCardNumber(registrationDTO.getPaymentCardNo())) {
            LOGGER.error("Invalid payment card number for manager registration");
            throw new InvalidPaymentCardNumberException("Invalid payment card number");
        }
        if (!registrationDTO.getPassword().equals(registrationDTO.getReEnterPassword())) {
            LOGGER.error("Entered passwords do not match");
            throw new PasswordMismatchException("Passwords do not match");
        }
        if (isLoginBusy(registrationDTO.getLogin())) {
            LOGGER.error("This login already exists");
            throw new LoginAlreadyExistsException("Login already exists");
        }
        if (isEmailBusy(registrationDTO.getEmail())) {
            LOGGER.error("A manager with this e-mail has already been registered");
            throw new EmailAlreadyExistsException("Manager with this e-mail exists");
        }
        Manager manager = new Manager();
        manager.setManagerName(registrationDTO.getManagerName());
        manager.setType(registrationDTO.getType());
        manager.setPhone(registrationDTO.getPhone());
        manager.setEmail(registrationDTO.getEmail());
        manager.setIban(registrationDTO.getIban());
        manager.setLogin(registrationDTO.getLogin());
        manager.setDescription(registrationDTO.getDescription());
        manager.setPaymentCardNo(registrationDTO.getPaymentCardNo());
        String encodedPassword = passwordEncoder.encode(registrationDTO.getPassword());
        manager.setPassword(encodedPassword);
        manager.setActive(true);
        manager.setProperties(new HashSet<>());
        manager.setJoinDate(Timestamp.valueOf(LocalDateTime.now()));
        manager.setConfirmationToken("");
        List<String> knownIPs = new ArrayList<>();
        manager.setKnownIps(knownIPs);
        managerService.addManager(manager);
        LOGGER.info("New manager added by an Admin: ID" + manager.getId() + ", Name: " + manager.getManagerName() + ", Description: " + manager.getDescription());
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void updateTenantInformation(Long tenantId, TenantDTO_Profile updatedTenantInfo) {
        Optional<Tenant> tenantOptional = tenantService.getTenantById(tenantId);
        if (tenantOptional.isPresent()) {
            Tenant existingTenant = tenantOptional.get();
            LOGGER.log(Level.INFO, "An Admin has updated the profile for Tenant {}. The following changes were applied:", tenantId);
            System.out.println("An Admin has updated the profile for Tenant " + tenantId + ". The following changes were applied:");
            if (!updatedTenantInfo.getFirstName().equals(existingTenant.getFirstName())) {
                LOGGER.log(Level.INFO, "First name changed to: "  + updatedTenantInfo.getFirstName());
                System.out.println("First name changed to: "  + updatedTenantInfo.getFirstName());
            }
            if (!updatedTenantInfo.getLastName().equals(existingTenant.getLastName())) {
                LOGGER.log(Level.INFO, "Surname changed to: "  + updatedTenantInfo.getLastName());
                System.out.println("Surname changed to: "  + updatedTenantInfo.getLastName());
            }
            if (!updatedTenantInfo.getPhone().equals(existingTenant.getPhone())) {
                LOGGER.log(Level.INFO, "Phone number changed to: "  + updatedTenantInfo.getPhone());
                System.out.println("Phone number changed to: "  + updatedTenantInfo.getPhone());
            }
            if (!updatedTenantInfo.getEmail().equals(existingTenant.getEmail())) {
                LOGGER.log(Level.INFO, "Email address changed to: "  + updatedTenantInfo.getEmail());
                System.out.println("Email address changed to: "  + updatedTenantInfo.getEmail());
            }
            if (!updatedTenantInfo.getIban().equals(existingTenant.getIban())) {
                LOGGER.log(Level.INFO, "IBAN changed to: "  + updatedTenantInfo.getIban());
                System.out.println("IBAN address changed to: "  + updatedTenantInfo.getIban());
            }
            if (!updatedTenantInfo.getPaymentCardNo().equals(existingTenant.getPaymentCardNo())) {
                LOGGER.log(Level.INFO, "Payment card number changed to: "  + updatedTenantInfo.getPaymentCardNo());
                System.out.println("Payment card number address changed to: "  + updatedTenantInfo.getPaymentCardNo());
            }
            if (updatedTenantInfo.getRating() != existingTenant.getRating()) {
                LOGGER.log(Level.INFO, "Tenant rating set to: "  + updatedTenantInfo.getRating());
                System.out.println("Tenant rating set to: "  + updatedTenantInfo.getRating());
            }

            tenantMapper.updateTenantFromDTO(existingTenant, updatedTenantInfo);
            tenantService.addTenant(existingTenant);
        } else {
            LOGGER.error("No tenant with the ID {} exists in the database.", tenantId);
            throw new TenantNotFoundException("No tenant found with ID: " + tenantId);
        }
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void updateManagerInformation(Long managerId, ManagerProfileDTO updatedProfile) {
        Optional<Manager> managerOptional = managerService.getManagerById(managerId);
        if (managerOptional.isPresent()) {
            Manager existingManager = managerOptional.get();
            LOGGER.log(Level.INFO, "An Admin has updated the profile for Manager {}. The following changes were applied:", managerId);
            System.out.println("An Admin has updated the profile for Manager " + managerId + ". The following changes were applied:");
            if (!updatedProfile.getManagerName().equals(existingManager.getManagerName())) {
                LOGGER.log(Level.INFO, "Manager name changed to: "  + updatedProfile.getManagerName());
                System.out.println("Manager name changed to: "  + updatedProfile.getManagerName());
            }
            if (!updatedProfile.getDescription().equals(existingManager.getDescription())) {
                LOGGER.log(Level.INFO, "Description changed to: "  + updatedProfile.getDescription());
                System.out.println("Description changed to: "  + updatedProfile.getDescription());
            }
            if (!updatedProfile.getPhone().equals(existingManager.getPhone())) {
                LOGGER.log(Level.INFO, "Phone number changed to: "  + updatedProfile.getPhone());
                System.out.println("Phone number changed to: "  + updatedProfile.getPhone());
            }
            if (!updatedProfile.getIban().equals(existingManager.getIban())) {
                LOGGER.log(Level.INFO, "IBAN changed to: "  + updatedProfile.getIban());
                System.out.println("IBAN changed to: "  + updatedProfile.getIban());
            }
            if (!updatedProfile.getPaymentCardNo().equals(existingManager.getPaymentCardNo())) {
                LOGGER.log(Level.INFO, "Payment card No. changed to: "  + updatedProfile.getPaymentCardNo());
                System.out.println("Payment card No. changed to: "  + updatedProfile.getPaymentCardNo());
            }
            if (!updatedProfile.getEmail().equals(existingManager.getEmail())) {
                LOGGER.log(Level.INFO, "Email address changed to: "  + updatedProfile.getEmail());
                System.out.println("Email address changed to: "  + updatedProfile.getEmail());
            }
            managerMapper.updateManagerFromDTO(existingManager, updatedProfile);
            managerService.addManager(existingManager);
        } else {
            LOGGER.log(Level.ERROR, "No manager with the {} ID exists in the database.", managerId);
            throw new ManagerNotFoundException("No manager found with ID: " + managerId);
        }
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void removeTenant(Long tenantId) {
        boolean obstaclesPresent = false;
        if (tenantService.getTenantById(tenantId).isPresent()) {
            Tenant tenant = tenantService.getTenantById(tenantId).get();

            // Processing potentially associated Claims
            List<Claim> claimsByTenant = claimService.getClaimsFiledByTenant(tenantId);
            List<Claim> claimsAgainstTenant = claimService.getClaimsAgainstTenant(tenantId);
            claimsByTenant.removeIf(claim -> claim.getClaimStatus().equals(ClaimStatus.RESOLVED));
            claimsAgainstTenant.removeIf(claim -> claim.getClaimStatus().equals(ClaimStatus.RESOLVED));
            if (!claimsByTenant.isEmpty() || !claimsAgainstTenant.isEmpty()) {
                StringBuilder sb = new StringBuilder("Tenant removal failed: there are Claims pending from or against the Tenant: \\n");
                sb.append("Claims filed by Tenant: ");
                for (Claim claim : claimsByTenant) {
                    sb.append(claim.getId());
                    if (claimsByTenant.iterator().hasNext()) sb.append(", ");
                    else sb.append("\\n");
                }
                sb.append("Claims filed against Tenant: ");
                for (Claim claim : claimsAgainstTenant) {
                    sb.append(claim.getId());
                    if (claimsAgainstTenant.iterator().hasNext()) sb.append(", ");
                    else sb.append("\\n");
                }
                sb.append("These Claims must be resolved before the Tenant can be removed.");
                LOGGER.log(Level.WARN, sb.toString());
                System.out.println(sb.toString());
                obstaclesPresent = true;
                return;
            }

            // Processing potentially associated Bookings
            Set<Booking> bookingsByTenant = bookingService.getBookingsByTenant(tenant);
            if (!bookingsByTenant.isEmpty()) {
                for (Booking booking : bookingsByTenant) {
                    if (booking.getStatus().equals(BookingStatus.PENDING_APPROVAL)) {
                        booking.setStatus(BookingStatus.CANCELLED);
                    }
                    if (booking.getStatus().equals(BookingStatus.PENDING_PAYMENT)) {
                        List<TenantPayment> payments = paymentService.getPaymentsByTenant(tenantId);
                        payments.removeIf(payment -> !payment.isReceivedFromTenant());
                        if (payments.isEmpty()) booking.setStatus(BookingStatus.CANCELLED);
                    }
                    if (booking.getStatus().equals(BookingStatus.CONFIRMED)) {
                        Refund refund = new Refund();
                        refund.setTenantId(tenantId);
                        refund.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
                        refund.setBookingId(booking.getId());
                        refund.setAmount(paymentService.getPaymentByBooking(booking.getId()).getAmount());
                        refundService.addRefund(refund);
                        booking.setStatus(BookingStatus.CANCELLED);
                    }
                    if (booking.getStatus().equals(BookingStatus.CURRENT)) {
                        LOGGER.log(Level.WARN, "Cannot remove a Tenant currently residing in a Property.");
                        System.out.println("Cannot remove a Tenant currently residing in a Property.");
                        obstaclesPresent = true;
                        return;
                    }
                    if (booking.getStatus().equals(BookingStatus.OVER)) {
                        LeasingHistory history = new LeasingHistory();
                        Optional<Tenant> optionalTenant = tenantService.getTenantById(booking.getTenantId());
                        optionalTenant.ifPresent(history::setTenant);
                        history.setPropertyId(booking.getProperty().getId());
                        history.setStartDate(booking.getStartDate());
                        history.setEndDate(booking.getEndDate());
                        leasingHistoryService.addLeasingHistory(history);
                        bookingService.deleteBooking(booking.getId());
                    }
                }
                if (!bookingsByTenant.isEmpty()) {
                    Iterator<Booking> iterator = bookingsByTenant.iterator();
                    while (iterator.hasNext()) {
                        Booking booking = iterator.next();
                        if (booking.getStatus().equals(BookingStatus.OVER) || booking.getStatus().equals(BookingStatus.CANCELLED)) {
                            iterator.remove();
                        }
                    }
                }
                if (!bookingsByTenant.isEmpty()) {
                    StringBuilder sb = new StringBuilder("Tenant removal failed: could not remove all associated bookings. Bookings with the following IDs: \\n");
                    for (Booking booking : bookingsByTenant) {
                        sb.append(booking.getId());
                        if (bookingsByTenant.iterator().hasNext()) sb.append(", ");
                        else sb.append("\\n");
                    }
                    sb.append("could not be removed, and must be deleted manually before the Tenant can be removed.");
                    LOGGER.log(Level.WARN, sb.toString());
                    System.out.println(sb.toString());
                    obstaclesPresent = true;
                    return;
                }
            }

            // Checking if there are any refunds pending for the Tenant
            List<Refund> refunds = refundService.getRefundsByTenant(tenantId);
            if (!refunds.isEmpty()) {
                StringBuilder sb = new StringBuilder("Tenant removal failed: there are refunds due to the Tenant. Refund IDs: \\n");
                for (int i = 0; i < refunds.size(); i++) {
                    sb.append(refunds.get(i).getId());
                    if (i < refunds.size() - 1) {
                        sb.append(", ");
                    } else {
                        sb.append(".\n");
                    }
                }
                sb.append("These refunds must be settled before the Tenant can be removed.");
                LOGGER.log(Level.WARN, sb.toString());
                System.out.println(sb.toString());
                obstaclesPresent = true;
                return;
            }

            // Checking and stubbing Tenant Payments
            List<TenantPayment> tenantPayments = paymentService.getPaymentsByTenant(tenantId);
            for (TenantPayment payment : tenantPayments) {
                if (!payment.isReceivedFromTenant()) {
                    LOGGER.log(Level.WARN, "Tenant removal failed: payment due from the Tenant has been found. Booking with the ID " + payment.getAssociatedBookingId() + " must be removed first.");
                    System.out.println("Tenant removal failed: payment due from the Tenant has been found. Booking with the ID " + payment.getAssociatedBookingId() + " must be removed first.");
                    obstaclesPresent = true;
                    return;
                }
            }
            Tenant stubTenant = new Tenant();
            stubTenant.setFirstName("Tenant with ID " + tenantId);
            stubTenant.setLastName(" - REMOVED");
            for (TenantPayment payment : tenantPayments) {
                payment.setTenant(stubTenant);
                paymentService.addTenantPayment(payment);
            }

            // replacing the Tenant in LeasingHistories with a stub Tenant
            List<LeasingHistory> leasingHistories = leasingHistoryService.getLeasingHistoryByTenant(tenant);
            for (LeasingHistory history : leasingHistories) {
                history.setTenant(stubTenant);
                leasingHistoryService.addLeasingHistory(history);
            }

            // Removal
            if (!obstaclesPresent) {
                LOGGER.log(Level.INFO, "Tenant with ID {} has been removed.", tenantId);
                System.out.println("Tenant with ID" + tenantId + "has been removed.");
                tenantService.deleteTenant(tenantId);
            } else {
                LOGGER.log(Level.WARN, "Tenant removal failed: please see the error message above");
                System.out.println("Tenant removal failed: please see the error message above");
            }
        } else {
            LOGGER.log(Level.ERROR, "No tenant with the {} ID exists in the database.", tenantId);
            throw new TenantNotFoundException("No tenant found with ID: " + tenantId);
        }
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void removeManager(Long managerId) {
        boolean obstaclesPresent = false;
        if (managerService.getManagerById(managerId).isPresent()) {
            Manager manager = managerService.getManagerById(managerId).get();

            // Processing potentially associated Claims
            List<Claim> claimsByManager = claimService.getClaimsFiledByManager(managerId);
            List<Claim> claimsAgainstManager = claimService.getClaimsAgainstManager(managerId);
            claimsByManager.removeIf(claim -> claim.getClaimStatus().equals(ClaimStatus.RESOLVED));
            claimsAgainstManager.removeIf(claim -> claim.getClaimStatus().equals(ClaimStatus.RESOLVED));
            if (!claimsByManager.isEmpty() || !claimsAgainstManager.isEmpty()) {
                StringBuilder sb = new StringBuilder("Manager removal failed: there are Claims pending from or against the Manager: \\n");
                sb.append("Claims filed by Manager: ");
                for (Claim claim : claimsByManager) {
                    sb.append(claim.getId());
                    if (claimsByManager.iterator().hasNext()) sb.append(", ");
                    else sb.append("\\n");
                }
                sb.append("Claims filed against Manager: ");
                for (Claim claim : claimsAgainstManager) {
                    sb.append(claim.getId());
                    if (claimsAgainstManager.iterator().hasNext()) sb.append(", ");
                    else sb.append("\\n");
                }
                sb.append("These Claims must be resolved before the Manager can be removed.");
                LOGGER.log(Level.WARN, sb.toString());
                System.out.println(sb.toString());
                obstaclesPresent = true;
                return;
            }

            // Processing potentially associated Bookings
            Set<Booking> bookingsByManager = bookingService.getBookingsByManager(manager);
            if (!bookingsByManager.isEmpty()) {
                for (Booking booking : bookingsByManager) {
                    if (booking.getStatus().equals(BookingStatus.PENDING_APPROVAL)) {
                        booking.setStatus(BookingStatus.CANCELLED);
                    }
                    if (booking.getStatus().equals(BookingStatus.PENDING_PAYMENT)) {
                        paymentService.deleteTenantPayment(paymentService.getPaymentByBooking(booking.getId()).getId());
                        booking.setStatus(BookingStatus.CANCELLED);
                    }
                    if (booking.getStatus().equals(BookingStatus.CONFIRMED)) {
                        LOGGER.log(Level.WARN, "There are confirmed Bookings for one or more Properties of this Manager. The Manager cannot be deleted until these Bookings are completed. To prevent new Bookings for this Manager, set its active status to false.");
                        System.out.println("There are confirmed Bookings for one or more Properties of this Manager. The Manager cannot be deleted until these Bookings are completed. To prevent new Bookings for this Manager, set its active status to false.");
                        obstaclesPresent = true;
                        return;
                    }
                    if (booking.getStatus().equals(BookingStatus.CURRENT)) {
                        LOGGER.log(Level.WARN, "There are current Bookings for one or more Properties of this Manager. The Manager cannot be deleted until these Bookings are completed. To prevent new Bookings for this Manager, set its active status to false.");
                        System.out.println("There are current Bookings for one or more Properties of this Manager. The Manager cannot be deleted until these Bookings are completed. To prevent new Bookings for this Manager, set its active status to false.");
                        obstaclesPresent = true;
                        return;
                    }
                    if (booking.getStatus().equals(BookingStatus.OVER)) {
                        LeasingHistory history = new LeasingHistory();
                        Optional<Tenant> optionalTenant = tenantService.getTenantById(booking.getTenantId());
                        optionalTenant.ifPresent(history::setTenant);
                        history.setPropertyId(booking.getProperty().getId());
                        history.setStartDate(booking.getStartDate());
                        history.setEndDate(booking.getEndDate());
                        leasingHistoryService.addLeasingHistory(history);
                        bookingService.deleteBooking(booking.getId());
                    }
                }
                if (!bookingsByManager.isEmpty()) {
                    Iterator<Booking> iterator = bookingsByManager.iterator();
                    while (iterator.hasNext()) {
                        Booking booking = iterator.next();
                        if (booking.getStatus().equals(BookingStatus.OVER) || booking.getStatus().equals(BookingStatus.CANCELLED)) {
                            iterator.remove();
                        }
                    }
                }
                if (!bookingsByManager.isEmpty()) {
                    StringBuilder sb = new StringBuilder("Manager removal failed: could not remove all associated bookings. Bookings with the following IDs: \\n");
                    for (Booking booking : bookingsByManager) {
                        sb.append(booking.getId());
                        if (bookingsByManager.iterator().hasNext()) sb.append(", ");
                        else sb.append("\\n");
                    }
                    sb.append("could not be removed, and must be deleted manually before the Manager can be removed.");
                    LOGGER.log(Level.WARN, sb.toString());
                    System.out.println(sb.toString());
                    obstaclesPresent = true;
                    return;
                }
            }

            // Checking if there are any payments due to the Manager
            List<TenantPayment> payments = paymentService.getPaymentsByManager(managerId);
            List<Long> outstandingPayoutIDs = new ArrayList<>();
            for (TenantPayment payment : payments) {
                if (!payment.isFeePaidToManager()) outstandingPayoutIDs.add(payment.getId());
            }
            if (!outstandingPayoutIDs.isEmpty()) {
                StringBuilder sb = new StringBuilder("Manager removal failed: there are payouts due to this Manager. Associated Tenant payment IDs: \\n");
                for (Long index : outstandingPayoutIDs) {
                    sb.append(index);
                    if (bookingsByManager.iterator().hasNext()) sb.append(", ");
                    else sb.append("\\n");
                }
                sb.append("These payouts must be made before the Manager can be removed.");
                LOGGER.log(Level.WARN, sb.toString());
                System.out.println(sb.toString());
                obstaclesPresent = true;
            }

            // Stubbing the Manager in Properties and deactivating them
            List<Property> managersProperties = propertyService.getPropertiesByManager(managerId);
            Manager stubManager = new Manager();
            stubManager.setManagerName("REMOVED Manager, ID: " + managerId);
            for (Property property : managersProperties) {
                property.setManager(stubManager);
                property.setStatus(PropertyStatus.BLOCKED);
                propertyService.addProperty(property);
            }

            // Removal
            if (!obstaclesPresent) {
                LOGGER.log(Level.INFO, "Manager with ID {} has been removed.", managerId);
                System.out.println("Manager with ID" + managerId + "has been removed.");
                managerService.deleteManager(managerId);
            } else {
                LOGGER.log(Level.WARN, "Manager removal failed: please see the error message above");
                System.out.println("Manager removal failed: please see the error message above");
            }
        } else {
            LOGGER.log(Level.ERROR, "No manager with the {} ID exists in the database.", managerId);
            throw new ManagerNotFoundException("No manager found with ID: " + managerId);
        }
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void addProperty(PropertyAdditionDTO propertyDTO) {
        Property property = PropertyCreationMapper.INSTANCE.toEntity(propertyDTO);
        property.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        property.setBills(new HashSet<>());
        property.setBookings(new HashSet<>());
        propertyService.addProperty(property);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void removeProperty(Long propertyId) {
        Optional<Property> propertyOptional = propertyService.getPropertyById(propertyId);
        if (propertyOptional.isPresent()) {
            Property property = propertyOptional.get();
            Set<Booking> bookings = bookingService.getBookingsByProperty(property);
            for (Booking booking : bookings) {
                if (booking.getStatus().equals(BookingStatus.CONFIRMED) ||
                        booking.getStatus().equals(BookingStatus.PENDING_PAYMENT) ||
                        booking.getStatus().equals(BookingStatus.CURRENT)) {
                    LOGGER.warn("Cannot remove property with active bookings.");
                    return;
                }
            }
            for (Booking booking : bookings) {
                if (booking.getStatus().equals(BookingStatus.OVER)) {
                    LeasingHistory history = new LeasingHistory();
                    Optional<Tenant> optionalTenant = tenantService.getTenantById(booking.getTenantId());
                    optionalTenant.ifPresent(history::setTenant);
                    history.setPropertyId(booking.getProperty().getId());
                    history.setStartDate(booking.getStartDate());
                    history.setEndDate(booking.getEndDate());
                    leasingHistoryService.addLeasingHistory(history);
                    bookingService.deleteBooking(booking.getId());
                }
                if (booking.getStatus().equals(BookingStatus.PENDING_APPROVAL)) booking.setStatus(BookingStatus.CANCELLED);
            }
            propertyService.deleteProperty(propertyId);
            LOGGER.info("Property with ID {} has been removed.", propertyId);
        } else {
            LOGGER.error("Property with ID {} does not exist.", propertyId);
            throw new PropertyNotFoundException("No property found with ID: " + propertyId);
        }
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void resolveClaim(Long claimId, String resolution) {
        claimService.resolveClaim(claimId, resolution);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<AdminRefundDTO> viewPendingRefunds() {
        List<Refund> allRefunds = refundService.getAllRefunds();
        List<AdminRefundDTO> output = new ArrayList<>();
        for (Refund refund : allRefunds) {
            AdminRefundDTO dto = new AdminRefundDTO();
            dto.setAmount(refund.getAmount());
            dto.setBookingId(refund.getBookingId());
            dto.setTenantId(refund.getTenantId());
            dto.setCreatedAt(refund.getCreatedAt());
            int refundPeriodSetOrDefault = 15;
            for (NumericalConfig config : configService.getSystemSettings()) {
                if (config.getName().equals("RefundPaymentPeriodDays")) refundPeriodSetOrDefault = config.getValue().intValue();
            }
            dto.setDueDate(dto.getCreatedAt().toLocalDateTime().toLocalDate().plusDays(refundPeriodSetOrDefault));
            Instant instant = refund.getCreatedAt().toInstant();
            LocalDateTime creationTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
            Duration duration = Duration.between(creationTime.toLocalDate().atStartOfDay(), dto.getDueDate().atStartOfDay());
            long days = duration.toDays();
            if (days > 0) dto.setPaymentDeadline("Payable in " + days + " days");
            if (days <= 0) dto.setPaymentDeadline("OVERDUE");
            output.add(dto);
        }
        return output;
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public boolean settleRefund(Refund refund) {
        try {
            boolean paymentSuccessful = paymentProviderService.stub2(refund);
            if (paymentSuccessful) {
                try {
                    File file = new File(COMPLETED_REFUNDS_FILE_PATH);
                    if (!file.exists()) {
                        file.createNewFile();
                    }
                    try (FileWriter writer = new FileWriter(file, true)) {
                        writer.write(LocalDateTime.now().toString() + " Refund " + refund.getId() + " completed: " + refund.getAmount() + " paid to Tenant " + refund.getTenantId() + ", booking ID: " + refund.getBookingId() + "\n");
                    }
                } catch (IOException e) {
                    LOGGER.error("Error occurred while writing completed refund record: " + e.getMessage());
                }
                LOGGER.info("Refund settled successfully and removed: " + refund);
                refundService.deleteRefund(refund.getId());
                return true;
            } else {
                LOGGER.warn("Payment processing failed for refund: " + refund);
                return false;
            }
        } catch (Exception e) {
            LOGGER.error("Error occurred while settling refund: " + e.getMessage());
            return false;
        }
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<AdminPayoutDTO> viewPendingPayouts() {
        List<Payout> allPayouts = payoutService.getAllPayouts();
        List<AdminPayoutDTO> output = new ArrayList<>();
        for (Payout payout : allPayouts) {
            AdminPayoutDTO dto = new AdminPayoutDTO();
            dto.setAmount(payout.getAmount());
            dto.setBookingId(payout.getBookingId());
            dto.setManagerId(payout.getManagerId());
            dto.setCreatedAt(payout.getCreatedAt());
            int refundPeriodSetOrDefault = 20;
            for (NumericalConfig config : configService.getSystemSettings()) {
                if (config.getName().equals("PayoutPaymentPeriodDays")) refundPeriodSetOrDefault = config.getValue().intValue();
            }
            dto.setDueDate(dto.getCreatedAt().toLocalDateTime().toLocalDate().plusDays(refundPeriodSetOrDefault));
            Instant instant = payout.getCreatedAt().toInstant();
            LocalDateTime creationTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
            Duration duration = Duration.between(creationTime.toLocalDate().atStartOfDay(), dto.getDueDate().atStartOfDay());
            long days = duration.toDays();
            if (days > 0) dto.setPaymentDeadline("Payable in " + days + " days");
            if (days <= 0) dto.setPaymentDeadline("OVERDUE");
            output.add(dto);
        }
        return output;
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public boolean settlePayout(Payout payout) {
        try {
            boolean paymentSuccessful = paymentProviderService.stub3(payout);
            if (paymentSuccessful) {
                try {
                    File file = new File(COMPLETED_PAYOUTS_FILE_PATH);
                    if (!file.exists()) {
                        file.createNewFile();
                    }
                    try (FileWriter writer = new FileWriter(file, true)) {
                        writer.write(LocalDateTime.now().toString() + " Payout " + payout.getId() + " completed: " + payout.getAmount() + " paid to Manager " + payout.getManagerId() + ", booking ID: " + payout.getBookingId() + "\n");
                    }
                } catch (IOException e) {
                    LOGGER.error("Error occurred while writing completed payout record: " + e.getMessage());
                }
                LOGGER.info("Payout settled successfully and removed: " + payout);
                payoutService.deletePayout(payout.getId());
                return true;
            } else {
                LOGGER.warn("Payout processing failed for payout: " + payout);
                return false;
            }
        } catch (Exception e) {
            LOGGER.error("Error occurred while settling payout: " + e.getMessage());
            return false;
        }
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void createPayout(Long bookingId, Long managerId, Double amount) {
        Payout payout = new Payout();
        payout.setBookingId(bookingId);
        payout.setManagerId(managerId);
        payout.setAmount(amount);
        payout.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        payoutService.addPayout(payout);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void createRefund(Long bookingId, Long tenantId, Double amount) {
        Refund refund = new Refund();
        refund.setAmount(amount);
        refund.setBookingId(bookingId);
        refund.setTenantId(tenantId);
        refund.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        refundService.addRefund(refund);
    }

    // AUXILIARY METHODS
    // LUHN LOGIC
    private boolean isValidPaymentCardNumber(String paymentCardNo) {
        char[] processing = paymentCardNo.toCharArray();
        List<Character> digitsOnly = new ArrayList<>();
        List<Integer> output = new ArrayList<>();
        for (char c : processing) {
            if (c == '0' || c == '1' || c == '2' || c == '3'
                    || c == '4' || c == '5' || c == '6'
                    || c == '7' || c == '8' || c == '9') {
                digitsOnly.add(c);
            }
        }
        if (digitsOnly.size() < 1) {
            System.out.println("The input contains no digits!");
            LOGGER.error("The input contains no digits!");
        } else {
            for (Character character : digitsOnly) {
                output.add(Integer.parseInt(String.valueOf(character)));
            }
        }
        if (output.size() < 1) return false;
        boolean valid = false;
        List<Integer> processedList = new ArrayList<>();
        for (int i = 0; i < output.size(); i++) {
            if (i % 2 == 0) {
                if (output.get(i) * 2 < 10) {
                    processedList.add(output.get(i) * 2);
                } else {
                    processedList.add(output.get(i) * 2 - 9);
                }
            }
            else {
                processedList.add(output.get(i));
            }
        }
        int checkSum = 0;
        for (Integer digit : processedList) {
            checkSum += digit;
        }
        if (checkSum % 10 == 0) valid = true;
        return valid;
    }

    private boolean isLoginBusy(String login) {
        return tenantService.getTenantByLogin(login) == null && managerService.getManagerByLogin(login) == null;
    }
    private boolean isEmailBusy(String email) {
        return tenantService.getTenantByEmail(email) == null;
    }

}
