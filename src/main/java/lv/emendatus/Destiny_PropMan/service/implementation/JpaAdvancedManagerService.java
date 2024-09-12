package lv.emendatus.Destiny_PropMan.service.implementation;

import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.*;
import lv.emendatus.Destiny_PropMan.mapper.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import lv.emendatus.Destiny_PropMan.domain.dto.managerial.*;
import lv.emendatus.Destiny_PropMan.domain.dto.reservation.BookingDTO_Reservation;
import lv.emendatus.Destiny_PropMan.domain.entity.*;
import lv.emendatus.Destiny_PropMan.domain.entity.Currency;
import lv.emendatus.Destiny_PropMan.exceptions.*;
import lv.emendatus.Destiny_PropMan.repository.interfaces.BookingRepository;
import lv.emendatus.Destiny_PropMan.repository.interfaces.ManagerRepository;
import lv.emendatus.Destiny_PropMan.service.interfaces.AdvancedManagerService;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.util.StringUtils;

import javax.crypto.*;
import javax.mail.MessagingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

@Service
public class JpaAdvancedManagerService implements AdvancedManagerService {

    private final ManagerRepository managerRepository;
    private final BookingRepository bookingRepository;
    private final Logger LOGGER = LogManager.getLogger(JpaPropertyService.class);

    private final JpaManagerService managerService;
    private final JpaBookingService bookingService;
    private final JpaPropertyService propertyService;
    private final JpaNumericalConfigService configService;
    private final JpaClaimService claimService;
    private final JpaTenantService tenantService;
    private final JpaLeasingHistoryService leasingHistoryService;
    private final JpaTenantPaymentService tenantPaymentService;
    private final JpaBillService billService;
    private final JpaPropertyDiscountService discountService;
    private final JpaEarlyTerminationRequestService terminationService;
    private final JpaRefundService refundService;
    private final JpaPayoutService payoutService;
    private final JpaCurrencyService currencyService;
    private final JpaEmailService emailService;
    private final JpaPropertyLockService lockService;
    private final JpaPropertyAmenityService propertyAmenityService;
    private final JpaAmenityService amenityService;
    @Autowired
    private final JpaCardDataSaverService cardDataSaverService;
    @Autowired
    private final JpaNumericDataMappingService numericDataMappingService;
    @Autowired
    private final ManagerMapper managerMapper;
    @Autowired
    private final ManagerPropertyMapper managerPropertyMapper;
    @Autowired
    private final PublicManagerMapper publicManagerMapper;
    @Autowired
    private final PropertyCreationMapper propertyCreationMapper;
    @Autowired
    private final JpaTenantRatingService ratingService;
    @Autowired
    private final JpaThirdPartyPaymentProviderService paymentProviderService;
    @Autowired
    private final JpaTokenService tokenService;
    @Autowired
    private final JpaTokenResetService resetService;
    @Autowired
    private final BookingMapper bookingMapper;
    public JpaAdvancedManagerService(ManagerRepository managerRepository, BookingRepository bookingRepository, JpaManagerService managerService, JpaBookingService bookingService, JpaPropertyService propertyService, JpaNumericalConfigService configService, JpaClaimService claimService, JpaTenantService tenantService, JpaLeasingHistoryService leasingHistoryService, JpaTenantPaymentService tenantPaymentService, JpaBillService billService, JpaPropertyDiscountService discountService, JpaEarlyTerminationRequestService terminationService, JpaRefundService refundService, JpaPayoutService payoutService, JpaCurrencyService currencyService, JpaEmailService emailService, JpaPropertyLockService lockService, JpaPropertyAmenityService propertyAmenityService, JpaAmenityService amenityService, JpaCardDataSaverService cardDataSaverService, JpaNumericDataMappingService numericDataMappingService, ManagerMapper managerMapper, ManagerPropertyMapper managerPropertyMapper, PublicManagerMapper publicManagerMapper, PropertyCreationMapper propertyCreationMapper, JpaTenantRatingService ratingService, JpaThirdPartyPaymentProviderService paymentProviderService, JpaTokenService tokenService, JpaTokenResetService resetService, BookingMapper bookingMapper) {
        this.managerRepository = managerRepository;
        this.bookingRepository = bookingRepository;
        this.managerService = managerService;
        this.bookingService = bookingService;
        this.propertyService = propertyService;
        this.configService = configService;
        this.claimService = claimService;
        this.tenantService = tenantService;
        this.leasingHistoryService = leasingHistoryService;
        this.tenantPaymentService = tenantPaymentService;
        this.billService = billService;
        this.discountService = discountService;
        this.terminationService = terminationService;
        this.refundService = refundService;
        this.payoutService = payoutService;
        this.currencyService = currencyService;
        this.emailService = emailService;
        this.lockService = lockService;
        this.propertyAmenityService = propertyAmenityService;
        this.amenityService = amenityService;
        this.cardDataSaverService = cardDataSaverService;
        this.numericDataMappingService = numericDataMappingService;
        this.managerMapper = managerMapper;
        this.managerPropertyMapper = managerPropertyMapper;
        this.publicManagerMapper = publicManagerMapper;
        this.propertyCreationMapper = propertyCreationMapper;
        this.ratingService = ratingService;
        this.paymentProviderService = paymentProviderService;
        this.tokenService = tokenService;
        this.resetService = resetService;
        this.bookingMapper = bookingMapper;
    }

    @Value("${PROPMAN_PLATFORM_NAME}")
    private String platformName;

    @Value("${PROPMAN_MAIL_USERNAME}")
    private String platformMail;

    @Override
    public PublicManagerProfileDTO getManagerProfile(Long managerId) {
        Optional<Manager> managerOptional = managerRepository.findById(managerId);
        if (managerOptional.isPresent()) {
            Manager manager = managerOptional.get();
            return publicManagerMapper.toDTO(manager);
        } else {
            LOGGER.log(Level.ERROR, "No manager with the {} ID exists in the database.", managerId);
            throw new ManagerNotFoundException("No manager found with ID: " + managerId);
        }
    }

