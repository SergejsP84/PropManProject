package lv.emendatus.Destiny_PropMan.service.implementation;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.transaction.annotation.Transactional;
import lv.emendatus.Destiny_PropMan.domain.dto.Admin.AdminPayoutDTO;
import lv.emendatus.Destiny_PropMan.domain.dto.Admin.AdminRefundDTO;
import lv.emendatus.Destiny_PropMan.domain.dto.Admin.SetNumConfigDTO;
import lv.emendatus.Destiny_PropMan.domain.dto.managerial.ManagerProfileDTO;
import lv.emendatus.Destiny_PropMan.domain.dto.managerial.PropertyAdditionDTO;
import lv.emendatus.Destiny_PropMan.domain.dto.profile.TenantDTO_Profile;
import lv.emendatus.Destiny_PropMan.domain.dto.registration.ManagerRegistrationDTO;
import lv.emendatus.Destiny_PropMan.domain.dto.registration.TenantRegistrationDTO;
import lv.emendatus.Destiny_PropMan.domain.entity.*;
import lv.emendatus.Destiny_PropMan.domain.entity.Currency;
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

import javax.crypto.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.time.*;
import java.util.*;

@Service
public class JpaAdminFunctionalityService implements AdminFunctionalityService {

//    @PersistenceContext
//    private EntityManager entityManager;

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
    public final JpaCurrencyService currencyService;
    public final JpaNumericDataMappingService numericDataMappingService;
    public final JpaAmenityService amenityService;
    public final JpaPropertyAmenityService propertyAmenityService;
    public final JpaNumericalConfigService numericalConfigService;
    private static final String AES_SECRET_KEY = System.getenv("AES_SECRET_KEY");
    private final BCryptPasswordEncoder passwordEncoder;
    private final TenantMapper tenantMapper;
    private final ManagerMapper managerMapper;
    private final Logger LOGGER = LogManager.getLogger(JpaPropertyService.class);
    private static final String COMPLETED_REFUNDS_FILE_PATH = "completedRefunds.txt";
    private static final String COMPLETED_PAYOUTS_FILE_PATH = "completedPayouts.txt";



    public JpaAdminFunctionalityService(JpaTenantService tenantService, JpaManagerService managerService, JpaBookingService bookingService, JpaRefundService refundService, JpaTenantPaymentService paymentService, JpaLeasingHistoryService leasingHistoryService, JpaClaimService claimService, JpaPropertyService propertyService, JpaNumericalConfigService configService, JpaThirdPartyPaymentProviderService paymentProviderService, JpaPayoutService payoutService, JpaCurrencyService currencyService, JpaNumericDataMappingService numericDataMappingService, JpaAmenityService amenityService, JpaPropertyAmenityService propertyAmenityService, JpaNumericalConfigService numericalConfigService, BCryptPasswordEncoder passwordEncoder, TenantMapper tenantMapper, ManagerMapper managerMapper) {
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
        this.currencyService = currencyService;
        this.numericDataMappingService = numericDataMappingService;
        this.amenityService = amenityService;
        this.propertyAmenityService = propertyAmenityService;
        this.numericalConfigService = numericalConfigService;
        this.passwordEncoder = passwordEncoder;
        this.tenantMapper = tenantMapper;
        this.managerMapper = managerMapper;
    }


    @Override
    @PreAuthorize("hasAuthority('ADMIN')")
    @Transactional
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
    @Transactional
    @PreAuthorize("hasAuthority('ADMIN')")
    public void toggleManagerStatus(Long managerId) {
        System.out.println("toggleManagerStatus method initiated");
        Optional<Manager> optionalManager = managerService.getManagerById(managerId);
        if (optionalManager.isPresent()) {
//            System.out.println("Manager " + managerId + "has been found");
//            System.out.println("The manager's Active status is " + manager.get().isActive());
            Manager manager = optionalManager.get();
            if (manager.isActive()) {
                manager.setActive(false);
                for (Property property : propertyService.getPropertiesByManager(managerId)) {
                    property.setStatus(PropertyStatus.BLOCKED);
                    propertyService.addProperty(property);
                }
                managerService.addManager(manager);
            } else {
                manager.setActive(true);
                for (Property property : propertyService.getPropertiesByManager(managerId)) {
                    if (property.getStatus().equals(PropertyStatus.BLOCKED)) property.setStatus(PropertyStatus.AVAILABLE);
                    propertyService.addProperty(property);
                }
                managerService.addManager(manager);
            }
//            manager.get().setActive(!manager.get().isActive());
//            System.out.println("The manager's Active status has been set to " + manager.get().isActive());
//            managerService.addManager(manager.get());
//            System.out.println("The updated manager has been saved to the database");
//            Optional<Manager> manager2 = managerService.getManagerById(managerId);
//            if (manager2.isPresent()) {
//                System.out.println("Manager re-obtained from the database");
//                System.out.println("The retrieved manager's Active status is " + manager2.get().isActive());
//            } else {
//                System.out.println("Something else is wrong");
//            }
        } else {
            LOGGER.warn("No manager with the ID {} found in the database", managerId);
        }
    }

