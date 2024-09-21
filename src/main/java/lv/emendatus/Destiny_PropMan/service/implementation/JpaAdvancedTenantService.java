package lv.emendatus.Destiny_PropMan.service.implementation;

import lv.emendatus.Destiny_PropMan.domain.dto.reservation.ETRequestDTO;
import lv.emendatus.Destiny_PropMan.domain.entity.Currency;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.*;
import lv.emendatus.Destiny_PropMan.exceptions.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import lv.emendatus.Destiny_PropMan.domain.dto.profile.BookingHistoryDTO;
import lv.emendatus.Destiny_PropMan.domain.dto.profile.LeasingHistoryDTO_Profile;
import lv.emendatus.Destiny_PropMan.domain.dto.profile.TenantDTO_Profile;
import lv.emendatus.Destiny_PropMan.domain.dto.view.*;
import lv.emendatus.Destiny_PropMan.domain.entity.*;
import lv.emendatus.Destiny_PropMan.mapper.*;
import lv.emendatus.Destiny_PropMan.repository.interfaces.PropertyRepository;
import lv.emendatus.Destiny_PropMan.repository.interfaces.TenantRepository;
import lv.emendatus.Destiny_PropMan.service.interfaces.AdvancedTenantService;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import javax.crypto.*;
import javax.mail.MessagingException;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.sql.Timestamp;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class JpaAdvancedTenantService implements AdvancedTenantService {
    @Autowired
    private PropertyRepository propertyRepository;
    @Autowired
    private PropertyViewMapper propertyMapper;
    @Autowired
    private PaymentsViewMapper paymentsViewMapper;
    @Autowired
    private BookingsViewMapper bookingsViewMapper;
    private final TenantRepository tenantRepository;
    private final TenantMapper tenantMapper;
    private final LeasingHistoryMapper leasingHistoryMapper;
    private final JpaLeasingHistoryService leasingHistoryService;
    private final JpaTenantFavoritesService favoritesService;
    private final JpaClaimService claimService;
    private final JpaBookingService bookingService;
    private final JpaTenantService tenantService;
    private final JpaNumericalConfigService configService;
    private final JpaTenantPaymentService paymentService;
    private final JpaPropertyService propertyService;
    private final JpaEarlyTerminationRequestService terminationService;
    private final JpaThirdPartyPaymentProviderService paymentProviderService;
    private final JpaCurrencyService currencyService;
    private final JpaEmailService emailService;
    private final JpaPropertyRatingService ratingService;
    private final JpaAmenityService amenityService;
    private final JpaPropertyAmenityService propertyAmenityService;
    private final JpaReviewService reviewService;
    private final JpaThirdPartyPaymentProviderService thirdPartyPaymentProviderService;
    private final JpaNumericDataMappingService numericDataMappingService;
    private final JpaTokenService tokenService;
    private final JpaTokenResetService resetService;
    private final JpaCardDataSaverService cardDataSaverService;
    private final Logger LOGGER = LogManager.getLogger(JpaPropertyService.class);
    private static final String COMPLETED_PAYMENTS_FILE_PATH = "completedTenantPayments.txt";
    @Value("${PROPMAN_PLATFORM_NAME}")
    private String platformName;

    @Value("${PROPMAN_MAIL_USERNAME}")
    private String platformMail;

    public JpaAdvancedTenantService(PropertyRepository propertyRepository, PropertyViewMapper propertyMapper, PaymentsViewMapper paymentsViewMapper, TenantRepository tenantRepository, TenantMapper tenantMapper, LeasingHistoryMapper leasingHistoryMapper, JpaLeasingHistoryService leasingHistoryService, JpaTenantFavoritesService favoritesService, JpaClaimService claimService, JpaBookingService bookingService, JpaTenantService tenantService, JpaNumericalConfigService configService, JpaTenantPaymentService paymentService, JpaPropertyService propertyService, JpaEarlyTerminationRequestService terminationService, JpaThirdPartyPaymentProviderService paymentProviderService, JpaCurrencyService currencyService, JpaEmailService emailService, JpaPropertyRatingService ratingService, JpaAmenityService amenityService, JpaPropertyAmenityService propertyAmenityService, JpaReviewService reviewService, JpaThirdPartyPaymentProviderService thirdPartyPaymentProviderService, JpaNumericDataMappingService numericDataMappingService, JpaTokenService tokenService, JpaTokenResetService resetService, JpaCardDataSaverService cardDataSaverService) {
        this.propertyRepository = propertyRepository;
        this.propertyMapper = propertyMapper;
        this.paymentsViewMapper = paymentsViewMapper;
        this.tenantRepository = tenantRepository;
        this.tenantMapper = tenantMapper;
        this.leasingHistoryMapper = leasingHistoryMapper;
        this.leasingHistoryService = leasingHistoryService;
        this.favoritesService = favoritesService;
        this.claimService = claimService;
        this.bookingService = bookingService;
        this.tenantService = tenantService;
        this.configService = configService;
        this.paymentService = paymentService;
        this.propertyService = propertyService;
        this.terminationService = terminationService;
        this.paymentProviderService = paymentProviderService;
        this.currencyService = currencyService;
        this.emailService = emailService;
        this.ratingService = ratingService;
        this.amenityService = amenityService;
        this.propertyAmenityService = propertyAmenityService;
        this.reviewService = reviewService;
        this.thirdPartyPaymentProviderService = thirdPartyPaymentProviderService;
        this.numericDataMappingService = numericDataMappingService;
        this.tokenService = tokenService;
        this.resetService = resetService;
        this.cardDataSaverService = cardDataSaverService;
    }

    @Override
    public PropertiesForTenantsDTO getPropertyDetails(Long propertyId, String tenantLogin) {
        Optional<Property> propertyOptional = propertyRepository.findById(propertyId);
        if (propertyOptional.isPresent()) {
            Property property = propertyOptional.get();
            PropertiesForTenantsDTO dto = propertyMapper.propertyToPropertiesForTenantsDTO(property);
            Currency baseCurrency = currencyService.returnBaseCurrency();
            dto.setCurrency(baseCurrency);
            if (tenantLogin != null) {
                Optional<Tenant> tenantOptional = Optional.of(tenantService.getTenantByLogin(tenantLogin));
                if (tenantOptional.isPresent() && tenantOptional.get().getPreferredCurrency() != null) {
                    Currency preferredCurrency = tenantOptional.get().getPreferredCurrency();
                    if (!preferredCurrency.equals(baseCurrency)) {
                        dto.setPricePerDay(dto.getPricePerDay() * preferredCurrency.getRateToBase());
                        dto.setPricePerMonth(dto.getPricePerMonth() * preferredCurrency.getRateToBase());
                        dto.setPricePerWeek(dto.getPricePerWeek() * preferredCurrency.getRateToBase());
                    }
                    dto.setCurrency(preferredCurrency);
                }
            }
            Set<String> amenities = new HashSet<>();
            for (PropertyAmenity propertyAmenity : propertyAmenityService.getPropertyAmenitiesByProperty(propertyId)) {
                if (amenityService.getAmenityById(propertyAmenity.getAmenity_id()).isPresent()) {
                    amenities.add(amenityService.getAmenityById(propertyAmenity.getAmenity_id()).get().getDescription());
                }
            }
            dto.setAmenities(amenities);
            List<ReviewDTO> reviews = new ArrayList<>(reviewService.getPropertyReviews(propertyId));
            dto.setReviews(reviews);
            return dto;

        } else {
            LOGGER.log(Level.ERROR, "No property with the {} ID exists in the database.", propertyId);
            throw new PropertyNotFoundException("No property found with ID: " + propertyId);
        }
    }

    @Override
    @PreAuthorize("hasAuthority('TENANT')")
    public TenantDTO_Profile getTenantInformation(Long tenantId, Principal principal) throws Exception {
        String authenticatedUsername = principal.getName();
        Optional<Tenant> tenantOptional = tenantRepository.findById(tenantId);
        if (tenantOptional.isPresent()) {
            Tenant tenant = tenantOptional.get();
            if (authenticatedUsername.equals(tenant.getLogin())) {
                TenantDTO_Profile profile = TenantMapper.INSTANCE.toDTO(tenant);
                profile.setPaymentCardNo(thirdPartyPaymentProviderService.decryptCardNumber(tenantId, UserType.TENANT, tenant.getPaymentCardNo()));
                profile.setCvv(thirdPartyPaymentProviderService.decryptCVV(tenantId, UserType.TENANT, tenant.getCvv()).toCharArray());
                return profile;
            } else {
                throw new AccessDeniedException("A Tenant's profile can only be viewed by the Tenant himself.");
            }
        } else {
            LOGGER.log(Level.ERROR, "No tenant with the {} ID exists in the database.", tenantId);
            throw new TenantNotFoundException("No tenant found with ID: " + tenantId);
        }
    }

    @Override
    @PreAuthorize("hasAuthority('TENANT')")
    @Transactional
    public void updateTenantInformation(Long tenantId, TenantDTO_Profile updatedTenantInfo, Principal principal) throws Exception {
        String authenticatedUsername = principal.getName();
        Optional<Tenant> tenantOptional = tenantRepository.findById(tenantId);
        if (tenantOptional.isPresent()) {
            Tenant existingTenant = tenantOptional.get();
            if (authenticatedUsername.equals(existingTenant.getLogin())) {
                float preservedRating = existingTenant.getRating();
                if (!updatedTenantInfo.getEmail().equals(existingTenant.getEmail())) {
                    // Case when the email is updated
                    String preservedEmail = existingTenant.getEmail();
                    if (isEmailBusy(updatedTenantInfo.getEmail())) {
                        LOGGER.error("A tenant with this e-mail has already been registered");
                        throw new EmailAlreadyExistsException("Tenant with this e-mail exists");
                    } else {
                        tenantMapper.updateTenantFromDTO(existingTenant, updatedTenantInfo);
                        existingTenant.setTemporaryEmail(updatedTenantInfo.getEmail());
                        if (!updatedTenantInfo.getPaymentCardNo().equals(paymentProviderService.decryptCardNumber(tenantId, UserType.TENANT, existingTenant.getPaymentCardNo()))
                                || !Arrays.equals(updatedTenantInfo.getCvv(), paymentProviderService.decryptCVV(tenantId, UserType.TENANT, existingTenant.getCvv()).toCharArray())
                                || !updatedTenantInfo.getCardValidityDate().equals(existingTenant.getCardValidityDate())) {
                            cardDataSaverService.removeNDMRecordFromFile(UserType.TENANT, tenantId);
                            numericDataMappingService.flushEmAll();
                            existingTenant.setPaymentCardNo(encryptCardNumber(existingTenant.getId(), UserType.TENANT, updatedTenantInfo.getPaymentCardNo()));
                            existingTenant.setCvv(encryptCVV(existingTenant.getId(), UserType.TENANT, updatedTenantInfo.getCvv()).toCharArray());
                            existingTenant.setCardValidityDate(updatedTenantInfo.getCardValidityDate());
                            List<Long> nDMIdsToBeDeleted = new ArrayList<>();
                            for (NumericDataMapping mapping : numericDataMappingService.getAllMappings()) {
                                nDMIdsToBeDeleted.add(mapping.getId());
                            }
                            for (Long id : nDMIdsToBeDeleted) {
                                numericDataMappingService.deleteNumericDataMappingById(id);
                            }
                        }
                        existingTenant.setRating(preservedRating);
                        existingTenant.setEmail(preservedEmail);
                        System.out.println("Temporarily set the user's email back to " + preservedEmail);
                        String confirmationToken = tokenService.generateToken();
                        existingTenant.setConfirmationToken(confirmationToken);
                        tenantRepository.save(existingTenant);
                        try {
                            emailService.sendEmail(updatedTenantInfo.getEmail(), "E-mail confirmation link for " + platformName, createConfirmationEmailBody(updatedTenantInfo.getFirstName(), updatedTenantInfo.getLastName(), confirmationToken));
                        } catch (MessagingException e) {
                            e.printStackTrace();
                        }
                        TokenResetter resetter = new TokenResetter();
                        resetter.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
                        resetter.setUserId(existingTenant.getId());
                        resetter.setUserType(UserType.TENANT);
                        resetService.addResetter(resetter);
                    }
                } else {
                    // case when the Tenant's email is unchanged
                    if (!updatedTenantInfo.getPaymentCardNo().equals(paymentProviderService.decryptCardNumber(tenantId, UserType.TENANT, existingTenant.getPaymentCardNo()))
                            || !Arrays.equals(updatedTenantInfo.getCvv(), paymentProviderService.decryptCVV(tenantId, UserType.TENANT, existingTenant.getCvv()).toCharArray())
                            || !updatedTenantInfo.getCardValidityDate().equals(existingTenant.getCardValidityDate())) {
                        cardDataSaverService.removeNDMRecordFromFile(UserType.TENANT, tenantId);
                        numericDataMappingService.flushEmAll();
                        existingTenant.setPaymentCardNo(encryptCardNumber(existingTenant.getId(), UserType.TENANT, updatedTenantInfo.getPaymentCardNo()));
                        existingTenant.setCvv(encryptCVV(existingTenant.getId(), UserType.TENANT, updatedTenantInfo.getCvv()).toCharArray());
                        existingTenant.setCardValidityDate(updatedTenantInfo.getCardValidityDate());
                        List<Long> nDMIdsToBeDeleted = new ArrayList<>();
                        for (NumericDataMapping mapping : numericDataMappingService.getAllMappings()) {
                            nDMIdsToBeDeleted.add(mapping.getId());
                        }
                        for (Long id : nDMIdsToBeDeleted) {
                            numericDataMappingService.deleteNumericDataMappingById(id);
                        }
                    }
                }
                Arrays.fill(updatedTenantInfo.getCvv(), '\0');
            } else {
                throw new AccessDeniedException("A Tenant's profile can only be edited by the Tenant himself.");
            }
        } else {
            LOGGER.log(Level.ERROR, "No tenant with the {} ID exists in the database.", tenantId);
            throw new TenantNotFoundException("No tenant found with ID: " + tenantId);
        }
    }
    @Override
    @PreAuthorize("hasAuthority('TENANT')")
    public BookingHistoryDTO viewBookingHistory(Long tenantId, Principal principal) {
        Optional<Tenant> tenantOptional = tenantRepository.findById(tenantId);
        String authenticatedUsername = principal.getName();
        if (tenantOptional.isPresent()) {
            Tenant tenant = tenantOptional.get();
            if (authenticatedUsername.equals(tenant.getLogin())) {
                List<LeasingHistory> tenantsHistory = leasingHistoryService.getLeasingHistoryByTenant(tenant);
                List<LeasingHistoryDTO_Profile> leasingHistoryDTOs = new ArrayList<>();
                for (LeasingHistory history : tenantsHistory) {
                    LeasingHistoryDTO_Profile leasingHistoryDTO = leasingHistoryMapper.toDTO(history);
                    if (propertyService.getPropertyById(leasingHistoryDTO.getPropertyId()).isPresent()) {
                        leasingHistoryDTO.setAddress(propertyService.getPropertyById(leasingHistoryDTO.getPropertyId()).get().getAddress());
                        leasingHistoryDTO.setSettlement(propertyService.getPropertyById(leasingHistoryDTO.getPropertyId()).get().getSettlement());
                        leasingHistoryDTO.setCountry(propertyService.getPropertyById(leasingHistoryDTO.getPropertyId()).get().getCountry());
                    }
                    leasingHistoryDTOs.add(leasingHistoryDTO);
                }
                return new BookingHistoryDTO(tenantId, leasingHistoryDTOs);
            } else {
                throw new AccessDeniedException("A Tenant can only view his own booking history.");
            }
        } else {
            LOGGER.log(Level.ERROR, "No tenant with the {} ID exists in the database.", tenantId);
            throw new TenantNotFoundException("No tenant found with ID: " + tenantId);
        }
    }


    @Override
    @PreAuthorize("hasAuthority('TENANT')")
    @Transactional
    public void saveFavoriteProperty(FavoritePropertyDTO favoriteDTO, Principal principal) {
        Tenant tenant = tenantRepository.findById(favoriteDTO.getTenantId()).orElse(null);
        Property property = propertyRepository.findById(favoriteDTO.getPropertyId()).orElse(null);
        String authenticatedUsername = principal.getName();
        if (tenant != null && property != null) {
            if (authenticatedUsername.equals(tenant.getLogin())) {
                Optional<TenantFavorites> current = favoritesService.getTenantFavoritesByTenant(favoriteDTO.getTenantId());
                if (current.isPresent()) {
                    favoritesService.addPropertyToFavorites(favoriteDTO.getTenantId(), favoriteDTO.getPropertyId());
                } else {
                    TenantFavorites tenantFavorites = new TenantFavorites();
                    tenantFavorites.setTenantId(favoriteDTO.getTenantId());
                    List<Long> favoritePropertyIds = new ArrayList<>();
                    favoritePropertyIds.add(favoriteDTO.getPropertyId());
                    tenantFavorites.setFavoritePropertyIDs(favoritePropertyIds);
                    favoritesService.addTenantFavorites(tenantFavorites);
                }
            } else {
                throw new AccessDeniedException("A Tenant can only save favorite Properties for himself.");
            }
        } else {
            LOGGER.log(Level.ERROR, "Either the Tenant or the Property could not be found!");
            throw new EntityNotFoundException("Either the Tenant or the Property could not be found!");
        }
    }

    @Override
    @PreAuthorize("hasAuthority('TENANT')")
    public List<FavoritePropertyDTO_Profile> getFavoriteProperties(Long tenantId, Principal principal) {
        Tenant tenant = tenantRepository.findById(tenantId).orElse(null);
        String authenticatedUsername = principal.getName();
        if (tenant != null) {
            if (authenticatedUsername.equals(tenant.getLogin())) {
                Optional<TenantFavorites> current = favoritesService.getTenantFavoritesByTenant(tenantId);
                if (current.isPresent()) {
                    return current.get().getFavoritePropertyIDs().stream()
                            .map(propertyId -> propertyMapper.toFavoritePropertyDTO_Profile(propertyId, propertyRepository))
                            .collect(Collectors.toList());
                } else {
                    LOGGER.log(Level.INFO, "No favorite properties listed for tenant with the ID {}.", tenantId);
                    return Collections.emptyList();
                }
            } else {
                throw new AccessDeniedException("A Tenant can only retrieve his own favorite Properties.");
            }
        } else {
            LOGGER.log(Level.WARN, "No tenant with the {} ID exists in the database.", tenantId);
            throw new TenantNotFoundException("No tenant found with ID: " + tenantId);
        }
    }
    @Override
    @PreAuthorize("hasAuthority('TENANT')")
    public void removePropertyFromFavorites(Long tenantId, Long propertyId, Principal principal) {
        String authenticatedUsername = principal.getName();
        if (tenantService.getTenantById(tenantId).isPresent()) {
            Tenant tenant = tenantService.getTenantById(tenantId).get();
            if (authenticatedUsername.equals(tenant.getLogin())) {
                favoritesService.removePropertyFromFavorites(tenantId, propertyId);
            } else {
                throw new AccessDeniedException("A Tenant can only retrieve his own favorite Properties.");
            }
        } else {
            LOGGER.log(Level.WARN, "No tenant with the {} ID exists in the database.", tenantId);
            throw new TenantNotFoundException("No tenant found with ID: " + tenantId);
        }
    }

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('TENANT')")
    public void submitClaimFromTenant(Long bookingId, String description, Principal principal) {
        Optional<Booking> bookingOptional = bookingService.getBookingById(bookingId);
        String authenticatedUsername = principal.getName();
        if (bookingOptional.isEmpty()) {
            LOGGER.log(Level.WARN, "No booking with the {} ID exists in the database.", bookingId);
            throw new BookingNotFoundException("No booking found with ID: " + bookingId);
        }
        Booking booking = bookingOptional.get();
        Optional<Tenant> tenantOptional = tenantService.getTenantById(booking.getTenantId());
        if (tenantOptional.isEmpty()) {
            LOGGER.log(Level.ERROR, "No tenant with ID {} exists in the database.", booking.getTenantId());
            throw new TenantNotFoundException("No tenant found with ID: " + booking.getTenantId());
        }
        Tenant tenant = tenantOptional.get();
        if (!authenticatedUsername.equals(tenant.getLogin())) {
            LOGGER.log(Level.WARN, "A Tenant can only submit claims on their own behalf.");
            throw new AccessDeniedException("A Tenant can only submit claims on their own behalf.");
        }
        int delaySetOrDefault = 7;
        for (NumericalConfig config : configService.getSystemSettings()) {
            if (config.getName().equals("ClaimPeriodDays")) {
                delaySetOrDefault = config.getValue().intValue();
            }
        }
        if (bookingService.calculateDaysDifference(booking.getEndDate()) > delaySetOrDefault) {
            LOGGER.log(Level.WARN, "Sorry, the claiming period for booking {} has expired.", bookingId);
            throw new ClaimPeriodExpiredException("Cannot create claim, claiming period expired for this booking");
        }
        Claim claim = new Claim();
        claim.setBookingId(bookingId);
        claim.setClaimStatus(ClaimStatus.OPEN);
        claim.setCreatedAt(Timestamp.from(Instant.now()));
        claim.setAdmitted(false);
        claim.setClaimantType(ClaimantType.TENANT);
        claim.setDescription(description);
        claimService.addClaim(claim);

        LOGGER.log(Level.INFO, "Your claim in regard to booking {} has been filed.", bookingId);
    }

    @Override
    @PreAuthorize("hasAuthority('TENANT')")
    public List<PaymentsViewDTO> viewCompletedPayments(Long tenantId, Principal principal) {
        String authenticatedUsername = principal.getName();
        Tenant tenant = tenantService.getTenantByLogin(authenticatedUsername);
        if (tenant != null) {
            if (tenant.getId().equals(tenantId)) {
                List<TenantPayment> allPaidPaymentsOfTenant = paymentService.getPaymentsByTenant(tenantId)
                        .stream().filter(TenantPayment::isReceivedFromTenant).toList();
                List<Property> paidProperties = new ArrayList<>();
                for (TenantPayment payment : allPaidPaymentsOfTenant) {
                    if (propertyService.getPropertyById(payment.getAssociatedPropertyId()).isPresent()) {
                        paidProperties.add(propertyService.getPropertyById(payment.getAssociatedPropertyId()).get());
                    }
                }
                return paymentsViewMapper.toDTOList(allPaidPaymentsOfTenant, paidProperties);
            } else {
                throw new AccessDeniedException("Users can only see their own payments");
            }
        } else {
            throw new EntityNotFoundException("User not found");
        }
    }

    @Override
    @PreAuthorize("hasAuthority('TENANT')")
    public List<PaymentsViewDTO> viewOutstandingPayments(Long tenantId, Principal principal) {
        String authenticatedUsername = principal.getName();
        Optional<Tenant> optionalTenant = tenantService.getTenantById(tenantId);
        if (optionalTenant.isPresent()) {
            Tenant tenant = optionalTenant.get();
            if (authenticatedUsername.equals(tenant.getLogin())) {
                List<TenantPayment> allOutstandingPaymentsOfTenant = paymentService.getPaymentsByTenant(tenantId)
                        .stream().filter(tenantPayment -> !tenantPayment.isReceivedFromTenant()).toList();
                List<Property> bookedProperties = new ArrayList<>();
                for (TenantPayment payment : allOutstandingPaymentsOfTenant) {
                    if (propertyService.getPropertyById(payment.getAssociatedPropertyId()).isPresent()) {
                        bookedProperties.add(propertyService.getPropertyById(payment.getAssociatedPropertyId()).get());
                    }
                }
                return paymentsViewMapper.toDTOList(allOutstandingPaymentsOfTenant, bookedProperties);
            } else {
                throw new AccessDeniedException("A Tenant can only retrieve his own payments.");
            }
        } else {
            LOGGER.log(Level.WARN, "No tenant with the {} ID exists in the database.", tenantId);
            throw new TenantNotFoundException("No tenant found with ID: " + tenantId);
        }
    }

    @Override
    @PreAuthorize("hasAuthority('TENANT')")
    public List<BookingsViewDTO> viewTenantsBookings(Long tenantId, Principal principal) {
        String authenticatedUsername = principal.getName();
        List<Booking> tenantBookings;
        if (tenantService.getTenantById(tenantId).isPresent()) {
            if (authenticatedUsername.equals(tenantService.getTenantById(tenantId).get().getLogin())) {
                tenantBookings = bookingService.getBookingsByTenant(tenantService.getTenantById(tenantId).get()).stream().toList();
            } else {
                throw new AccessDeniedException("A Tenant can only view his own Bookings.");
            }
        } else {
            LOGGER.log(Level.WARN, "No tenant with the {} ID exists in the database.", tenantId);
            throw new TenantNotFoundException("No tenant found with ID: " + tenantId);
        }
        return bookingsViewMapper.toDTOList(tenantBookings);
    }

    @Override
    @PreAuthorize("hasAuthority('TENANT')")
    @Transactional
    public void requestEarlyTermination(ETRequestDTO dto, Principal principal) {
        String authenticatedUsername = principal.getName();
        LocalDateTime now = LocalDateTime.now();
        LocalDate tomorrow = now.toLocalDate().plusDays(1);
        if (dto.getTerminationDate().toLocalDate().isBefore(tomorrow)) {
            LOGGER.log(Level.ERROR, "Termination date must be no earlier than the next day.");
            throw new BadEarlyTerminationRequestException("Termination must be no earlier than the next day.");
        }
        EarlyTerminationRequest request = new EarlyTerminationRequest();
        if (bookingService.getBookingById(dto.getBookingId()).isPresent()) {
            Booking booking = bookingService.getBookingById(dto.getBookingId()).get();
            Long tenantId = booking.getTenantId();
            Optional<Tenant> tenantOptional = tenantService.getTenantById(tenantId);
            if (tenantOptional.isPresent()) {
                if (authenticatedUsername.equals(tenantOptional.get().getLogin())) {
                    if (dto.getTerminationDate().isEqual(booking.getEndDate().toLocalDateTime())
                            || dto.getTerminationDate().isAfter(booking.getEndDate().toLocalDateTime())) {
                        LOGGER.log(Level.ERROR, "Early termination date must be before the final date of the initial booking.");
                        throw new BadEarlyTerminationRequestException("Early termination date must be before the final date of the initial booking.");
                    }
                    request.setTenantId(tenantId);
                    request.setStatus(ETRequestStatus.PENDING);
                    request.setManagersResponse("");
                    request.setTerminationDate(dto.getTerminationDate());
                    request.setBookingId(dto.getBookingId());
                    request.setComment(dto.getComment());
                    terminationService.addETRequest(request);
                    try {
                        emailService.sendEmail(booking.getProperty().getManager().getEmail(),
                                "Early cancellation request at " + platformName,
                                createEarlyTerminationLetterToManager(booking.getProperty().getManager().getManagerName(), request));
                    } catch (MessagingException e) {
                        e.printStackTrace();
                    }
                } else {
                    throw new AccessDeniedException("A Tenant can only request early termination of his own Bookings.");
                }
            } else {
                LOGGER.log(Level.ERROR, "Error fetching the Tenant from the database.");
                throw new EntityNotFoundException("Error fetching the Tenant from the database.");
            }
        } else {
            LOGGER.log(Level.ERROR, "Could not extract Tenant from the Booking - Booking record possibly corrupt.");
            throw new TenantNotFoundException("Failed in extracting the Tenant from the Booking");
        }
    }

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('TENANT')")
    public void processPayment(Long paymentId, Principal principal) {
        String authenticatedUsername = principal.getName();
        Optional<TenantPayment> paymentOptional = paymentService.getTenantPaymentById(paymentId);
        if (paymentOptional.isPresent()) {
            TenantPayment tenantPayment = paymentOptional.get();
            if (authenticatedUsername.equals(tenantPayment.getTenant().getLogin())) {
                boolean paymentSuccessful = false;
                if (tenantPayment.getTenant().getCardValidityDate().isBefore(YearMonth.now())) {
                    LOGGER.error("Payment processing failed for TenantPayment with ID {} due to the expiration of the tenant's payment card.", tenantPayment.getId());
                } else {
                    // Add third-party payment processing mechanism in the service
                    paymentSuccessful = paymentProviderService.stub(tenantPayment, tenantPayment.getTenant().getPaymentCardNo(), tenantPayment.getTenant().getCvv());
                }
                if (paymentSuccessful) {
                    try {
                        File file = new File(COMPLETED_PAYMENTS_FILE_PATH);
                        if (!file.exists()) {
                            file.createNewFile();
                        }
                        try (FileWriter writer = new FileWriter(file, true)) {
                            writer.write(LocalDateTime.now().toString() + " Payment from Tenant " + tenantPayment.getTenant().getId() + " processed successfully. Amount: " + tenantPayment.getCurrency().getDesignation() + " " + tenantPayment.getAmount() + ", booking ID: " + tenantPayment.getAssociatedBookingId() + "\n");
                        }
                    } catch (IOException e) {
                        LOGGER.error("Error occurred while writing completed payment record: " + e.getMessage());
                    }
                    tenantPayment.setReceivedFromTenant(true);
                    Optional<Booking> bookingOptional = bookingService.getBookingById(tenantPayment.getAssociatedBookingId());
                    if (bookingOptional.isPresent()) {
                        Booking booking = bookingOptional.get();
                        booking.setPaid(true);
                        booking.setStatus(BookingStatus.CONFIRMED);
                        bookingService.addBooking(booking);
                        paymentService.addTenantPayment(tenantPayment);
                        try {
                            if (tenantService.getTenantById(booking.getTenantId()).isPresent()) {
                                Tenant tenant = tenantService.getTenantById(booking.getTenantId()).get();
                                emailService.sendEmail(tenant.getEmail(),
                                        "Your payment has been received!",
                                        createPaymentConfirmationLetterToTenant(tenant.getFirstName(), tenant.getLastName(), booking));
                            }
                        } catch (MessagingException e) {
                            LOGGER.error("Error sending email to tenant: " + e.getMessage(), e);
                            System.out.println("Hit an error while sending the mail to Tenant");
                            e.printStackTrace();
                        }
                        try {
                            emailService.sendEmail(booking.getProperty().getManager().getEmail(),
                                    "We have received the rental fee for your property!",
                                    createPaymentConfirmationLetterToManager(booking.getProperty().getManager().getManagerName(), booking));
                        } catch (MessagingException e) {
                            LOGGER.error("Error sending email to manager: " + e.getMessage(), e);
                            System.out.println("Hit an error while sending the mail to Manager");
                            e.printStackTrace();
                        }
                    } else {
                        LOGGER.error("Booking with ID {} not found.", tenantPayment.getAssociatedBookingId());
                        throw new BookingNotFoundException("Booking not found");
                    }
                    LOGGER.info("Payment processed successfully. TenantPayment marked as received, and Booking {} status set to CONFIRMED.", tenantPayment.getAssociatedBookingId());
                } else {
                    LOGGER.error("Payment processing failed for TenantPayment with ID {}.", tenantPayment.getId());
                    // Handle payment failure as in line with the third-party payment service provider's data output
                }
            } else {
                throw new AccessDeniedException("A Tenant can only request process his own payments.");
            }
        } else {
            LOGGER.error("Payment with ID {} not found.", paymentId);
            throw new EntityNotFoundException("Payment not found");
        }
    }

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('TENANT')")
    public void rateAProperty(Long tenantId, Long bookingId, Integer rating, Principal principal) {
        String authenticatedUsername = principal.getName();
        Optional<Booking> optionalBooking = bookingService.getBookingById(bookingId);
        if (optionalBooking.isPresent()) {
            Optional<Tenant> optionalTenant = tenantService.getTenantById(tenantId);
            if (optionalTenant.isPresent()) {
                Tenant tenant = optionalTenant.get();
                if (authenticatedUsername.equals(tenant.getLogin())) {
                    if (rating >= 1 && rating <= 5) {
                        Booking booking = optionalBooking.get();
                        if (booking.getTenantId().equals(tenant.getId())) {
                            if (!ratingService.bookingAlreadyRated(bookingId)) {
                                if (booking.getStatus().equals(BookingStatus.OVER)) {
                                    Optional<Property> optionalProperty = propertyService.getPropertyById(booking.getProperty().getId());
                                    if (optionalProperty.isPresent()) {
                                        Property property = optionalProperty.get();
                                        PropertyRating propertyRating = new PropertyRating();
                                        propertyRating.setTenantId(tenantId);
                                        propertyRating.setBookingId(bookingId);
                                        propertyRating.setPropertyId(property.getId());
                                        propertyRating.setRating(rating);
                                        ratingService.addPropertyRating(propertyRating);
                                        List<PropertyRating> existingRatings = ratingService.getRatingsForProperty(property.getId());
                                        if (existingRatings.isEmpty() || existingRatings.size() == 1) {
                                            property.setRating(Float.valueOf(rating));
                                        } else {
                                            int totalScore = 0;
                                            for (PropertyRating item : existingRatings) {
                                                totalScore += item.getRating();
                                            }
                                            totalScore += rating;
                                            Float updatedRating = (float) (totalScore / existingRatings.size() + 1);
                                            property.setRating(updatedRating);
                                        }
                                        propertyService.addProperty(property);
                                    } else {
                                        LOGGER.log(Level.ERROR, "No property with the {} ID exists in the database.", tenantId);
                                        throw new PropertyNotFoundException("No property found with ID: " + tenantId);
                                    }
                                } else {
                                    LOGGER.log(Level.WARN, "Can only rate Bookings that are already over!");
                                    System.out.println("Can only rate Bookings that are already over!");
                                }
                            } else {
                                LOGGER.log(Level.WARN, "This Booking has already been rated!");
                                System.out.println("This Booking has already been rated!");
                            }
                        } else {
                            LOGGER.log(Level.WARN, "A Tenant may only rate his/her own Bookings!");
                            System.out.println("A Tenant may only rate his/her own Bookings!");
                        }
                    } else {
                        LOGGER.log(Level.WARN, "Invalid rating specified: the rating must be between 1 and 5 inclusive");
                        System.out.println("Invalid rating specified: the rating must be between 1 and 5 inclusive");
                    }
                } else {
                    throw new AccessDeniedException("You do not have permission to rate a property that you did not book.");
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

    // AUXILIARY METHODS
    public String createPaymentConfirmationLetterToTenant(String firstName, String lastName, Booking booking) {
        String greeting = "Dear " + firstName + " " + lastName + ",\n\n";
        String info = "We are happy to inform you that your payment for booking #" + booking.getId() + " has been received.\n\n";
        String closing = "Your manager will be happy to see you in " + booking.getProperty().getSettlement() + ", " + booking.getProperty().getCountry() + "!\n\nBest regards,\n" + platformName + " team";
        return greeting + info + closing;
    }

    public String createPaymentConfirmationLetterToManager(String managerName, Booking booking) {
        String greeting = "Dear " + managerName + ",\n\n";
        String info = "We are happy to inform you that the payment for booking #" + booking.getId() + " has been made.\n\n";
        int payoutPeriodSetOrDefault = 20;
        for (NumericalConfig config : configService.getSystemSettings()) {
            if (config.getName().equals("PayoutPaymentPeriodDays")) payoutPeriodSetOrDefault = config.getValue().intValue();
        }
        int delaySetOrDefault = 7;
        for (NumericalConfig config : configService.getSystemSettings()) {
            if (config.getName().equals("ClaimPeriodDays")) delaySetOrDefault = config.getValue().intValue();
        }
        String payment = "We will remit this amount less our service commission to you within "
                + payoutPeriodSetOrDefault
                + " days of expiration of the booking period and the respective claim period, which should be about " + booking.getEndDate().toLocalDateTime().plusDays(delaySetOrDefault).plusDays(payoutPeriodSetOrDefault) + ".";
        String closing = "Make sure you arrange a warm welcome for our tenant at your place!\n\nBest regards,\n" + platformName + " team";
        return greeting + info + closing;
    }

    public String createEarlyTerminationLetterToManager(String managerName, EarlyTerminationRequest request) {
        String greeting = "Dear " + managerName + ",\n\n";
        String info = "Unfortunately, your tenant with booking ID #" + request.getBookingId() + " seems to have encountered a major change of plans for some reason, and thus he/she requests his/her current booking to be terminated earlier than expected, on " + request.getTerminationDate() + ". The reason stated by the tenant is as follows:\n\n";
        String reason = request.getComment() + "\n\n";
        String instructions = "While you do retain the right to decline this cancellation request, we kindly ask you to log in to our platform and consider whether you can be of even more help to your tenant and have his or her request granted. All relevant details are displayed in the respective notification at the site.\n\n";
        String closing = "We do apologize for this unfortunate turn of events, and hope that the situation will be resolved with as little inconvenience to you as possible.\n\nBest regards,\n" + platformName + " team";
        return greeting + info + reason + instructions + closing;
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



    public String createConfirmationEmailBody(String firstName, String lastName, String confirmationLink) {
        String greeting = "Hello " + firstName + " " + lastName + ",\n\n";
        String explanation = "We have received your request for a change of your email address. To confirm the change of your email, please click the following link:\n\n";
        String link = "http://localhost:8080/ten/confirm-email-change?token=" + confirmationLink + "\n\n";
        String expiration = "The confirmation link is going to expire in five minutes; should this be the case, please initiate the change of your email address once more.";
        String instructions = "If you have any trouble with the link, or if you did not request this registration, please contact our support team at " + platformMail + ".\n\n";
        String closing = "Thank you for choosing our service.\n\nBest regards,\n" + platformName + " team";

        return greeting + explanation + link + expiration + instructions + closing;
    }

    private boolean isEmailBusy(String email) {
        return tenantService.getTenantByEmail(email) != null;
    }
}