    @Override
    @PreAuthorize("hasAuthority('MANAGER')")
    @Transactional
    public void updateManagerProfile(Long managerId, ManagerProfileDTO updatedProfile, Principal principal) throws Exception {
        String authenticatedUsername = principal.getName();
        Optional<Manager> managerOptional = managerRepository.findById(managerId);
        if (managerOptional.isPresent()) {
            Manager existingManager = managerOptional.get();
            if (authenticatedUsername.equals(existingManager.getLogin())) {
                if (!updatedProfile.getEmail().equals(existingManager.getEmail())) {
                    // Case when the email is updated
                    String preservedEmail = existingManager.getEmail();
                    if (isEmailBusy(updatedProfile.getEmail())) {
                        LOGGER.error("A manager with this e-mail has already been registered");
                        throw new EmailAlreadyExistsException("Manager with this e-mail exists");
                    } else {
                        managerMapper.updateManagerFromDTO(existingManager, updatedProfile);
                        existingManager.setTemporaryEmail(updatedProfile.getEmail());
                        if (!updatedProfile.getPaymentCardNo().equals(paymentProviderService.decryptCardNumber(managerId, UserType.MANAGER, existingManager.getPaymentCardNo()))
                                || !Arrays.equals(updatedProfile.getCvv(), paymentProviderService.decryptCVV(managerId, UserType.MANAGER, existingManager.getCvv()).toCharArray())
                                || !updatedProfile.getCardValidityDate().equals(existingManager.getCardValidityDate())) {
                            cardDataSaverService.removeNDMRecordFromFile(UserType.MANAGER, managerId);
                            numericDataMappingService.flushEmAll();
                            existingManager.setPaymentCardNo(encryptCardNumber(existingManager.getId(), UserType.MANAGER, updatedProfile.getPaymentCardNo()));
                            existingManager.setCvv(encryptCVV(existingManager.getId(), UserType.MANAGER, updatedProfile.getCvv()).toCharArray());
                            existingManager.setCardValidityDate(updatedProfile.getCardValidityDate());
                            List<Long> nDMIdsToBeDeleted = new ArrayList<>();
                            for (NumericDataMapping mapping : numericDataMappingService.getAllMappings()) {
                                nDMIdsToBeDeleted.add(mapping.getId());
                            }
                            for (Long id : nDMIdsToBeDeleted) {
                                numericDataMappingService.deleteNumericDataMappingById(id);
                            }
                        }
                        existingManager.setEmail(preservedEmail);
                        System.out.println("Temporarily set the user's email back to " + preservedEmail);
                        String confirmationToken = tokenService.generateToken();
                        existingManager.setConfirmationToken(confirmationToken);
                        managerRepository.save(existingManager);
                        try {
                            emailService.sendEmail(updatedProfile.getEmail(), "E-mail confirmation link for " + platformName, createConfirmationEmailBody(updatedProfile.getManagerName(), confirmationToken));
                        } catch (MessagingException e) {
                            e.printStackTrace();
                        }
                        TokenResetter resetter = new TokenResetter();
                        resetter.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
                        resetter.setUserId(existingManager.getId());
                        resetter.setUserType(UserType.MANAGER);
                        resetService.addResetter(resetter);
                    }
                } else {
                    // case when the Manager's email is unchanged
                    if (!updatedProfile.getPaymentCardNo().equals(paymentProviderService.decryptCardNumber(managerId, UserType.MANAGER, existingManager.getPaymentCardNo()))
                            || !Arrays.equals(updatedProfile.getCvv(), paymentProviderService.decryptCVV(managerId, UserType.MANAGER, existingManager.getCvv()).toCharArray())
                            || !updatedProfile.getCardValidityDate().equals(existingManager.getCardValidityDate())) {
                        cardDataSaverService.removeNDMRecordFromFile(UserType.MANAGER, managerId);
                        numericDataMappingService.flushEmAll();
                        existingManager.setPaymentCardNo(encryptCardNumber(existingManager.getId(), UserType.MANAGER, updatedProfile.getPaymentCardNo()));
                        existingManager.setCvv(encryptCVV(existingManager.getId(), UserType.MANAGER, updatedProfile.getCvv()).toCharArray());
                        existingManager.setCardValidityDate(updatedProfile.getCardValidityDate());
                        List<Long> nDMIdsToBeDeleted = new ArrayList<>();
                        for (NumericDataMapping mapping : numericDataMappingService.getAllMappings()) {
                            nDMIdsToBeDeleted.add(mapping.getId());
                        }
                        for (Long id : nDMIdsToBeDeleted) {
                            numericDataMappingService.deleteNumericDataMappingById(id);
                        }
                    }
                }
                Arrays.fill(updatedProfile.getCvv(), '\0');
            } else {
                throw new AccessDeniedException("A Manager's profile can only be edited by the Manager himself.");
            }
        } else {
            LOGGER.log(Level.ERROR, "No manager with the {} ID exists in the database.", managerId);
            throw new TenantNotFoundException("No manager found with ID: " + managerId);
        }
    }
        //        String authenticatedUsername = principal.getName();
//        Optional<Manager> managerOptional = managerRepository.findById(managerId);
//        if (managerOptional.isPresent()) {
//            Manager existingManager = managerOptional.get();
//            if (authenticatedUsername.equals(existingManager.getLogin())) {
//                managerMapper.updateManagerFromDTO(existingManager, updatedProfile);
//                managerRepository.save(existingManager);
//            } else {
//                throw new AccessDeniedException("You do not have permission to update this profile.");
//            }
//            managerMapper.updateManagerFromDTO(existingManager, updatedProfile);
//            managerRepository.save(existingManager);
//        } else {
//            LOGGER.log(Level.ERROR, "No manager with the {} ID exists in the database.", managerId);
//            throw new ManagerNotFoundException("No manager found with ID: " + managerId);
//        }
//    }

    @Override
    public List<ManagerPropertyDTO> getManagerPropertyPortfolio(Long managerId) {
        Set<Property> properties = managerService.getManagerProperties(managerId);
        return properties.stream()
                .map(managerPropertyMapper::toDTO)
                .collect(Collectors.toList());
    }

    // USED AS AN AUXILIARY METHOD
    @Override
    @PreAuthorize("hasAuthority('MANAGER')")
    public ManagerReservationDTO viewReservationsForProperty(Long propertyId, Principal principal) {
        Optional<Property> property = propertyService.getPropertyById(propertyId);
        if (property.isPresent()) {
            Set<Long> reservationIds = getReservationIdsForProperty(property.get());
            return new ManagerReservationDTO(propertyId, reservationIds);
        } else {
            LOGGER.error("No property with the {} ID exists in the database.", propertyId);
            throw new PropertyNotFoundException("No property found with ID: " + propertyId);
        }
    }

    // USED AS AN AUXILIARY METHOD
    @Override
    @PreAuthorize("hasAuthority('MANAGER')")
    public ManagerReservationDTO viewReservationsForManager(Long managerId, Principal principal) {
        Optional<Manager> manager = managerService.getManagerById(managerId);
        if (manager.isPresent()) {
            Set<Long> reservationIds = new HashSet<>();
            Set<Property> properties = managerService.getManagerProperties(managerId);
            for (Property property : properties) {
                reservationIds.addAll(getReservationIdsForProperty(property));
            }
            return new ManagerReservationDTO(managerId, reservationIds);
        } else {
            LOGGER.error("No manager with the {} ID exists in the database.", managerId);
            throw new ManagerNotFoundException("No manager found with ID: " + managerId);
        }
    }

    @Override
    @PreAuthorize("hasAuthority('MANAGER')")
    public List<BookingDTO_Reservation> getBookingsForProperty(Long propertyId, Principal principal) {
        Optional<Property> property = propertyService.getPropertyById(propertyId);
        if (property.isPresent()) {
            String managersLogin = property.get().getManager().getLogin();
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (!(authentication instanceof AnonymousAuthenticationToken)) {
                UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                if (!userDetails.getUsername().equals(managersLogin)) {
                    throw new AccessDeniedException("You do not have permission to access this resource.");
                }
            }
            ManagerReservationDTO bunch = viewReservationsForProperty(propertyId, principal);
            List<BookingDTO_Reservation> bookingDTOs = new ArrayList<>();
            for (Long reservationId : bunch.getReservationIds()) {
                Optional<Booking> booking = bookingService.getBookingById(reservationId);
                BookingDTO_Reservation bookingDTO;
                if (booking.isPresent()) {
                    bookingDTO = bookingMapper.toDTO(booking.get());
                    bookingDTOs.add(bookingDTO);
                }
            }
            return bookingDTOs;
        } else throw new PropertyNotFoundException("Unable to find the specified property!");
    }

    @Override
    @PreAuthorize("hasAuthority('MANAGER')")
    public List<BookingDTO_Reservation> getBookingsForManager(Long managerId, Principal principal) {
        String authenticatedUsername = principal.getName();
        Optional<Manager> managerOptional = managerRepository.findById(managerId);
        if (managerOptional.isPresent()) {
            Manager existingManager = managerOptional.get();
            if (authenticatedUsername.equals(existingManager.getLogin())) {
                ManagerReservationDTO bunch = viewReservationsForManager(managerId, principal);
                List<BookingDTO_Reservation> bookingDTOs = new ArrayList<>();
                for (Long reservationId : bunch.getReservationIds()) {
                    Optional<Booking> booking = bookingService.getBookingById(reservationId);
                    BookingDTO_Reservation bookingDTO;
                    if (booking.isPresent()) {
                        bookingDTO = bookingMapper.toDTO(booking.get());
                        bookingDTOs.add(bookingDTO);
                    }
                }
                return bookingDTOs;
            } else {
                throw new AccessDeniedException("You do not have permission to view other Managers' Booking lists.");
            }
        }
        return null;
    }