    @Override
    @PreAuthorize("hasAuthority('ADMIN')")
    @Transactional
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
        System.out.println("   ---   1) Initiated the tenant creation process");
        Tenant tenant = new Tenant();
        System.out.println("   ---   2) New tenant entity created");
        tenant.setFirstName(registrationDTO.getFirstName());
        System.out.println("   ---   3) First name assigned: " + tenant.getFirstName());
        tenant.setLastName(registrationDTO.getLastName());
        System.out.println("   ---   4) Last name assigned: " + tenant.getLastName());
        tenant.setPhone(registrationDTO.getPhone());
        System.out.println("   ---   5) Phone number assigned: " + tenant.getPhone());
        tenant.setEmail(registrationDTO.getEmail());
        System.out.println("   ---   6) E-mail assigned: " + tenant.getEmail());
        tenant.setIban(registrationDTO.getIban());
        System.out.println("   ---   7) IBAN assigned: " + tenant.getIban());
        tenant.setLogin(registrationDTO.getLogin());
        System.out.println("   ---   8) Login assigned: " + tenant.getLogin());
        String encodedPassword = passwordEncoder.encode(registrationDTO.getPassword());
        tenant.setPassword(encodedPassword);
        System.out.println("   ---   9) Password assigned: " + tenant.getPassword());
        tenant.setRating(0F);
        System.out.println("   ---   10) Rating assigned: " + tenant.getRating());
        tenant.setActive(true);
        System.out.println("   ---   11) Active status assigned: " + tenant.isActive());
        tenant.setTenantPayments(new HashSet<>());
        System.out.println("   ---   12) Assigned an empty HashSet of TenantPayments");
        tenant.setCurrentProperty(null);
        System.out.println("   ---   13) Set the Current Property for the Tenant to NULL");
        tenant.setLeasingHistories(new ArrayList<>());
        System.out.println("   ---   14) Assigned an empty ArrayList of LeasingHistories");
        Long newTenantID = numericalConfigService.getLastTenantId();
        tenant.setId(newTenantID + 1);
        try {
            tenant.setPaymentCardNo(encryptCardNumber(tenant.getId(), UserType.TENANT, registrationDTO.getPaymentCardNo()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        System.out.println("   ---   15) Payment card number assigned: " + tenant.getPaymentCardNo());
        tenant.setCardValidityDate(registrationDTO.getCardValidityDate());
        System.out.println("   ---   16) Card validity date assigned: " + tenant.getCardValidityDate());
        try {
            tenant.setCvv(encryptCVV(tenant.getId(), UserType.TENANT, registrationDTO.getCvv()).toCharArray());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        System.out.println("   ---   17) CVV assigned and encrypted to: " + Arrays.toString(tenant.getCvv()));
        tenant.setConfirmationToken("");
        System.out.println("   ---   18) Confirmation token set to empty, because the Tenant is being created by an Admin");
        tenant.setPreferredCurrency(currencyService.returnBaseCurrency());
        System.out.println("   ---   19) Preferred currency assigned to: " + tenant.getPreferredCurrency().getDesignation());
        List<String> knownIPs = new ArrayList<>();
        tenant.setKnownIps(knownIPs);
        System.out.println("   ---   20) Known IP list set to an empty ArrayList");
        List<GrantedAuthority> authoritiesT = new ArrayList<>();
        authoritiesT.add(new SimpleGrantedAuthority("TENANT"));
        tenant.setAuthorities(authoritiesT);
        tenant.setExpirationTime(LocalDateTime.now());
        tenantService.addTenant(tenant);
        Optional<NumericalConfig> updatedConfig = numericalConfigService.getNumericalConfigByName("LastRegisteredTenantID");
        if (updatedConfig.isPresent()) {
            updatedConfig.get().setValue((newTenantID.doubleValue() + 1));
            numericalConfigService.updateNumericalConfig(updatedConfig.get().getId(), updatedConfig.get());
        }
        System.out.println("Tenant added to the database");
        LOGGER.info("New tenant added by an Admin: ID" + tenant.getId() + ", First name / surname: " + tenant.getFirstName() + " " + tenant.getLastName());
    }


    @Override
    @PreAuthorize("hasAuthority('ADMIN')")
    @Transactional
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
        Long newManagerID = numericalConfigService.getLastManagerId();
        manager.setId(newManagerID + 1);
        try {
            manager.setPaymentCardNo(encryptCardNumber(manager.getId(), UserType.MANAGER, registrationDTO.getPaymentCardNo()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        manager.setCardValidityDate(registrationDTO.getCardValidityDate());
        try {
            manager.setCvv(encryptCVV(manager.getId(), UserType.MANAGER, registrationDTO.getCvv()).toCharArray());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        String encodedPassword = passwordEncoder.encode(registrationDTO.getPassword());
        manager.setPassword(encodedPassword);
        manager.setActive(true);
        manager.setProperties(new HashSet<>());
        manager.setJoinDate(Timestamp.valueOf(LocalDateTime.now()));
        manager.setConfirmationToken("");
        List<String> knownIPs = new ArrayList<>();
        manager.setKnownIps(knownIPs);
        List<GrantedAuthority> authoritiesM = new ArrayList<>();
        authoritiesM.add(new SimpleGrantedAuthority("MANAGER"));
        manager.setAuthorities(authoritiesM);
        manager.setExpirationTime(LocalDateTime.now());
        managerService.addManager(manager);
        Optional<NumericalConfig> updatedConfig = numericalConfigService.getNumericalConfigByName("LastRegisteredManagerID");
        if (updatedConfig.isPresent()) {
            updatedConfig.get().setValue((newManagerID.doubleValue() + 1));
            numericalConfigService.updateNumericalConfig(updatedConfig.get().getId(), updatedConfig.get());
        }
        LOGGER.info("New manager added by an Admin: ID" + manager.getId() + ", Name: " + manager.getManagerName() + ", Description: " + manager.getDescription());
    }

    @Override
    @PreAuthorize("hasAuthority('ADMIN')")
    @Transactional
    public void updateTenantInformation(Long tenantId, TenantDTO_Profile updatedTenantInfo) throws Exception {
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
            if (!updatedTenantInfo.getCardValidityDate().equals(existingTenant.getCardValidityDate())) {
                LOGGER.log(Level.INFO, "Card validity date changed to: "  + updatedTenantInfo.getCardValidityDate());
                System.out.println("Card validity date changed to: "  + updatedTenantInfo.getCardValidityDate());
                existingTenant.setCardValidityDate(updatedTenantInfo.getCardValidityDate());
            }


            if (!updatedTenantInfo.getPaymentCardNo().equals(paymentProviderService.decryptCardNumber(tenantId, UserType.TENANT, existingTenant.getPaymentCardNo()))) {
                LOGGER.log(Level.INFO, "Payment card number changed to: "  + updatedTenantInfo.getPaymentCardNo());
                System.out.println("Payment card number changed to: "  + updatedTenantInfo.getPaymentCardNo());
                try {
                    existingTenant.setPaymentCardNo(encryptUpdatedCardNumber(existingTenant.getId(), UserType.TENANT, updatedTenantInfo.getPaymentCardNo()));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            if (!Arrays.equals(updatedTenantInfo.getCvv(), existingTenant.getCvv())) {
                LOGGER.log(Level.INFO, "CVV changed to: "  + Arrays.toString(updatedTenantInfo.getCvv()));
                System.out.println("CVV changed to: "  + Arrays.toString(updatedTenantInfo.getCvv()));
                try {
                    existingTenant.setCvv(encryptUpdatedCVV(existingTenant.getId(), UserType.TENANT, updatedTenantInfo.getCvv()).toCharArray());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
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
    @PreAuthorize("hasAuthority('ADMIN')")
    @Transactional
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
                try {
                    existingManager.setPaymentCardNo(encryptUpdatedCardNumber(existingManager.getId(), UserType.MANAGER, updatedProfile.getPaymentCardNo()));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            if (!updatedProfile.getCardValidityDate().equals(existingManager.getCardValidityDate())) {
                LOGGER.log(Level.INFO, "Card validity date changed to: "  + updatedProfile.getCardValidityDate());
                System.out.println("Card validity date changed to: "  + updatedProfile.getCardValidityDate());
                existingManager.setCardValidityDate(updatedProfile.getCardValidityDate());
            }
            if (!Arrays.equals(updatedProfile.getCvv(), existingManager.getCvv())) {
                LOGGER.log(Level.INFO, "CVV changed to: "  + Arrays.toString(updatedProfile.getCvv()));
                System.out.println("CVV changed to: "  + Arrays.toString(updatedProfile.getCvv()));
                try {
                    existingManager.setCvv(encryptUpdatedCVV(existingManager.getId(), UserType.MANAGER, updatedProfile.getCvv()).toCharArray());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
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
    @PreAuthorize("hasAuthority('ADMIN')")
    @Transactional
    public void removeTenant(Long tenantId) {
        System.out.println("removeTenant method initiated");
        boolean obstaclesPresent = false;
        if (tenantService.getTenantById(tenantId).isPresent()) {
            Tenant tenant = tenantService.getTenantById(tenantId).get();
            System.out.println("Tenant found - " + tenant.getFirstName() + tenant.getLastName());

            // Processing potentially associated Claims
            List<Claim> claimsByTenant = claimService.getClaimsFiledByTenant(tenantId);
            List<Claim> claimsAgainstTenant = claimService.getClaimsAgainstTenant(tenantId);
            if (!claimsByTenant.isEmpty()) claimsByTenant.removeIf(claim -> claim.getClaimStatus().equals(ClaimStatus.RESOLVED));
            if (!claimsAgainstTenant.isEmpty()) claimsAgainstTenant.removeIf(claim -> claim.getClaimStatus().equals(ClaimStatus.RESOLVED));
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

            System.out.println("Passed the claim check stage");


            // Processing potentially associated Bookings
            Set<Booking> bookingsByTenant = bookingService.getBookingsByTenant(tenant);
            if (!bookingsByTenant.isEmpty()) {
                System.out.println("Checking bookings...");
                for (Booking booking : bookingsByTenant) {
                    if (booking.getStatus().equals(BookingStatus.PENDING_APPROVAL)) {
                        booking.setStatus(BookingStatus.CANCELLED);
                    }
                    System.out.println("Done with bookings pending approval");
                    if (booking.getStatus().equals(BookingStatus.PENDING_PAYMENT)) {
                        List<TenantPayment> payments = paymentService.getPaymentsByTenant(tenantId);
                        List<TenantPayment> completedPayments = new ArrayList<>();
                        for (TenantPayment tenantPayment : payments) {
                            if (tenantPayment.isReceivedFromTenant()) {
                                completedPayments.add(tenantPayment);
                            }
                        }
                        if (completedPayments.isEmpty()) booking.setStatus(BookingStatus.CANCELLED);
                    }
                    System.out.println("Done with bookings pending payment");
                    if (booking.getStatus().equals(BookingStatus.CONFIRMED)) {
                        Refund refund = new Refund();
                        refund.setTenantId(tenantId);
                        refund.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
                        refund.setBookingId(booking.getId());
                        refund.setAmount(paymentService.getPaymentByBooking(booking.getId()).getAmount());
                        refundService.addRefund(refund);
                        booking.setStatus(BookingStatus.CANCELLED);
                    }
                    System.out.println("Done with cancelling paid bookings");
                    if (booking.getStatus().equals(BookingStatus.CURRENT)) {
                        LOGGER.log(Level.WARN, "Cannot remove a Tenant currently residing in a Property.");
                        System.out.println("Cannot remove a Tenant currently residing in a Property.");
                        obstaclesPresent = true;
                        return;
                    }
                    System.out.println("Done with processing current bookings");
                    if (booking.getStatus().equals(BookingStatus.OVER)) {
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
                        bookingService.deleteBooking(booking.getId());
                    }
                    System.out.println("Done with removing OVER bookings");
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
                System.out.println("Removed CANCELLED and OVER bookings from the list");
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

            System.out.println("Passed the booking check stage");

            // Checking if there are any refunds pending for the Tenant
            List<Refund> refunds = refundService.getRefundsByTenant(tenantId);
            if (!refunds.isEmpty()) {
                StringBuilder sb = new StringBuilder("Tenant removal failed: there are refunds due to the Tenant. Refund IDs: \n\n");
                for (int i = 0; i < refunds.size(); i++) {
                    sb.append(refunds.get(i).getId());
                    if (i < refunds.size() - 1) {
                        sb.append(", ");
                    } else {
                        sb.append(".");
                    }
                }
                LOGGER.log(Level.WARN, sb.toString());
                System.out.println(sb.toString());
                System.out.println("These refunds must be settled before the Tenant can be removed.");
                obstaclesPresent = true;
                return;
            }

            System.out.println("Passed the refund check stage");

            // Checking and stubbing Tenant Payments
            List<TenantPayment> tenantPayments = paymentService.getPaymentsByTenant(tenantId);
            for (TenantPayment payment : tenantPayments) {
                if (!payment.isReceivedFromTenant()) {
                    if (bookingService.getBookingById(payment.getAssociatedBookingId()).isPresent()) {
                        if (!bookingService.getBookingById(payment.getAssociatedBookingId()).get().getStatus().equals(BookingStatus.CANCELLED)) {
                            LOGGER.log(Level.WARN, "Tenant removal failed: payment due from the Tenant has been found. Booking with the ID " + payment.getAssociatedBookingId() + " must be removed first.");
                            System.out.println("Tenant removal failed: payment due from the Tenant has been found. Booking with the ID " + payment.getAssociatedBookingId() + " must be removed first.");
                            obstaclesPresent = true;
                            return;
                        }
                    }
                }
            }
//            Tenant stubTenant = new Tenant();
//            stubTenant.setFirstName("Tenant with ID " + tenantId);
//            stubTenant.setLastName(" - REMOVED");
//            stubTenant.setLogin("StubTenant");
//            stubTenant.setPassword("Babababa000");
//            stubTenant.setCardValidityDate(YearMonth.now());
//            stubTenant.setPaymentCardNo("4453112311231123");
//            stubTenant.setCvv(new char[]{0, 0, 0});
//            stubTenant.setEmail("bonk@bonk.bonk");
            for (TenantPayment payment : tenantPayments) {
                if (payment.isReceivedFromTenant() && payment.isFeePaidToManager()) {
                    System.out.println("Removing TenantPayment with ID " + payment.getId());
                    paymentService.deleteTenantPayment(payment.getId());
                } else if (!payment.isReceivedFromTenant() && !payment.isFeePaidToManager()) {
                    System.out.println("Removing TenantPaymentwitd ID " + payment.getId());
                    paymentService.deleteTenantPayment(payment.getId());
                } else {
                    obstaclesPresent = true;
                    LOGGER.log(Level.WARN, "Cannot remove a Tenant while there are unsettled associated payments");
                    System.out.println("Cannot remove a Tenant while there are unsettled associated payments");
                }
            }

            System.out.println("Passed the TenantPayment stubbing stage");

            // replacing the Tenant in LeasingHistories with a stub Tenant
//            entityManager.persist(stubTenant);
            List<LeasingHistory> leasingHistories = leasingHistoryService.getLeasingHistoryByTenant(tenant);
            for (LeasingHistory history : leasingHistories) {
                leasingHistoryService.deleteLeasingHistory(history.getId());
            }

            System.out.println("Passed the LeasingHistory stubbing stage");

            // Removal
            if (!obstaclesPresent) {
                LOGGER.log(Level.INFO, "Tenant with ID {} has been removed.", tenantId);
                System.out.println("Tenant with ID" + tenantId + " has been removed.");
                tenantService.deleteTenant(tenantId);
                System.out.println("Tenant removal completed");
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
    @PreAuthorize("hasAuthority('ADMIN')")
    @Transactional
    public void removeManager(Long managerId) {
        boolean obstaclesPresent = false;
        if (managerService.getManagerById(managerId).isPresent()) {
            Manager manager = managerService.getManagerById(managerId).get();

            // Processing potentially associated Claims
            List<Claim> claimsByManager = claimService.getClaimsFiledByManager(managerId);
            List<Claim> claimsAgainstManager = claimService.getClaimsAgainstManager(managerId);
            if (!claimsByManager.isEmpty()) claimsByManager.removeIf(claim -> claim.getClaimStatus().equals(ClaimStatus.RESOLVED));
            if (!claimsAgainstManager.isEmpty()) claimsAgainstManager.removeIf(claim -> claim.getClaimStatus().equals(ClaimStatus.RESOLVED));
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
                        if (optionalTenant.isPresent()) {
                            List<LeasingHistory> histories = optionalTenant.get().getLeasingHistories();
                            histories.add(history);
                            optionalTenant.get().setLeasingHistories(histories);
                            tenantService.addTenant(optionalTenant.get());
                        }
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
                if (!payment.isFeePaidToManager()) {
                    outstandingPayoutIDs.add(payment.getId());
                }
            }
            if (!outstandingPayoutIDs.isEmpty()) {
                StringBuilder sb = new StringBuilder("Manager removal failed: there are payouts due to this Manager. Associated Tenant payment IDs:\n");
                for (int i = 0; i < outstandingPayoutIDs.size(); i++) {
                    sb.append(outstandingPayoutIDs.get(i));
                    if (i < outstandingPayoutIDs.size() - 1) {
                        sb.append(", ");
                    }
                }
                sb.append("\nThese payouts must be made before the Manager can be removed.");
                LOGGER.log(Level.WARN, sb.toString());
                System.out.println(sb.toString());
                obstaclesPresent = true;
            }

            // Stubbing the Manager in Properties and deactivating them
            if (!obstaclesPresent) {
                List<Property> managersProperties = propertyService.getPropertiesByManager(managerId);
                for (Property property : managersProperties) {
                    property.setManager(null);
                    property.setStatus(PropertyStatus.BLOCKED);
                    propertyService.addProperty(property);
                }
            }

            // Removal
            if (!obstaclesPresent) {
                LOGGER.log(Level.INFO, "Manager with ID {} has been removed.", managerId);
                System.out.println("Manager with ID" + managerId + " has been removed.");
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
    @PreAuthorize("hasAuthority('ADMIN')")
    @Transactional
    public void addProperty(PropertyAdditionDTO propertyDTO) {
        Optional<Manager> managerOpt = managerService.getManagerById(propertyDTO.getManagerId());
        if (managerOpt.isEmpty()) {
            throw new ManagerNotFoundException("Manager not found with ID: " + propertyDTO.getManagerId());
        }
        Property property = PropertyCreationMapper.INSTANCE.fromDTO(propertyDTO);
        property.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        property.setBills(new HashSet<>());
        property.setBookings(new HashSet<>());
        property.setManager(managerOpt.get());
        propertyService.addProperty(property);
    }

    @Override
    @PreAuthorize("hasAuthority('ADMIN')")
    @Transactional
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
                    if (optionalTenant.isPresent()) {
                        List<LeasingHistory> histories = optionalTenant.get().getLeasingHistories();
                        histories.add(history);
                        optionalTenant.get().setLeasingHistories(histories);
                        tenantService.addTenant(optionalTenant.get());
                    }
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
    @PreAuthorize("hasAuthority('ADMIN')")
    public void resolveClaim(Long claimId, String resolution) {
        claimService.resolveClaim(claimId, resolution);
    }

    @Override
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<AdminRefundDTO> viewPendingRefunds() {
        List<Refund> allRefunds = refundService.getAllRefunds();
        List<AdminRefundDTO> output = new ArrayList<>();
        for (Refund refund : allRefunds) {
            AdminRefundDTO dto = new AdminRefundDTO();
            dto.setAmount(refund.getAmount());
            dto.setBookingId(refund.getBookingId());
            dto.setTenantId(refund.getTenantId());
            dto.setCreatedAt(refund.getCreatedAt());
            dto.setCurrency(refund.getCurrency());
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
    @PreAuthorize("hasAuthority('ADMIN')")
    @Transactional
    public boolean settleRefund(Refund refund) {
        try {
            boolean paymentSuccessful = false;
            if (tenantService.getTenantById(refund.getTenantId()).isPresent()) {
                if (tenantService.getTenantById(refund.getTenantId()).get().getCardValidityDate().isBefore(YearMonth.now()))                 {
                    LOGGER.error("Refund processing failed for refund with ID {} due to the expiration of the tenant's payment card.", refund.getId());
                } else{
                    // Add third-party payment processing mechanism in the service
                    paymentSuccessful = paymentProviderService.stub2(refund);
                }
            }
            if (paymentSuccessful) {
                try {
                    File file = new File(COMPLETED_REFUNDS_FILE_PATH);
                    if (!file.exists()) {
                        file.createNewFile();
                    }
                    try (FileWriter writer = new FileWriter(file, true)) {
                        String currencyDesignation = "";
                        if (currencyService.getCurrencyById(refund.getId()).isPresent()) currencyDesignation = currencyService.getCurrencyById(refund.getId()).get().getDesignation();
                        writer.write(LocalDateTime.now().toString() + " Refund " + refund.getId() + " completed: " + currencyDesignation + " " + refund.getAmount() + " paid to Tenant " + refund.getTenantId() + ", booking ID: " + refund.getBookingId() + "\n");
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
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<AdminPayoutDTO> viewPendingPayouts() {
        List<Payout> allPayouts = payoutService.getAllPayouts();
        List<AdminPayoutDTO> output = new ArrayList<>();
        for (Payout payout : allPayouts) {
            AdminPayoutDTO dto = new AdminPayoutDTO();
            dto.setAmount(payout.getAmount());
            dto.setBookingId(payout.getBookingId());
            dto.setManagerId(payout.getManagerId());
            dto.setCreatedAt(payout.getCreatedAt());
            dto.setCurrency(payout.getCurrency());
            int payoutPeriodSetOrDefault = 20;
            for (NumericalConfig config : configService.getSystemSettings()) {
                if (config.getName().equals("PayoutPaymentPeriodDays")) payoutPeriodSetOrDefault = config.getValue().intValue();
            }
            dto.setDueDate(dto.getCreatedAt().toLocalDateTime().toLocalDate().plusDays(payoutPeriodSetOrDefault));
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
    @PreAuthorize("hasAuthority('ADMIN')")
    @Transactional
    public boolean settlePayout(Payout payout) {
        try {
            boolean paymentSuccessful = false;
            if (managerService.getManagerById(payout.getManagerId()).isPresent()) {
                if (managerService.getManagerById(payout.getManagerId()).get().getCardValidityDate().isBefore(YearMonth.now()))                 {
                    LOGGER.error("Payout processing failed for payout with ID {} due to the expiration of the manager's payment card.", payout.getId());
                } else{
                    // Add third-party payment processing mechanism in the service
                    paymentSuccessful = paymentProviderService.stub3(payout);
                }
            }
            if (paymentSuccessful) {
                try {
                    File file = new File(COMPLETED_PAYOUTS_FILE_PATH);
                    if (!file.exists()) {
                        file.createNewFile();
                    }
                    try (FileWriter writer = new FileWriter(file, true)) {
                        writer.write(LocalDateTime.now().toString() + " Payout " + payout.getId() + " completed: " + payout.getCurrency().getDesignation() + " " + payout.getAmount() + " paid to Manager " + payout.getManagerId() + ", booking ID: " + payout.getBookingId() + "\n");
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
    @PreAuthorize("hasAuthority('ADMIN')")
    @Transactional
    public void createPayout(Long bookingId, Long managerId, Double amount, Long currencyId) {
        Payout payout = new Payout();
        payout.setBookingId(bookingId);
        payout.setManagerId(managerId);
        payout.setAmount(amount);
        payout.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        if (currencyService.getCurrencyById(currencyId).isPresent()) {
        payout.setCurrency(currencyService.getCurrencyById(currencyId).get());
        } else {
            payout.setCurrency(currencyService.returnBaseCurrency());
        }
        payoutService.addPayout(payout);
    }

    @Override
    @PreAuthorize("hasAuthority('ADMIN')")
    @Transactional
    public void createRefund(Long bookingId, Long tenantId, Double amount, Long currencyId) {
        Refund refund = new Refund();
        refund.setAmount(amount);
        refund.setBookingId(bookingId);
        refund.setTenantId(tenantId);
        refund.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        if (currencyService.getCurrencyById(currencyId).isPresent()) {
            refund.setCurrency(currencyService.getCurrencyById(currencyId).get());
        } else {
            refund.setCurrency(currencyService.returnBaseCurrency());
        }
        refundService.addRefund(refund);
    }

    @Override
    @PreAuthorize("hasAuthority('ADMIN')")
    @Transactional
    public void addNewCurrency(String designation, Double rateToBase) {
        boolean exists = false;
        for (Currency currency : currencyService.getAllCurrencies()) {
            if (currency.getDesignation().equals(designation)) {
                LOGGER.warn("This currency already exists in the database.");
                exists = true;
            }
        }
        if (!exists) {
            Currency currency = new Currency();
            currency.setIsBaseCurrency(false);
            currency.setDesignation(designation);
            currency.setRateToBase(rateToBase);
            currencyService.addCurrency(currency);
        }
    }

    @Override
    @PreAuthorize("hasAuthority('ADMIN')")
    @Transactional
    public void setNewBaseCurrency(Long newBaseCurrencyId, List<Double> ratesForOtherCurrencies) {
        currencyService.setBaseCurrency(newBaseCurrencyId);
        int listIndex = 0;
        for (Currency currency : currencyService.getAllCurrencies()) {
            if (!currency.getIsBaseCurrency()) {
                currency.setRateToBase(ratesForOtherCurrencies.get(listIndex));
                listIndex++;
            }
        }
    }

    @Override
    @PreAuthorize("hasAuthority('ADMIN')")
    @Transactional
    public void setNumericalConfigs(SetNumConfigDTO dto) {
        // Yeah, I know it's perverted... It's just that the project has evolved FAR from the initially contemplated setup, sorry
        List<NumericalConfig> systemSettings = configService.getSystemSettings();
        boolean mustCreateRefundPaymentPeriodDays = true;
        boolean mustCreatePayoutPaymentPeriodDays = true;
        boolean mustCreateClaimPeriodDays = true;
        boolean mustCreatePaymentPeriodDays = true;
        boolean mustCreateEarlyTerminationPenalty = true;
        boolean mustCreateSystemInterestRate = true;
        boolean mustCreateLateCancellationPeriodInDays = true;
        boolean mustCreateUrgentCancellationPeriodInDays = true;
        boolean mustCreateUrgentCancellationPenalty = true;
        boolean mustCreateLateCancellationPenalty = true;
        boolean mustCreateRegularCancellationPenalty = true;
        for (NumericalConfig config : systemSettings) {
            if (config.getName().equals("RefundPaymentPeriodDays")) mustCreateRefundPaymentPeriodDays = false;
            if (config.getName().equals("PayoutPaymentPeriodDays")) mustCreatePayoutPaymentPeriodDays = false;
            if (config.getName().equals("ClaimPeriodDays")) mustCreateClaimPeriodDays = false;
            if (config.getName().equals("PaymentPeriodDays")) mustCreatePaymentPeriodDays = false;
            if (config.getName().equals("EarlyTerminationPenalty")) mustCreateEarlyTerminationPenalty = false;
            if (config.getName().equals("SystemInterestRate")) mustCreateSystemInterestRate = false;
            if (config.getName().equals("UrgentCancellationPeriodInDays")) mustCreateUrgentCancellationPeriodInDays = false;
            if (config.getName().equals("LateCancellationPeriodInDays")) mustCreateLateCancellationPeriodInDays = false;
            if (config.getName().equals("UrgentCancellationPenalty")) mustCreateUrgentCancellationPenalty = false;
            if (config.getName().equals("LateCancellationPenalty")) mustCreateLateCancellationPenalty = false;
            if (config.getName().equals("RegularCancellationPenalty")) mustCreateRegularCancellationPenalty = false;
        }
        if (mustCreateRefundPaymentPeriodDays) {
            NumericalConfig config = new NumericalConfig();
            config.setName("RefundPaymentPeriodDays");
            config.setValue(15.0);
            config.setType(NumConfigType.SYSTEM_SETTING);
            configService.addNumericalConfig(config);
            LOGGER.info("Refund payment period set to 15 days");
        } else {
            Optional<NumericalConfig> config = configService.getNumericalConfigByName("RefundPaymentPeriodDays");
            if (config.isPresent()) {
                if (!config.get().getValue().equals((double) dto.getRefundPaymentPeriodDays())) {
                    config.get().setValue((double) dto.getRefundPaymentPeriodDays());
                    configService.addNumericalConfig(config.get());
                    LOGGER.info("Refund payment period set to " + dto.getRefundPaymentPeriodDays() + " days");
                }
            }
        }
        if (mustCreatePayoutPaymentPeriodDays) {
            NumericalConfig config = new NumericalConfig();
            config.setName("PayoutPaymentPeriodDays");
            config.setValue(20.0);
            config.setType(NumConfigType.SYSTEM_SETTING);
            configService.addNumericalConfig(config);
            LOGGER.info("Payout payment period set to 20 days");
        } else {
            Optional<NumericalConfig> config = configService.getNumericalConfigByName("PayoutPaymentPeriodDays");
            if (config.isPresent()) {
                if (!config.get().getValue().equals((double) dto.getPayoutPaymentPeriodDays())) {
                    config.get().setValue((double) dto.getPayoutPaymentPeriodDays());
                    configService.addNumericalConfig(config.get());
                    LOGGER.info("Payout payment period set to " + dto.getPayoutPaymentPeriodDays() + " days");
                }
            }
        }
        if (mustCreateClaimPeriodDays) {
            NumericalConfig config = new NumericalConfig();
            config.setName("ClaimPeriodDays");
            config.setValue(7.0);
            config.setType(NumConfigType.SYSTEM_SETTING);
            configService.addNumericalConfig(config);
            LOGGER.info("Claim period set to 7 days");
        } else {
            Optional<NumericalConfig> config = configService.getNumericalConfigByName("ClaimPeriodDays");
            if (config.isPresent()) {
                if (!config.get().getValue().equals((double) dto.getClaimPeriodDays())) {
                    config.get().setValue((double) dto.getClaimPeriodDays());
                    configService.addNumericalConfig(config.get());
                    LOGGER.info("Claim period set to " + dto.getClaimPeriodDays() + " days");
                }
            }
        }
        if (mustCreateEarlyTerminationPenalty) {
            NumericalConfig config = new NumericalConfig();
            config.setName("EarlyTerminationPenalty");
            config.setValue(0.0);
            config.setType(NumConfigType.SYSTEM_SETTING);
            configService.addNumericalConfig(config);
            LOGGER.info("Early termination penalty set to 0%");
        } else {
            Optional<NumericalConfig> config = configService.getNumericalConfigByName("EarlyTerminationPenalty");
            if (config.isPresent()) {
                if (!config.get().getValue().equals((double) dto.getEarlyTerminationPenalty())) {
                    config.get().setValue((double) dto.getEarlyTerminationPenalty());
                    configService.addNumericalConfig(config.get());
                    LOGGER.info("Early termination penalty set to " + dto.getEarlyTerminationPenalty() + "%");
                }
            }
        }
        if (mustCreatePaymentPeriodDays) {
            NumericalConfig config = new NumericalConfig();
            config.setName("PaymentPeriodDays");
            config.setValue(8.0);
            config.setType(NumConfigType.SYSTEM_SETTING);
            configService.addNumericalConfig(config);
            LOGGER.info("Tenant payment period set to 8 days before arrival");
        } else {
            Optional<NumericalConfig> config = configService.getNumericalConfigByName("PaymentPeriodDays");
            if (config.isPresent()) {
                if (!config.get().getValue().equals((double) dto.getPaymentPeriodDays())) {
                    config.get().setValue((double) dto.getPaymentPeriodDays());
                    configService.addNumericalConfig(config.get());
                    LOGGER.info("Tenant payment period set to " + dto.getPaymentPeriodDays() + " days before arrival");
                }
            }
        }
        if (mustCreateSystemInterestRate) {
            NumericalConfig config = new NumericalConfig();
            config.setName("SystemInterestRate");
            config.setValue(10.0);
            config.setType(NumConfigType.SYSTEM_SETTING);
            configService.addNumericalConfig(config);
            LOGGER.info("System interest rate set to 10%");
        } else {
            Optional<NumericalConfig> config = configService.getNumericalConfigByName("SystemInterestRate");
            if (config.isPresent()) {
                if (!config.get().getValue().equals((double) dto.getSystemInterestRate())) {
                    config.get().setValue((double) dto.getSystemInterestRate());
                    configService.addNumericalConfig(config.get());
                    LOGGER.info("System interest rate set to " + dto.getSystemInterestRate() + "%");
                }
            }
        }
        if (mustCreateLateCancellationPeriodInDays) {
            NumericalConfig config = new NumericalConfig();
            config.setName("LateCancellationPeriodInDays");
            config.setValue(10.0);
            config.setType(NumConfigType.SYSTEM_SETTING);
            configService.addNumericalConfig(config);
            LOGGER.info("Late cancellation period set to 10 days");
        } else {
            Optional<NumericalConfig> config = configService.getNumericalConfigByName("LateCancellationPeriodInDays");
            if (config.isPresent()) {
                if (!config.get().getValue().equals((double) dto.getLateCancellationPeriodInDays())) {
                    config.get().setValue((double) dto.getLateCancellationPeriodInDays());
                    configService.addNumericalConfig(config.get());
                    LOGGER.info("Late cancellation period set to " + dto.getLateCancellationPeriodInDays() + " days");
                }
            }
        }
        if (mustCreateUrgentCancellationPeriodInDays) {
            NumericalConfig config = new NumericalConfig();
            config.setName("UrgentCancellationPeriodInDays");
            config.setValue(3.0);
            config.setType(NumConfigType.SYSTEM_SETTING);
            configService.addNumericalConfig(config);
            LOGGER.info("Urgent cancellation period set to 3 days");
        } else {
            Optional<NumericalConfig> config = configService.getNumericalConfigByName("UrgentCancellationPeriodInDays");
            if (config.isPresent()) {
                if (!config.get().getValue().equals((double) dto.getUrgentCancellationPeriodInDays())) {
                    config.get().setValue((double) dto.getUrgentCancellationPeriodInDays());
                    configService.addNumericalConfig(config.get());
                    LOGGER.info("Urgent cancellation period set to " + dto.getUrgentCancellationPeriodInDays() + " days");
                }
            }
        }
        if (mustCreateUrgentCancellationPenalty) {
            NumericalConfig config = new NumericalConfig();
            config.setName("UrgentCancellationPenalty");
            config.setValue(50.0);
            config.setType(NumConfigType.SYSTEM_SETTING);
            configService.addNumericalConfig(config);
            LOGGER.info("Urgent cancellation penalty set to 50%");
        } else {
            Optional<NumericalConfig> config = configService.getNumericalConfigByName("UrgentCancellationPenalty");
            if (config.isPresent()) {
                if (!config.get().getValue().equals((double) dto.getUrgentCancellationPenaltyPercentage())) {
                    config.get().setValue((double) dto.getUrgentCancellationPenaltyPercentage());
                    configService.addNumericalConfig(config.get());
                    LOGGER.info("Urgent cancellation penalty set to " + dto.getUrgentCancellationPenaltyPercentage() + "%");
                }
            }
        }
        if (mustCreateLateCancellationPenalty) {
            NumericalConfig config = new NumericalConfig();
            config.setName("LateCancellationPenalty");
            config.setValue(25.0);
            config.setType(NumConfigType.SYSTEM_SETTING);
            configService.addNumericalConfig(config);
            LOGGER.info("Late cancellation penalty set to 25%");
        } else {
            Optional<NumericalConfig> config = configService.getNumericalConfigByName("LateCancellationPenalty");
            if (config.isPresent()) {
                if (!config.get().getValue().equals((double) dto.getLateCancellationPenaltyPercentage())) {
                    config.get().setValue((double) dto.getLateCancellationPenaltyPercentage());
                    configService.addNumericalConfig(config.get());
                    LOGGER.info("Late cancellation penalty set to " + dto.getLateCancellationPenaltyPercentage() + "%");
                }
            }
        }
        if (mustCreateRegularCancellationPenalty) {
            NumericalConfig config = new NumericalConfig();
            config.setName("RegularCancellationPenalty");
            config.setValue(0.0);
            config.setType(NumConfigType.SYSTEM_SETTING);
            configService.addNumericalConfig(config);
            LOGGER.info("Regular cancellation penalty set to 0%");
        } else {
            Optional<NumericalConfig> config = configService.getNumericalConfigByName("RegularCancellationPenalty");
            if (config.isPresent()) {
                if (!config.get().getValue().equals((double) dto.getRegularCancellationPenaltyPercentage())) {
                    config.get().setValue((double) dto.getRegularCancellationPenaltyPercentage());
                    configService.addNumericalConfig(config.get());
                    LOGGER.info("Regular cancellation penalty set to " + dto.getRegularCancellationPenaltyPercentage() + "%");
                }
            }
        }
    }

    @Override
    @PreAuthorize("hasAuthority('ADMIN')")
    public void addAmenityToDatabase(String amenityDescription) {
        boolean alreadyExists = false;
        for (Amenity existing_one : amenityService.getAllAmenities()) {
            if (existing_one.getDescription().equals(amenityDescription)) {
                alreadyExists = true;
                LOGGER.info("This amenity already exists in the database");
                System.out.println("This amenity already exists in the database");
            }
        }
        if (!alreadyExists) {
            Amenity amenity = new Amenity();
            amenity.setDescription(amenityDescription);
            amenityService.addAmenity(amenity);
            LOGGER.info("Amenity {} added to the amenity database", amenityDescription);
            System.out.println("Amenity " + amenityDescription + " added to the amenity database");
        }
    }

    @Override
    @PreAuthorize("hasAuthority('ADMIN')")
    @Transactional
    public void removeAmenityFromDatabase(Long amenityId) {
        if (amenityService.getAmenityById(amenityId).isPresent()) {
            List<Long> relationsToBeRemoved = new ArrayList<>();
            String removedAmenityDescription = amenityService.getAmenityById(amenityId).get().getDescription();
            for (PropertyAmenity propertyAmenity : propertyAmenityService.getAllPropertyAmenities()) {
                if (propertyAmenity.getAmenity_id().equals(amenityId)) relationsToBeRemoved.add(propertyAmenity.getId());
            }
            int removalCounter = 0;
            for (Long id : relationsToBeRemoved) {
                propertyAmenityService.deletePropertyAmenity(id);
                removalCounter++;
            }
            amenityService.deleteAmenity(amenityId);
            LOGGER.info("Amenity {} and {} links to properties have been removed", removedAmenityDescription, removalCounter);
            System.out.println("Amenity " + removedAmenityDescription + " and " + removalCounter + " associated links to properties have been removed");
        } else {
            LOGGER.log(Level.ERROR, "No amenity with the {} ID exists in the database.", amenityId);
            throw new EntityNotFoundException("No amenity found with ID: " + amenityId);
        }
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
                return !(tenantService.getTenantByLogin(login) == null && managerService.getManagerByLogin(login) == null);
    }
    private boolean isEmailBusy(String email) {
        return !(tenantService.getTenantByEmail(email) == null);
    }

    // Encrypt CVV using AES encryption, will also be used for card number encryption
    public String encryptCVV(Long userId, UserType userType, char[] cvv) throws Exception {
        try {
        System.out.println("      ---   a) Initiated the encryptCVV method");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        System.out.println("      ---   b) Created a Cipher: " + cipher);
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(128); // 128-bit key length
        SecretKey secretKey = keyGenerator.generateKey();
        byte[] aesKeyBytes = secretKey.getEncoded();
        System.out.println("      ---   c) Generated AES Key (Base64 Encoded): " + Base64.getEncoder().encodeToString(aesKeyBytes));
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        System.out.println("      ---   d) Cipher.init triggered successfully");
        byte[] encryptedCVVBytes = cipher.doFinal(new String(cvv).getBytes());
        System.out.println("      ---   e) Byte array created: " + Arrays.toString(encryptedCVVBytes));
        // implement logics for saving the secret key alongside the encrypted value
        numericDataMappingService.saveCVVSecretKey(userId, userType, secretKey);
            System.out.println("      ---   f) Saved the secret key " + secretKey.toString() + " to the database");
        return Base64.getEncoder().encodeToString(encryptedCVVBytes);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        System.out.println("FAILED TO ENCRYPT THE CVV!");
        return Arrays.toString(cvv);
    }

    public String encryptCardNumber(Long userId, UserType userType, String cardNumber) throws Exception {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(128); // 128-bit key length
            SecretKey secretKey = keyGenerator.generateKey();
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedCardNumberBytes = cipher.doFinal(cardNumber.getBytes());
            numericDataMappingService.saveCardNumberSecretKey(userId, userType, secretKey);
            return Base64.getEncoder().encodeToString(encryptedCardNumberBytes);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException | IllegalBlockSizeException e) {
            System.out.println("Error encrypting card number: " + e.getMessage());
        }
        return null;
    }

    public String encryptUpdatedCardNumber(Long userId, UserType userType, String cardNumber) throws Exception {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(128); // 128-bit key length
            SecretKey secretKey = keyGenerator.generateKey();
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedCardNumberBytes = cipher.doFinal(cardNumber.getBytes());
            numericDataMappingService.saveCardNumberSecretKey(userId, userType, secretKey);
            return Base64.getEncoder().encodeToString(encryptedCardNumberBytes);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException | IllegalBlockSizeException e) {
            System.out.println("Error encrypting card number: " + e.getMessage());
        }
        return null;
    }

    public String encryptUpdatedCVV(Long userId, UserType userType, char[] cvv) throws Exception {
        try {
            System.out.println("      ---   a) Initiated the encryptCVV method");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            System.out.println("      ---   b) Created a Cipher: " + cipher);
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(128); // 128-bit key length
            SecretKey secretKey = keyGenerator.generateKey();
            byte[] aesKeyBytes = secretKey.getEncoded();
            System.out.println("      ---   c) Generated AES Key (Base64 Encoded): " + Base64.getEncoder().encodeToString(aesKeyBytes));
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            System.out.println("      ---   d) Cipher.init triggered successfully");
            byte[] encryptedCVVBytes = cipher.doFinal(new String(cvv).getBytes());
            System.out.println("      ---   e) Byte array created: " + Arrays.toString(encryptedCVVBytes));
            // implement logics for saving the secret key alongside the encrypted value
            numericDataMappingService.saveCVVSecretKey(userId, userType, secretKey);
            System.out.println("      ---   f) Saved the secret key " + secretKey.toString() + " to the database");
            return Base64.getEncoder().encodeToString(encryptedCVVBytes);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        System.out.println("FAILED TO ENCRYPT THE CVV!");
        return Arrays.toString(cvv);
    }
}