    @Override
    @PreAuthorize("hasAuthority('MANAGER')")
    @Transactional
    public void submitClaimfromManager(Long bookingId, String description, Principal principal) {
        String authenticatedUsername = principal.getName();
        Optional<Booking> booking = bookingService.getBookingById(bookingId);
        if (booking.isEmpty()) {
            LOGGER.log(Level.WARN, "No booking with the {} ID exists in the database.", bookingId);
            System.out.println("Booking not found");
            throw new BookingNotFoundException("No booking found with ID: " + bookingId);
        } else {
            if (authenticatedUsername.equals(booking.get().getProperty().getManager().getLogin())) {
                int delaySetOrDefault = 7;
                for (NumericalConfig config : configService.getSystemSettings()) {
                    if (config.getName().equals("ClaimPeriodDays")) delaySetOrDefault = config.getValue().intValue();
                }
                if (bookingService.calculateDaysDifference(booking.get().getEndDate()) > delaySetOrDefault) {
                    LOGGER.log(Level.WARN, "Sorry, the claiming period for booking {} has expired.", bookingId);
                    System.out.println("Cannot create claim, claiming period expired for this booking");
                } else {
                        Claim claim = new Claim();
                        claim.setBookingId(bookingId);
                        claim.setClaimStatus(ClaimStatus.OPEN);
                        LocalDateTime localDateTime = LocalDateTime.now();
                        Instant instant = localDateTime.atZone(ZoneOffset.UTC).toInstant();
                        Timestamp timestamp = Timestamp.from(instant);
                        claim.setCreatedAt(timestamp);
                        claim.setAdmitted(false);
                        claim.setClaimantType(ClaimantType.MANAGER);
                        claim.setDescription(description);
                        claimService.addClaim(claim);
                        LOGGER.log(Level.INFO, "Your claim in regard to booking {} has been filed.", bookingId);
                        System.out.println("Claim lodged successfully");
                }
            } else {
                throw new AccessDeniedException("Cannot submit claims in respect of Bookings managed by other Managers.");
            }
        }
    }

    // Auxiliary method
    private Set<Long> getReservationIdsForProperty(Property property) {
        Set<Booking> bookings = bookingService.getBookingsByProperty(property);
        Set<Long> reservationIds = new HashSet<>();
        for (Booking booking : bookings) {
            reservationIds.add(booking.getId());
        }
        return reservationIds;
    }

    @Override
    @PreAuthorize("hasAuthority('MANAGER')")
    @Transactional
    public void closeBookingByManager(Long bookingId) {
        int delaySetOrDefault = 7;
        for (NumericalConfig config : configService.getSystemSettings()) {
            if (config.getName().equals("ClaimPeriodDays")) delaySetOrDefault = config.getValue().intValue();
        }
        Optional<Booking> optionalBooking = bookingService.getBookingById(bookingId);
        if (optionalBooking.isPresent()) {
            Booking booking = optionalBooking.get();
            Long managerId = booking.getProperty().getManager().getId();
            if (booking.getStatus().equals(BookingStatus.CONFIRMED)
                    || booking.getStatus().equals(BookingStatus.CURRENT)
                    || booking.getStatus().equals(BookingStatus.PENDING_PAYMENT)) {
                System.out.println("Cannot close an already paid booking, a current booking or a booking with pending payment period!");
            } else if (!claimService.getClaimsByBooking(bookingId).isEmpty()) {
                System.out.println("Cannot close a booking while there is a claim pending!");
            } else if (booking.getStatus().equals(BookingStatus.OVER)
                    && bookingService.calculateDaysDifference(booking.getEndDate()) < delaySetOrDefault) {
                System.out.println("Cannot close a booking before the expiration of the claim period of " + delaySetOrDefault + " days!");
            } else {
                double managerPayment = tenantPaymentService.getPaymentByBooking(bookingId).getManagerPayment();
                Payout payout = new Payout();
                payout.setBookingId(bookingId);
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
                propertyService.removeBookingFromProperty(booking.getProperty().getId(), bookingId);
                bookingService.deleteBooking(bookingId);
            }
        } else {
            LOGGER.log(Level.ERROR, "No booking with the specified ID exists in the database.");
            throw new BookingNotFoundException("No booking found with ID: " + bookingId);
        }
    }

    @Override
    @PreAuthorize("hasAuthority('MANAGER')")
    @Transactional
    public FinancialStatementDTO generateFinancialStatement(LocalDate periodStart, LocalDate periodEnd, Long managerId, Principal principal) {
        String authenticatedUsername = principal.getName();

        if (managerService.getManagerById(managerId).isPresent() && authenticatedUsername.equals(managerService.getManagerById(managerId).get().getLogin())) {
            List<TenantPayment> completedPaymentsWithinPeriod =
                    tenantPaymentService.getPaymentsByDateRange(periodStart.atStartOfDay(), periodEnd.atStartOfDay())
                            .stream().filter(tenantPayment -> tenantPayment.getManagerId().equals(managerId)).toList()
                            .stream().filter(TenantPayment::isFeePaidToManager).toList();
            FinancialStatementDTO statementDTO = new FinancialStatementDTO();

            Map<Long, Double> income = new HashMap<>();
            for (TenantPayment payment : completedPaymentsWithinPeriod) {
                Long propertyId = payment.getAssociatedPropertyId();
                Double paymentAmount = payment.getManagerPayment();
                if (income.containsKey(propertyId)) {
                    Double totalIncome = income.get(propertyId) + paymentAmount;
                    income.put(propertyId, totalIncome);
                    if (currencyService.getCurrencyById(payment.getCurrency().getId()).isPresent()) {
                        List<Currency> currencies = statementDTO.getIncomeCurrencies();
                        currencies.add(currencyService.getCurrencyById(payment.getCurrency().getId()).get());
                        statementDTO.setIncomeCurrencies(currencies);
                    }
                } else {
                    if (currencyService.getCurrencyById(payment.getCurrency().getId()).isPresent()) {
                        List<Currency> currencies = statementDTO.getIncomeCurrencies();
                        currencies.add(currencyService.getCurrencyById(payment.getCurrency().getId()).get());
                        statementDTO.setIncomeCurrencies(currencies);
                    }
                    income.put(propertyId, paymentAmount);
                }
            }
            statementDTO.setIncome(income);

            List<Property> managerProperties = propertyService.getPropertiesByManager(managerId);
            List<Bill> managersBills = new ArrayList<>();
            for (Property property : managerProperties) {
                if (property.getManager().getId().equals(managerId)) {
                    managersBills.addAll(billService.getBillsByProperty(property));
                }
            }
            List<Bill> billsWithinPeriod = managersBills.stream()
                    .filter(bill -> !bill.getDueDate().toLocalDateTime().toLocalDate().isBefore(periodStart)
                            && !bill.getDueDate().toLocalDateTime().toLocalDate().isAfter(periodEnd)).toList();

            List<Map<String, Double>> expenses = new ArrayList<>();
            for (Property property : managerProperties) {
                Map<String, Double> record = new HashMap<>();
                for (Bill bill : billsWithinPeriod) {
                    String category = "Property " + property.getId() + "(" + bill.getCurrency().getDesignation() + "): " + bill.getExpenseCategory();
                    Double paymentAmount = bill.getAmount();
                    if (record.containsKey(category)) {
                        Double totalAmount = record.get(category) + paymentAmount;
                        record.put(category, totalAmount);
                    } else {
                        record.put(category, paymentAmount);
                    }
                }
                if (!record.isEmpty()) expenses.add(record);
            }
            statementDTO.setExpenses(expenses);

            return statementDTO;
        } else {
            throw new AccessDeniedException("You do not have permission to generate financial reports on other Managers.");
        }
    }

    @Override
    @PreAuthorize("hasAuthority('MANAGER')")
    public List<Bill> getUnpaidBillsForProperty(Long propertyId, Principal principal) {
        String authenticatedUsername = principal.getName();
        Optional<Property> property = propertyService.getPropertyById(propertyId);
        if (property.isPresent()) {
            if (authenticatedUsername.equals(property.get().getManager().getLogin())) {
                return billService.getBillsByProperty(property.get())
                        .stream()
                        .filter(bill -> !bill.isPaid())
                        .collect(Collectors.toList());
            } else {
                throw new AccessDeniedException("You can only view Bills for your own Properties.");
            }
        } else {
            throw new PropertyNotFoundException("Could not find the specified property!");
        }
    }

    @Override
    @PreAuthorize("hasAuthority('MANAGER')")
    public List<Bill> getUnpaidBillsForManager(Long managerId, Principal principal) {
        List<Property> managerProperties = propertyService.getPropertiesByManager(managerId);
        List<Bill> unpaidBills = new ArrayList<>();
        String authenticatedUsername = principal.getName();
        if (managerService.getManagerById(managerId).isPresent() && authenticatedUsername.equals(managerService.getManagerById(managerId).get().getLogin())) {
            for (Property property : managerProperties) {
                Optional<Property> optionalProperty = propertyService.getPropertyById(property.getId());
                if (optionalProperty.isPresent()) {
                    List<Bill> propertyBills = billService.getBillsByProperty(optionalProperty.get());
                    for (Bill bill : propertyBills) {
                        if (!bill.isPaid()) {
                            unpaidBills.add(bill);
                        }
                    }
                }
            }
            return unpaidBills;
        } else {
        throw new AccessDeniedException("You do not have permission to view other Managers' Bills.");
        }
    }

    @Override
    @PreAuthorize("hasAuthority('MANAGER')")
    @Transactional
    public void addProperty(PropertyAdditionDTO propertyDTO) {
        Optional<Manager> optionalManager = managerService.getManagerById(propertyDTO.getManagerId());
        if (optionalManager.isPresent()) {
            if (optionalManager.get().isActive()) {
                Property property = PropertyCreationMapper.INSTANCE.toEntity(propertyDTO);
                property.setManager(optionalManager.get());
                property.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
                property.setBills(new HashSet<>());
                property.setBookings(new HashSet<>());
                property.setPhotos(new ArrayList<>());
                propertyService.addProperty(property);
            } else {
                LOGGER.warn("Inactive managers cannot add properties.");
            }
        } else {
            LOGGER.error("No manager with the specified ID exists in the database.");
            throw new ManagerNotFoundException("No manager with the specified ID exists in the database.");
        }
    }

    @Override
    @PreAuthorize("hasAuthority('MANAGER')")
    @Transactional
    public PropertyDiscount setDiscountOrSurcharge(PropertyDiscountDTO propertyDiscountDTO, Principal principal) {
        Optional<Manager> optionalManager = managerService.getManagerById(propertyDiscountDTO.getManagerId());
        if (optionalManager.isPresent()) {
            String authenticatedUsername = principal.getName();
            if (authenticatedUsername.equals(optionalManager.get().getLogin())) {
                Optional<Property> optionalProperty = propertyService.getPropertyById(propertyDiscountDTO.getPropertyId());
                if (optionalProperty.isPresent()) {
                    Property property = optionalProperty.get();
                    if (!propertyDiscountDTO.getManagerId().equals(property.getManager().getId())) {
                        LOGGER.error("Cannot set discounts or surcharges for properties operated by a different manager!");
                        throw new IllegalArgumentException("Cannot set discounts or surcharges for properties operated by a different manager!");
                    }
                    PropertyDiscount propertyDiscount = new PropertyDiscount();
                    propertyDiscount.setProperty(property);
                    propertyDiscount.setPercentage(propertyDiscountDTO.getPercentage());
                    propertyDiscount.setPeriodStart(propertyDiscountDTO.getPeriodStart());
                    propertyDiscount.setPeriodEnd(propertyDiscountDTO.getPeriodEnd());
                    propertyDiscount.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
                    discountService.addPropertyDiscount(propertyDiscount);
                    return propertyDiscount;
                } else {
                    LOGGER.error("No property with the specified ID exists in the database.");
                    throw new PropertyNotFoundException("No property with the specified ID exists in the database.");
                }
            } else {
                throw new AccessDeniedException("You do not have permission to set discounts or surcharges for Properties operated by other Managers.");
            }
        } else {
            LOGGER.error("No manager with the specified ID exists in the database.");
            throw new ManagerNotFoundException("No manager with the specified ID exists in the database.");
        }
    }

    @Override
    @PreAuthorize("hasAuthority('MANAGER')")
    @Transactional
    public void resetDiscountsAndSurcharges(Long propertyId, LocalDate periodStart, LocalDate periodEnd, Principal principal) {
        String authenticatedUsername = principal.getName();
        Optional<Property> property = propertyService.getPropertyById(propertyId);
        if (property.isPresent()) {
            if (authenticatedUsername.equals(property.get().getManager().getLogin())) {
            List<PropertyDiscount> propertyDiscounts = discountService.getDiscountsForPropertyWithinPeriod(propertyId, periodStart, periodEnd);
            for (PropertyDiscount discount : propertyDiscounts) {
                discountService.deletePropertyDiscount(discount.getId());
            }
            LOGGER.log(Level.INFO, "Deleted " + propertyDiscounts.size() + " discounts and surcharges for property with ID: " + propertyId + " within the period from " + periodStart + " to " + periodEnd);
            } else {
                throw new AccessDeniedException("You do not have permission to reset discounts or surcharges for other Managers' Properties.");
            }
        }
        else {
            throw new PropertyNotFoundException("Could not find the specified property!");
        }
    }

    @Override
    @PreAuthorize("hasAuthority('MANAGER')")
    public List<Booking> getBookingsPendingApproval(Long managerId, Principal principal) {
        String authenticatedUsername = principal.getName();
        if (managerService.getManagerById(managerId).isPresent()) {
            if (authenticatedUsername.equals(managerService.getManagerById(managerId).get().getLogin())) {
                Set <Booking> managersBookings = bookingService.getBookingsByManager(managerService.getManagerById(managerId).get());
                return managersBookings.stream().filter(booking -> booking.getStatus().equals(BookingStatus.PENDING_APPROVAL)).toList();
            } else {
                throw new AccessDeniedException("You do not have permission to get Bookings for other Managers' Properties.");
            }
        } else {
            LOGGER.error("No manager with the specified ID exists in the database.");
            throw new ManagerNotFoundException("No manager with the specified ID exists in the database.");
        }
    }

    @Override
    @PreAuthorize("hasAuthority('MANAGER')")
    @Transactional
    public void approveBooking(Long bookingId, Principal principal) {
        String authenticatedUsername = principal.getName();
        if (bookingService.getBookingById(bookingId).isPresent()) {
            if (authenticatedUsername.equals(bookingService.getBookingById(bookingId).get().getProperty().getManager().getLogin())) {
                bookingService.updateBookingStatus(bookingId, BookingStatus.PENDING_PAYMENT);
                if (tenantService.getTenantById(bookingService.getBookingById(bookingId).get().getTenantId()).isPresent()) {
                    Tenant tenant = tenantService.getTenantById(bookingService.getBookingById(bookingId).get().getTenantId()).get();
                    try {
                        emailService.sendEmail(tenant.getEmail(),
                                "Booking at " + platformName + " confirmed!",
                                createConfirmationLetterToTenant(tenant.getFirstName(), tenant.getLastName(), bookingService.getBookingById(bookingId).get()));
                    } catch (MessagingException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                throw new AccessDeniedException("You do not have permission to approve Bookings for other Managers' Properties.");
            }
        } else {
            LOGGER.error("No booking with the specified ID exists in the database.");
            throw new BookingNotFoundException("No booking with the specified ID exists in the database.");
        }
    }

    @Override
    @PreAuthorize("hasAuthority('MANAGER')")
    @Transactional
    public void declineBooking(Long bookingId, Principal principal) {
        String authenticatedUsername = principal.getName();
        if (bookingService.getBookingById(bookingId).isPresent()) {
            if (authenticatedUsername.equals(bookingService.getBookingById(bookingId).get().getProperty().getManager().getLogin())) {
                bookingService.updateBookingStatus(bookingId, BookingStatus.CANCELLED);
                try {
                    if (tenantService.getTenantById(bookingService.getBookingById(bookingId).get().getTenantId()).isPresent()) {
                        String firstName = tenantService.getTenantById(bookingService.getBookingById(bookingId).get().getTenantId()).get().getFirstName();
                        String surName = tenantService.getTenantById(bookingService.getBookingById(bookingId).get().getTenantId()).get().getLastName();
                        String tenantEmail = tenantService.getTenantById(bookingService.getBookingById(bookingId).get().getTenantId()).get().getEmail();
                        emailService.sendEmail(tenantEmail,
                                "Booking request declined",
                                createRefusalLetterToTenant(firstName, surName, bookingService.getBookingById(bookingId).get()));
                    }
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            } else {
                throw new AccessDeniedException("You do not have permission to decline Bookings for other Managers' Properties.");
            }

        } else {
            LOGGER.error("No booking with the specified ID exists in the database.");
            throw new BookingNotFoundException("No booking with the specified ID exists in the database.");
        }
    }

    @Override
    @PreAuthorize("hasAuthority('MANAGER')")
    @Transactional
    public void acceptEarlyTermination(Long requestId, String reply, Principal principal) {
        String authenticatedUsername = principal.getName();
        Optional<EarlyTerminationRequest> requestOptional = terminationService.getETRequestById(requestId);
        if (requestOptional.isPresent()) {
            EarlyTerminationRequest request = requestOptional.get();
            request.setStatus(ETRequestStatus.APPROVED);
            request.setManagersResponse(reply);
            Optional<Booking> bookingOptional = bookingService.getBookingById(request.getBookingId());
            if (bookingOptional.isPresent()) {
                Booking booking = bookingOptional.get();
                if (booking.getProperty().getManager().getLogin().equals(authenticatedUsername)) {
                    booking.setEndDate(Timestamp.valueOf(request.getTerminationDate()));
                    bookingService.addBooking(booking);
                    TenantPayment payment = tenantPaymentService.getPaymentByBooking(booking.getId());
                    Double newAmount = bookingService.calculateTotalPrice(booking.getId());
                    double refundForTenant = payment.getAmount() - newAmount;
                    int penaltyPercent = 0;
                    for (NumericalConfig config : configService.getSystemSettings()) {
                        if (config.getName().equals("EarlyTerminationPenalty"))
                            penaltyPercent = config.getValue().intValue();
                    }
                    refundForTenant = refundForTenant - ((refundForTenant / 100) * penaltyPercent);
                    payment.setAmount(newAmount);
                    int interestChargedByTheSystemSetOrDefault = 10;
                    for (NumericalConfig config : configService.getSystemSettings()) {
                        if (config.getName().equals("SystemInterestRate"))
                            interestChargedByTheSystemSetOrDefault = config.getValue().intValue();
                    }
                    payment.setManagerPayment(newAmount - (newAmount / 100 * interestChargedByTheSystemSetOrDefault));
                    tenantPaymentService.addTenantPayment(payment);
                    Refund refund = new Refund();
                    refund.setAmount(refundForTenant);
                    refund.setBookingId(booking.getId());
                    refund.setTenantId(booking.getTenantId());
                    refund.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
                    refund.setCurrency(tenantPaymentService.getPaymentByBooking(booking.getId()).getCurrency());
                    refundService.addRefund(refund);
                    request.setProcessedOn(LocalDate.now());
                    if (tenantService.getTenantById(booking.getTenantId()).isPresent()) {
                        Tenant tenant = tenantService.getTenantById(booking.getTenantId()).get();
                        try {
                            emailService.sendEmail(tenant.getEmail(),
                                    "Early termination request granted!",
                                    createETAcceptanceLetterToTenant(tenant.getFirstName(), tenant.getLastName(), refund, request));
                        } catch (MessagingException e) {
                            e.printStackTrace();
                        }
                    }
                    terminationService.addETRequest(request);
                } else {
                    throw new AccessDeniedException("You do not have permission to accept EarlyTerminationRequests for other Managers' Bookings.");
                }
            } else {
                LOGGER.error("Could not retrieve the appropriate booking for request with ID {}.", requestId);
                throw new BookingNotFoundException("Associated booking not found");
            }
        } else {
            LOGGER.error("Early termination request with ID {} not found.", requestId);
            throw new EarlyTerminationRequestNotFound("Early termination request not found");
        }
    }

    @Override
    @PreAuthorize("hasAuthority('MANAGER')")
    @Transactional
    public void declineEarlyTermination(Long requestId, String reply, Principal principal) {
        String authenticatedUsername = principal.getName();
        Optional<EarlyTerminationRequest> requestOptional = terminationService.getETRequestById(requestId);
        if (requestOptional.isPresent()) {
            EarlyTerminationRequest request = requestOptional.get();
            if (bookingService.getBookingById(request.getBookingId()).isPresent()) {
                Manager manager = bookingService.getBookingById(request.getBookingId()).get().getProperty().getManager();
                if (authenticatedUsername.equals(manager.getLogin())) {
                    request.setStatus(ETRequestStatus.DECLINED);
                    request.setManagersResponse(reply);
                    request.setProcessedOn(LocalDate.now());
                    terminationService.addETRequest(request);
                    if (tenantService.getTenantById(request.getTenantId()).isPresent()) {
                        Tenant tenant = tenantService.getTenantById(request.getTenantId()).get();
                        try {
                            emailService.sendEmail(tenant.getEmail(),
                                    "Early termination request denied",
                                    createETDenialLetterToTenant(tenant.getFirstName(), tenant.getLastName(), request));
                        } catch (MessagingException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    throw new AccessDeniedException("You do not have permission to deny EarlyTerminationRequests for other Managers' Bookings.");
                }
            }
        } else {
            LOGGER.error("Early termination request with ID {} not found.", requestId);
            throw new EarlyTerminationRequestNotFound("Early termination request not found");
        }
    }

    @Override
    @PreAuthorize("hasAuthority('MANAGER')")
    @Transactional
    public void removeProperty(Long propertyId, Principal principal) {
        String authenticatedUsername = principal.getName();
        boolean canRemove = true;
        Optional<Property> propertyOptional = propertyService.getPropertyById(propertyId);
        if (propertyOptional.isPresent()) {
            Property property = propertyOptional.get();
            if (authenticatedUsername.equals(property.getManager().getLogin())) {
                Long managerId = property.getManager().getId();
                Set<Booking> bookings = bookingService.getBookingsByProperty(property);
                for (Booking booking : bookings) {
                    if (booking.getStatus().equals(BookingStatus.CONFIRMED) ||
                            booking.getStatus().equals(BookingStatus.PENDING_PAYMENT) ||
                            booking.getStatus().equals(BookingStatus.CURRENT)) {
                        LOGGER.warn("Cannot remove property with active bookings.");
                        canRemove = false;
                        return;
                    }
                }
                Optional<Manager> optionalManager = managerService.getManagerById(managerId);
                if (optionalManager.isPresent()) {
                    if (!optionalManager.get().isActive()) {
                        LOGGER.warn("Blocked managers cannot remove properties.");
                        canRemove = false;
                    }
                }
                if (canRemove) {
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
                        if (booking.getStatus().equals(BookingStatus.PENDING_APPROVAL))
                            booking.setStatus(BookingStatus.CANCELLED);
                    }
                    List<PropertyDiscount> propertyDiscounts = discountService.getDiscountsForProperty(propertyId);
                    List<Long> discountIDsForDeletion = new ArrayList<>();
                    for (PropertyDiscount discount : propertyDiscounts) {
                        discountIDsForDeletion.add(discount.getId());
                    }
                    for (Long index : discountIDsForDeletion) {
                        discountService.deletePropertyDiscount(index);
                    }
                    Set<PropertyAmenity> propertyAmenities = propertyAmenityService.getPropertyAmenitiesByProperty(propertyId);
                    List<Long> amenityIDsForDeletion = new ArrayList<>();
                    for (PropertyAmenity amenity : propertyAmenities) {
                        amenityIDsForDeletion.add(amenity.getId());
                    }
                    for (Long index: amenityIDsForDeletion) {
                        propertyAmenityService.deletePropertyAmenity(index);
                    }
                    managerService.removePropertyFromManager(property.getManager().getId(), propertyId);
                    propertyService.deleteProperty(propertyId);
                    LOGGER.info("Property with ID {} has been removed.", propertyId);
                }
            } else {
                throw new AccessDeniedException("You do not have permission to remove other Managers' Properties.");
            }
        } else {
            LOGGER.error("Property with ID {} does not exist.", propertyId);
            throw new PropertyNotFoundException("No property found with ID: " + propertyId);
        }
    }

    @Override
    @PreAuthorize("hasAuthority('MANAGER')")
    @Transactional
    public void uploadPhotos(Long propertyId, MultipartFile[] files, Principal principal) throws FileStorageException {
        Property property = propertyService.getPropertyById(propertyId)
                .orElseThrow(() -> new EntityNotFoundException("Property not found with id: " + propertyId));
        String authenticatedUsername = principal.getName();
        if (authenticatedUsername.equals(property.getManager().getLogin())) {
            for (MultipartFile file : files) {
                String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
                String uploadDir = Paths.get("photos", propertyId.toString()).toString(); // Change this line
                String filePath = Paths.get(uploadDir, fileName).toString();
                try {
                    Path path = Paths.get(uploadDir);
                    if (!Files.exists(path)) {
                        Files.createDirectories(path);
                    }
                    Files.copy(file.getInputStream(), Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);
                    List<String> updated = property.getPhotos();
                    updated.add(filePath);
                    property.setPhotos(updated);
                } catch (IOException e) {
                    throw new FileStorageException("Could not store file " + fileName + ". Please try again!", e);
                }
            }
            propertyService.addProperty(property);
        } else {
            throw new AccessDeniedException("You do not have permission to upload photos for other Managers' Properties.");
        }
    }

//    @Override
//    @PreAuthorize("hasAuthority('MANAGER')")
//    @Transactional
//    public void removePhoto(Long propertyId, String photoUrl, Principal principal) throws EntityNotFoundException, FileStorageException {
//        String authenticatedUsername = principal.getName();
//        Property property = propertyService.getPropertyById(propertyId)
//                .orElseThrow(() -> new EntityNotFoundException("Property not found with id: " + propertyId));
//        if (authenticatedUsername.equals(property.getManager().getLogin())) {
//            List<String> photos = property.getPhotos();
//            if (photos.contains(photoUrl)) {
//                try {
//                    Files.deleteIfExists(Paths.get(photoUrl));
//                    photos.remove(photoUrl);
//                    property.setPhotos(photos);
//                    propertyService.addProperty(property);
//                } catch (IOException e) {
//                    throw new FileStorageException("Failed to delete photo: " + photoUrl, e);
//                }
//            } else {
//                throw new EntityNotFoundException("Photo not found for property with id: " + propertyId);
//            }
//        } else {
//            throw new AccessDeniedException("You do not have permission to upload photos for other Managers' Properties.");
//        }
//    }

    @Override
    @PreAuthorize("hasAuthority('MANAGER')")
    @Transactional
    public void removePhoto(Long propertyId, String photoUrl, Principal principal) throws EntityNotFoundException, FileStorageException {
        String authenticatedUsername = principal.getName();
        Property property = propertyService.getPropertyById(propertyId)
                .orElseThrow(() -> new EntityNotFoundException("Property not found with id: " + propertyId));

        if (authenticatedUsername.equals(property.getManager().getLogin())) {
            List<String> photos = property.getPhotos();
            String expectedPhotoPath = Paths.get("photos", propertyId.toString(), photoUrl).toString(); // Ensure correct path construction

            if (photos.contains(expectedPhotoPath)) {
                try {
                    Files.deleteIfExists(Paths.get(expectedPhotoPath)); // Delete the photo using the correct path
                    photos.remove(expectedPhotoPath);
                    property.setPhotos(photos);
                    propertyService.addProperty(property);
                } catch (IOException e) {
                    throw new FileStorageException("Failed to delete photo: " + expectedPhotoPath, e);
                }
            } else {
                throw new EntityNotFoundException("Photo not found for property with id: " + propertyId);
            }
        } else {
            throw new AccessDeniedException("You do not have permission to remove photos for other Managers' Properties.");
        }
    }


    @Override
    @PreAuthorize("hasAuthority('MANAGER')")
    @Transactional
    public void makePropertyUnavailable(Long propertyId, LocalDate periodStart, LocalDate periodEnd, Principal principal) {
        String authenticatedUsername = principal.getName();
        if (propertyService.getPropertyById(propertyId).isPresent() &&
                authenticatedUsername.equals(propertyService.getPropertyById(propertyId).get().getManager().getLogin())) {
            System.out.println("Authentication successful");

            if (!(bookingService.
                    getBookingsByDateRangeWithOverlaps(periodStart,
                            periodEnd)).isEmpty()) {
                System.out.println("Entered the block where bookings for the specified period ARE found");
                List<Booking> existingBookings = bookingService.
                        getBookingsByDateRangeWithOverlaps(periodStart, periodEnd);
                System.out.println("Obtained a list of Bookings overlapping with the period in general");
                List<Booking> specificPropertyBookings = new ArrayList<>();
                for (Booking booking : existingBookings) {
                    if (booking.getProperty().getId().equals(propertyId)) specificPropertyBookings.add(booking);
                }
                int interferingBookingCounter = 0;
                System.out.println("Sieved the list so that only the bookings for the appropriate Property would remain");
                for (Booking booking : specificPropertyBookings) {
                    if (!booking.getStatus().equals(BookingStatus.CANCELLED)) {
                        interferingBookingCounter++;
                        LOGGER.log(Level.WARN, "A non-cancelled Booking has been found");
                    }
                }
                if (interferingBookingCounter < 1) {
                    Booking booking = new Booking();
                    booking.setProperty(propertyService.getPropertyById(propertyId).get());
                    booking.setTenantId(0L);
                    booking.setPaid(false);
                    booking.setStatus(BookingStatus.PROPERTY_LOCKED_BY_MANAGER);
                    booking.setStartDate(Timestamp.valueOf(periodStart.atStartOfDay()));
                    booking.setEndDate(Timestamp.valueOf(periodEnd.atStartOfDay().plusDays(1)));
                    bookingService.addBooking(booking);
                    Set<Booking> propertyBookings = bookingService.getBookingsByProperty(propertyService.getPropertyById(propertyId).get());
                    List<Booking> lockStubs = new ArrayList<>();
                    for (Booking propertyBooking : propertyBookings) {
                        if (propertyBooking.getStatus().equals(BookingStatus.PROPERTY_LOCKED_BY_MANAGER))
                            lockStubs.add(propertyBooking);
                    }
                    Booking latestLockStub = lockStubs.get(0);
                    for (Booking stub : lockStubs) {
                        if (stub.getId() > latestLockStub.getId()) latestLockStub = stub;
                    }
                    PropertyLock propertyLock = new PropertyLock();
                    propertyLock.setBookingStubId(latestLockStub.getId());
                    propertyLock.setPropertyId(propertyId);
                    lockService.addPropertyLock(propertyLock);
                    LOGGER.log(Level.INFO, "Property " + propertyId +
                            " closed off by its manager for a period of " + Timestamp.valueOf(periodStart.atStartOfDay())
                            + " through " + Timestamp.valueOf(periodEnd.atStartOfDay().plusDays(1)));
                    } else {LOGGER.log(Level.WARN, "Cannot lock a Property for an already booked period");}
            } else {
                if (propertyService.getPropertyById(propertyId).isPresent()) {
                    Booking booking = new Booking();
                    booking.setProperty(propertyService.getPropertyById(propertyId).get());
                    booking.setTenantId(0L);
                    booking.setPaid(false);
                    booking.setStatus(BookingStatus.PROPERTY_LOCKED_BY_MANAGER);
                    booking.setStartDate(Timestamp.valueOf(periodStart.atStartOfDay()));
                    booking.setEndDate(Timestamp.valueOf(periodEnd.atStartOfDay().plusDays(1)));
                    bookingService.addBooking(booking);
                    Set<Booking> propertyBookings = bookingService.getBookingsByProperty(propertyService.getPropertyById(propertyId).get());
                    List<Booking> lockStubs = new ArrayList<>();
                    for (Booking propertyBooking : propertyBookings) {
                        if (propertyBooking.getStatus().equals(BookingStatus.PROPERTY_LOCKED_BY_MANAGER))
                            lockStubs.add(propertyBooking);
                    }
                    Booking latestLockStub = lockStubs.get(0);
                    for (Booking stub : lockStubs) {
                        if (stub.getId() > latestLockStub.getId()) latestLockStub = stub;
                    }
                    PropertyLock propertyLock = new PropertyLock();
                    propertyLock.setBookingStubId(latestLockStub.getId());
                    propertyLock.setPropertyId(propertyId);
                    lockService.addPropertyLock(propertyLock);
                    LOGGER.log(Level.INFO, "Property " + propertyId +
                            " closed off by its manager for a period of " + Timestamp.valueOf(periodStart.atStartOfDay())
                            + " through " + Timestamp.valueOf(periodEnd.atStartOfDay().plusDays(1)));
                }
            }
        } else {
            throw new AccessDeniedException("You do not have permission to lock other Managers' Properties.");
        }
    }

    @Override
    @PreAuthorize("hasAuthority('MANAGER')")
    @Transactional
    public void unlockProperty(Long propertyId, Principal principal) {
        Optional<Property> optionalProperty = propertyService.getPropertyById(propertyId);
        String authenticatedUsername = principal.getName();
        if (!optionalProperty.isPresent()) {
            LOGGER.log(Level.ERROR, "No property with the {} ID exists in the database.", propertyId);
            throw new PropertyNotFoundException("No property found with ID: " + propertyId);
        }
        Property property = optionalProperty.get();
        if (authenticatedUsername.equals(property.getManager().getLogin())) {
            List<PropertyLock> propertyLocks = lockService.getPropertyLocksByPropertyId(propertyId);
            List<Long> bookingStubsToBeRemoved = propertyLocks.stream()
                    .map(PropertyLock::getBookingStubId)
                    .toList();
            int lockCount = propertyLocks.size();
            for (Long bookingId : bookingStubsToBeRemoved) {
                bookingService.deleteBooking(bookingId);
            }

            LOGGER.log(Level.INFO, "Property unlocked: " + lockCount + " locks removed");
        } else {
            throw new AccessDeniedException("You do not have permission to unlock other Managers' Properties.");
        }
    }

    @Override
    @PreAuthorize("hasAuthority('MANAGER')")
    @Transactional
    public void addBillToProperty(BillAdditionDTO dto, Long propertyId, Principal principal) {
        Optional<Property> optionalProperty = propertyService.getPropertyById(propertyId);
        if (optionalProperty.isPresent()) {
            Property property = optionalProperty.get();
            String authenticatedUsername = principal.getName();
            if (authenticatedUsername.equals(property.getManager().getLogin())) {
                Bill bill = new Bill();
                bill.setProperty(property);
                bill.setPaid(false);
                bill.setDueDate(dto.getDueDate());
                bill.setCurrency(dto.getCurrency());
                bill.setRecipient(dto.getRecipient());
                bill.setExpenseCategory(dto.getExpenseCategory());
                bill.setIssuedAt(dto.getIssuedAt());
                bill.setRecipientIBAN(dto.getRecipientIBAN());
                bill.setAddedAt(Timestamp.valueOf(LocalDateTime.now()));
                billService.addBill(bill);
                Bill retrievedBill = billService.getLatestBill(propertyId);
                propertyService.addBillToProperty(propertyId, retrievedBill.getId());
            } else {
                throw new AccessDeniedException("You do not have permission to add Bills to other Managers' Properties.");
            }
        } else {
            LOGGER.log(Level.WARN, "Property with ID {} not found in the database", propertyId);
            System.out.println("Property with ID " + propertyId + " not found in the database");
        }
    }

    @Override
    @PreAuthorize("hasAuthority('MANAGER')")
    @Transactional
    public void deleteBillFromProperty(Long billId, Long propertyId, Principal principal) {
        Optional<Property> optionalProperty = propertyService.getPropertyById(propertyId);
        String authenticatedUsername = principal.getName();
        if (optionalProperty.isPresent()) {
            if (authenticatedUsername.equals(optionalProperty.get().getManager().getLogin())) {
                Optional<Bill> optionalBill = billService.getBillById(billId);
                if (optionalBill.isPresent()) {
                    Property property = optionalProperty.get();
                    Long managerId = property.getManager().getId();
                    Bill bill = optionalBill.get();
                    propertyService.removeBillFromProperty(propertyId, billId);
                    billService.deleteBill(billId);
                } else {
                    LOGGER.log(Level.WARN, "Bill with ID {} not found in the database", billId);
                    System.out.println("Bill with ID " + billId + " not found in the database");
                }
            } else {
                throw new AccessDeniedException("You do not have permission to remove Bills from other Managers' Properties.");
            }
        } else {
            LOGGER.log(Level.WARN, "Property with ID {} not found in the database", propertyId);
            System.out.println("Property with ID " + propertyId + " not found in the database");
        }
    }

    @Override
    @PreAuthorize("hasAuthority('MANAGER')")
    @Transactional
    public void rateATenant(Long tenantId, Long managerId, Long bookingId, Integer rating, Principal principal) {
        Optional<Booking> optionalBooking = bookingService.getBookingById(bookingId);
        String authenticatedUsername = principal.getName();
        if (optionalBooking.isPresent()) {
            Optional<Tenant> optionalTenant = tenantService.getTenantById(tenantId);
            if (optionalTenant.isPresent()) {
                if (authenticatedUsername.equals(optionalBooking.get().getProperty().getManager().getLogin())) {
                    if (rating >= 1 && rating <= 5) {
                        Booking booking = optionalBooking.get();
                        Tenant tenant = optionalTenant.get();
                        if (booking.getProperty().getManager().getId().equals(managerId)) {
                            if (!ratingService.bookingAlreadyRated(bookingId)) {
                                if (booking.getStatus().equals(BookingStatus.OVER)) {
                                    Tenant ratedTenant = optionalTenant.get();
                                    TenantRating tenantRating = new TenantRating();
                                    tenantRating.setTenantId(ratedTenant.getId());
                                    tenantRating.setBookingId(bookingId);
                                    tenantRating.setRating(rating);
                                    ratingService.addTenantRating(tenantRating);
                                    List<TenantRating> existingRatings = ratingService.getRatingsForTenant(ratedTenant.getId());
                                    if (existingRatings.isEmpty() || existingRatings.size() == 1) {
                                        ratedTenant.setRating(Float.valueOf(rating));
                                    } else {
                                        int totalScore = 0;
                                        for (TenantRating item : existingRatings) {
                                            totalScore += item.getRating();
                                        }
                                        totalScore += rating;
                                        Float updatedRating = (float) (totalScore / existingRatings.size());
                                        ratedTenant.setRating(updatedRating);
                                    }
                                    tenantService.addTenant(ratedTenant);

                                } else {
                                    LOGGER.log(Level.WARN, "Can only rate Bookings that are already over!");
                                    System.out.println("Can only rate Bookings that are already over!");
                                }
                            } else {
                                LOGGER.log(Level.WARN, "This Booking has already been rated!");
                                System.out.println("This Booking has already been rated!");
                            }
                        } else {
                            LOGGER.log(Level.WARN, "A Manager may only rate Tenants who stayed at his/her Property!");
                            System.out.println("A Manager may only rate Tenants who stayed at his/her Property!");
                        }
                    } else {
                        LOGGER.log(Level.WARN, "Invalid rating specified: the rating must be between 1 and 5 inclusive");
                        System.out.println("Invalid rating specified: the rating must be between 1 and 5 inclusive");
                    }
                } else {
                    throw new AccessDeniedException("You do not have permission to rate Tenants that haven't made any current relevant Bookings at your Properties.");
                }
            } else {
                LOGGER.log(Level.ERROR, "No tenant with the {} ID exists in the database.", tenantId);
                throw new TenantNotFoundException("No tenant found with ID: " + tenantId);
            }
        } else {
            LOGGER.log(Level.ERROR, "No booking with the {} ID exists in the database.", bookingId);
            throw new BookingNotFoundException("No booking found with ID: " + bookingId);
        }
    }

    @PreAuthorize("hasAuthority('MANAGER')")
    @Override
    public void updateProperty(Long propertyId, PropertyUpdateDTO propertyDTO, Principal principal) {
        String authenticatedUsername = principal.getName();
        Optional<Property> optionalProperty = propertyService.getPropertyById(propertyId);
        if (optionalProperty.isPresent()) {
            Property property = optionalProperty.get();
            if (authenticatedUsername.equals(property.getManager().getLogin())) {
                if (!propertyDTO.getDescription().equals(property.getDescription())) property.setDescription(propertyDTO.getDescription());
                if (!propertyDTO.getType().equals(property.getType())) property.setType(propertyDTO.getType());
                if (!propertyDTO.getStatus().equals(property.getStatus())) property.setStatus(propertyDTO.getStatus());
                if (!propertyDTO.getSizeM2().equals(property.getSizeM2())) property.setSizeM2(propertyDTO.getSizeM2());
                if (!propertyDTO.getAddress().equals(property.getAddress())) property.setAddress(propertyDTO.getAddress());
                if (!propertyDTO.getSettlement().equals(property.getSettlement())) property.setSettlement(propertyDTO.getSettlement());
                if (!propertyDTO.getCountry().equals(property.getCountry())) property.setCountry(propertyDTO.getCountry());
                if (!propertyDTO.getPricePerDay().equals(property.getPricePerDay())) property.setPricePerDay(propertyDTO.getPricePerDay());
                if (!propertyDTO.getPricePerWeek().equals(property.getPricePerWeek())) property.setPricePerWeek(propertyDTO.getPricePerWeek());
                if (!propertyDTO.getPricePerMonth().equals(property.getPricePerMonth())) property.setPricePerMonth(propertyDTO.getPricePerMonth());
                propertyService.addProperty(property);
            } else {
                throw new AccessDeniedException("You do not have permission to change the details of a Property operated by someone else.");
            }
        } else {
            LOGGER.log(Level.ERROR, "No property with the {} ID exists in the database.", propertyId);
            throw new PropertyNotFoundException("No property found with ID: " + propertyId);
        }
    }

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('MANAGER')")
    public void manageAmenities(Long propertyId, List<Long> amenityIDs, Principal principal) {
        String authenticatedUsername = principal.getName();
        Optional<Property> optionalProperty = propertyService.getPropertyById(propertyId);
        if (optionalProperty.isPresent()) {
            Property property = optionalProperty.get();
            if (authenticatedUsername.equals(property.getManager().getLogin())) {
                Set<PropertyAmenity> existingAmenities = propertyAmenityService.getPropertyAmenitiesByProperty(propertyId);
                Set<PropertyAmenity> updatedList = new HashSet<>();
                for (Long amenityId : amenityIDs) {
                    if (amenityService.getAmenityById(amenityId).isPresent()) {
                        PropertyAmenity link = new PropertyAmenity();
                        link.setAmenity_id(amenityId);
                        link.setProperty_id(propertyId);
                        updatedList.add(link);
                    }
                }
                for (PropertyAmenity pa : existingAmenities) {
                    propertyAmenityService.deletePropertyAmenity(pa.getId());
                }
                for (PropertyAmenity propertyAmenity : updatedList) {
                    propertyAmenityService.addPropertyAmenity(propertyAmenity);
                }
            } else {
                throw new AccessDeniedException("You do not have permission to change the Amenities of a Property operated by someone else.");
            }
        } else {
            LOGGER.log(Level.ERROR, "No property with the {} ID exists in the database.", propertyId);
            throw new PropertyNotFoundException("No property found with ID: " + propertyId);
        }
    }

    @Override
    @PreAuthorize("hasAuthority('MANAGER')")
    public TenantDTOForManagers viewBookingTenant(Long bookingId, Principal principal) {
        String authenticatedUsername = principal.getName();
        Optional<Booking> optionalBooking = bookingService.getBookingById(bookingId);
        if (optionalBooking.isPresent()) {
            Booking booking = optionalBooking.get();
            if (booking.getProperty().getManager().getLogin().equals(authenticatedUsername)) {
                Optional<Tenant> optionalTenant = tenantService.getTenantById(booking.getTenantId());
                if (optionalTenant.isPresent()) {
                    Tenant tenant = optionalTenant.get();
                    TenantDTOForManagers dto = new TenantDTOForManagers();
                    dto.setFirstName(tenant.getFirstName());
                    dto.setLastName(tenant.getLastName());
                    dto.setEmail(tenant.getEmail());
                    dto.setPhone(tenant.getPhone());
                    dto.setRating(tenant.getRating());
                    return dto;
                } else {
                 throw new TenantNotFoundException("Could not extract a Tenant from the Booking.");
                }
            } else {
                throw new AccessDeniedException("You can only view the profiles of Tenants with active Bookings for any of your Properties.");
            }
        } else {
            LOGGER.log(Level.WARN, "No booking with the {} ID exists in the database.", bookingId);
            System.out.println("Booking not found");
            throw new BookingNotFoundException("No booking found with ID: " + bookingId);
        }
    }

    // AUXILIARY METHODS
    public String createConfirmationLetterToTenant(String firstName, String lastName, Booking booking) {
        String greeting = "Dear " + firstName + " " + lastName + ",\n\n";
        String info = "We are happy to inform you that your booking for the period of " + booking.getStartDate().toString() + " through " + booking.getEndDate().toString() + " has been confirmed by the property's manager at " + platformName + ".\n\n";
        String info2 = "Please make sure to remit the rental fee payment by " + tenantPaymentService.getPaymentByBooking(booking.getId()).getReceiptDue().toString() + ".";
        String communication = "Meanwhile, you can contact your manager directly at " + booking.getProperty().getManager().getEmail() + ".\n\n";
        String communication2 = "Should an urgent issue arise, you can also call your manager at " + booking.getProperty().getManager().getPhone() + ".\n\n";
        String closing = "We wish you a great trip to " + booking.getProperty().getSettlement() + ", " + booking.getProperty().getCountry() + "!\n\nBest regards,\n" + platformName + " team";
        return greeting + info + info2 + communication + communication2 + closing;
    }

    public String createRefusalLetterToTenant(String firstName, String lastName, Booking booking) {
        String greeting = "Dear " + firstName + " " + lastName + ",\n\n";
        String info = "We are sorry to inform you that your booking with ID " + booking.getId() + " could not be approved by the property's manager.\n\n";
        String closing = "Please accept our sincere apologies. While our platform cannot affect this manager's decision, we would love to invite you to browse other properties in " + booking.getProperty().getSettlement() + ", " + booking.getProperty().getCountry() + " that our website has to offer. \n\nBest regards,\n" + platformName + " team";
        return greeting + info + closing;
    }

    public String createETAcceptanceLetterToTenant(String firstName, String lastName, Refund refund, EarlyTerminationRequest request) {
        String greeting = "Dear " + firstName + " " + lastName + ",\n\n";
        String info = "Your manager has kindly granted your early termination request in respect of the booking with ID " + refund.getBookingId() + ". Your updated booking will end on " + request.getTerminationDate() + ".\n\n";
        int refundPeriodSetOrDefault = 15;
        int delaySetOrDefault = 7;
        for (NumericalConfig config : configService.getSystemSettings()) {
            if (config.getName().equals("RefundPaymentPeriodDays")) refundPeriodSetOrDefault = config.getValue().intValue();
            if (config.getName().equals("ClaimPeriodDays")) delaySetOrDefault = config.getValue().intValue();
        }
        String info2 = "Your payment has been adjusted accordingly, and a refund of " + refund.getCurrency().getDesignation() + " " + refund.getAmount() + " is estimated to be remitted to you by " + LocalDateTime.now().plusDays(delaySetOrDefault).plusDays(refundPeriodSetOrDefault) + ".";
        String closing = "We are looking forward to seeing you again! \n\nBest regards,\n" + platformName + " team";
        return greeting + info + closing;
    }

    public String createETDenialLetterToTenant(String firstName, String lastName, EarlyTerminationRequest request) {
        String greeting = "Dear " + firstName + " " + lastName + ",\n\n";
        String info = "We are sorry to inform you that your early termination request for booking with ID " + request.getBookingId() + " has been declined by the property's manager.\n\n";
        String info2 = "Our current policy does entitle our managers to abstain from granting early termination requests if there is a valid reason for that. Here is the comment submitted by the manager: ";
        String managersComment = request.getManagersResponse() + "\n\n";
        String closing = "Please accept our sincere apologies. If you think your request was denied wrongfully, please contact us to appeal against this manager's decision. \n\nBest regards,\n" + platformName + " team";
        return greeting + info + info2 + managersComment + closing;
    }

    public String createConfirmationEmailBody(String managerName, String confirmationLink) {
        String greeting = "Hello " + managerName + ",\n\n";
        String explanation = "We have received your request for a change of your email address. To confirm the change of your email, please click the following link:\n\n";
        String link = "http://localhost:8080/man/confirm-email-change?token=" + confirmationLink + "\n\n";
        String expiration = "The confirmation link is going to expire in five minutes; should this be the case, please initiate the change of your email address once more.";
        String instructions = "If you have any trouble with the link, or if you did not request this registration, please contact our support team at " + platformMail + ".\n\n";
        String closing = "Thank you for choosing our service.\n\nBest regards,\n" + platformName + " team";

        return greeting + explanation + link + expiration + instructions + closing;
    }

    private boolean isEmailBusy(String email) {
        return managerService.getManagerByEmail(email) != null;
    }

    public String encryptCVV(Long userId, UserType userType, char[] cvv) throws Exception {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(128);
            SecretKey secretKey = keyGenerator.generateKey();
            byte[] aesKeyBytes = secretKey.getEncoded();
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedCVVBytes = cipher.doFinal(new String(cvv).getBytes());
            numericDataMappingService.saveCVVSecretKey(userId, userType, secretKey);
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
            keyGenerator.init(128);
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
}
