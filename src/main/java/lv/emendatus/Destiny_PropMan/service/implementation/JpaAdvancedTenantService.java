package lv.emendatus.Destiny_PropMan.service.implementation;

import lv.emendatus.Destiny_PropMan.domain.entity.Currency;
import lv.emendatus.Destiny_PropMan.exceptions.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import lv.emendatus.Destiny_PropMan.domain.dto.profile.BookingHistoryDTO;
import lv.emendatus.Destiny_PropMan.domain.dto.profile.LeasingHistoryDTO_Profile;
import lv.emendatus.Destiny_PropMan.domain.dto.profile.TenantDTO_Profile;
import lv.emendatus.Destiny_PropMan.domain.dto.view.*;
import lv.emendatus.Destiny_PropMan.domain.entity.*;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.BookingStatus;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.ClaimStatus;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.ClaimantType;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.ETRequestStatus;
import lv.emendatus.Destiny_PropMan.mapper.*;
import lv.emendatus.Destiny_PropMan.repository.interfaces.PropertyRepository;
import lv.emendatus.Destiny_PropMan.repository.interfaces.TenantRepository;
import lv.emendatus.Destiny_PropMan.service.interfaces.AdvancedTenantService;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import javax.mail.MessagingException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneOffset;
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
    private final Logger LOGGER = LogManager.getLogger(JpaPropertyService.class);
    private static final String COMPLETED_PAYMENTS_FILE_PATH = "completedTenantPayments.txt";

    public JpaAdvancedTenantService(PropertyRepository propertyRepository, PropertyViewMapper propertyMapper, TenantRepository tenantRepository, TenantMapper tenantMapper, LeasingHistoryMapper leasingHistoryMapper, JpaLeasingHistoryService leasingHistoryService, JpaTenantFavoritesService favoritesService, JpaClaimService claimService, JpaBookingService bookingService, JpaTenantService tenantService, JpaNumericalConfigService configService, JpaTenantPaymentService paymentService, JpaPropertyService propertyService, JpaEarlyTerminationRequestService terminationService, JpaThirdPartyPaymentProviderService paymentProviderService, JpaCurrencyService currencyService, JpaEmailService emailService) {
        this.propertyRepository = propertyRepository;
        this.propertyMapper = propertyMapper;
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
    }

    @Override
    public PropertiesForTenantsDTO getPropertyDetails(Long propertyId, String tenantLogin) {
        Optional<Property> propertyOptional = propertyRepository.findById(propertyId);
        if (propertyOptional.isPresent()) {
            Property property = propertyOptional.get();
            PropertiesForTenantsDTO dto = propertyMapper.propertyToPropertiesForTenantsDTO(property);
            if (tenantLogin != null) {
                Optional<Tenant> tenantOptional = Optional.of(tenantService.getTenantByLogin(tenantLogin));
                if (tenantOptional.get().getPreferredCurrency() != null) {
                    Currency preferredCurrency = tenantOptional.get().getPreferredCurrency();
                    if (!tenantOptional.get().getPreferredCurrency().equals(currencyService.returnBaseCurrency())) {
                        dto.setPricePerDay(dto.getPricePerDay() * tenantOptional.get().getPreferredCurrency().getRateToBase()); // converting to tenant's currency
                        dto.setPricePerMonth(dto.getPricePerMonth() * tenantOptional.get().getPreferredCurrency().getRateToBase());
                        dto.setPricePerWeek(dto.getPricePerWeek() * tenantOptional.get().getPreferredCurrency().getRateToBase());
                    }
                    dto.setCurrency(preferredCurrency);
                }
            }
            return dto;
        } else {
            LOGGER.log(Level.ERROR, "No property with the {} ID exists in the database.", propertyId);
            throw new PropertyNotFoundException("No property found with ID: " + propertyId);
        }
    }

    @Override
    @PreAuthorize("hasRole('ROLE_TENANT') and #tenantId == principal.id")
    public TenantDTO_Profile getTenantInformation(Long tenantId) {
        Optional<Tenant> tenantOptional = tenantRepository.findById(tenantId);
        if (tenantOptional.isPresent()) {
            Tenant tenant = tenantOptional.get();
            return TenantMapper.INSTANCE.toDTO(tenant);
        } else {
            LOGGER.log(Level.ERROR, "No tenant with the {} ID exists in the database.", tenantId);
            throw new TenantNotFoundException("No tenant found with ID: " + tenantId);
        }
    }
    @Override
    @PreAuthorize("hasRole('ROLE_TENANT') and #tenantId == principal.id")
    @Transactional
    public void updateTenantInformation(Long tenantId, TenantDTO_Profile updatedTenantInfo) {
        Optional<Tenant> tenantOptional = tenantRepository.findById(tenantId);
        if (tenantOptional.isPresent()) {
            Tenant existingTenant = tenantOptional.get();
            tenantMapper.updateTenantFromDTO(existingTenant, updatedTenantInfo);
            tenantRepository.save(existingTenant);
        } else {
            LOGGER.log(Level.ERROR, "No tenant with the {} ID exists in the database.", tenantId);
            throw new TenantNotFoundException("No tenant found with ID: " + tenantId);
        }
    }
    @Override
    @PreAuthorize("hasRole('ROLE_TENANT') and #tenantId == principal.id")
    public BookingHistoryDTO viewBookingHistory(Long tenantId) {
        Optional<Tenant> tenantOptional = tenantRepository.findById(tenantId);
        if (tenantOptional.isPresent()) {
            Tenant tenant = tenantOptional.get();
            List<LeasingHistory> tenantsHistory = leasingHistoryService.getLeasingHistoryByTenant(tenant);
            List<LeasingHistoryDTO_Profile> leasingHistoryDTOs = new ArrayList<>();
            for (LeasingHistory history : tenantsHistory) {
                LeasingHistoryDTO_Profile leasingHistoryDTO = leasingHistoryMapper.toDTO(history);
                leasingHistoryDTOs.add(leasingHistoryDTO);
            }
            return new BookingHistoryDTO(tenantId, leasingHistoryDTOs);
        } else {
            LOGGER.log(Level.ERROR, "No tenant with the {} ID exists in the database.", tenantId);
            throw new TenantNotFoundException("No tenant found with ID: " + tenantId);
        }
    }


    @Override
    @PreAuthorize("hasRole('ROLE_TENANT') and #tenantId == principal.id")
    @Transactional
    public void saveFavoriteProperty(FavoritePropertyDTO favoriteDTO) {
        Tenant tenant = tenantRepository.findById(favoriteDTO.getTenantId()).orElse(null);
        Property property = propertyRepository.findById(favoriteDTO.getPropertyId()).orElse(null);
        if (tenant != null && property != null) {
            Long tenantId = favoriteDTO.getTenantId();
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
            LOGGER.log(Level.ERROR, "Either the Tenant or the Property could not be found!");
            throw new EntityNotFoundException("Either the Tenant or the Property could not be found!");
        }
    }

    @Override
    @PreAuthorize("hasRole('ROLE_TENANT') and #tenantId == principal.id")
    public List<FavoritePropertyDTO_Profile> getFavoriteProperties(Long tenantId) {
        Tenant tenant = tenantRepository.findById(tenantId).orElse(null);
        if (tenant != null) {
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
            LOGGER.log(Level.WARN, "No tenant with the {} ID exists in the database.", tenantId);
            throw new TenantNotFoundException("No tenant found with ID: " + tenantId);
        }
    }
    @Override
    @PreAuthorize("hasRole('ROLE_TENANT') and #tenantId == principal.id")
    public void removePropertyFromFavorites(Long tenantId, Long propertyId) {
        favoritesService.removePropertyFromFavorites(tenantId, propertyId);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_TENANT') and #tenantId == principal.id")
    @Transactional
    public void submitClaimfromTenant(Long bookingId, String description) {
        Optional<Booking> booking = bookingService.getBookingById(bookingId);
        if (booking.isEmpty()) {
            LOGGER.log(Level.WARN, "No booking with the {} ID exists in the database.", bookingId);
            System.out.println("Booking not found");
        } else {
            Long tenantId = booking.get().getTenantId();
            int delaySetOrDefault = 7;
            for (NumericalConfig config : configService.getSystemSettings()) {
                if (config.getName().equals("ClaimPeriodDays")) delaySetOrDefault = config.getValue().intValue();
            }
            if (bookingService.calculateDaysDifference(booking.get().getEndDate()) > delaySetOrDefault) {
                LOGGER.log(Level.WARN, "Sorry, the claiming period for booking {} has expired.", bookingId);
                System.out.println("Cannot create claim, claiming period expired for this booking");
                throw new BookingNotFoundException("No booking found with ID: " + bookingId);
            } else {
                Claim claim = new Claim();
                claim.setBookingId(bookingId);
                claim.setClaimStatus(ClaimStatus.OPEN);
                LocalDateTime localDateTime = LocalDateTime.now();
                Instant instant = localDateTime.atZone(ZoneOffset.UTC).toInstant();
                Timestamp timestamp = Timestamp.from(instant);
                claim.setCreatedAt(timestamp);
                claim.setAdmitted(false);
                claim.setClaimantType(ClaimantType.TENANT);
                claim.setDescription(description);
                claimService.addClaim(claim);
                LOGGER.log(Level.INFO, "Your claim in regard to booking {} has been filed.", bookingId);
                System.out.println("Claim lodged successfully");
            }
        }
    }

    @Override
    @PreAuthorize("hasRole('ROLE_TENANT') and #tenantId == principal.id")
    public List<PaymentsViewDTO> viewCompletedPayments(Long tenantId) {
        List<TenantPayment> allPaidPaymentsOfTenant = paymentService.getPaymentsByTenant(tenantId)
                .stream().filter(TenantPayment::isReceivedFromTenant).toList();
        List<Property> paidProperties = new ArrayList<>();
        for (TenantPayment payment : allPaidPaymentsOfTenant) {
            if (propertyService.getPropertyById(payment.getAssociatedPropertyId()).isPresent()) {
                paidProperties.add(propertyService.getPropertyById(payment.getAssociatedPropertyId()).get());

            }
        }
        return paymentsViewMapper.toDTOList(allPaidPaymentsOfTenant, paidProperties);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_TENANT') and #tenantId == principal.id")
    public List<PaymentsViewDTO> viewOutstandingPayments(Long tenantId) {
        List<TenantPayment> allOutstandingPaymentsOfTenant = paymentService.getPaymentsByTenant(tenantId)
                .stream().filter(tenantPayment -> !tenantPayment.isReceivedFromTenant()).toList();
        List<Property> bookedProperties = new ArrayList<>();
        for (TenantPayment payment : allOutstandingPaymentsOfTenant) {
            if (propertyService.getPropertyById(payment.getAssociatedPropertyId()).isPresent()) {
                bookedProperties.add(propertyService.getPropertyById(payment.getAssociatedPropertyId()).get());
            }
        }
        return paymentsViewMapper.toDTOList(allOutstandingPaymentsOfTenant, bookedProperties);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_TENANT') and #tenantId == principal.id")
    public List<BookingsViewDTO> viewTenantsBookings(Long tenantId) {
        List<Booking> tenantBookings = new ArrayList<>();
        if (tenantService.getTenantById(tenantId).isPresent()) {
            tenantBookings = bookingService.getBookingsByTenant(tenantService.getTenantById(tenantId).get()).stream().toList();
        } else {
            LOGGER.log(Level.WARN, "No tenant with the {} ID exists in the database.", tenantId);
            throw new TenantNotFoundException("No tenant found with ID: " + tenantId);
        }
        return bookingsViewMapper.toDTOList(tenantBookings);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_TENANT') and #tenantId == principal.id")
    @Transactional
    public void requestEarlyTermination(Long bookingId, LocalDateTime terminationDate, String comment) {
        if (terminationDate.isBefore(LocalDateTime.now().plusDays(1))) {
            LOGGER.log(Level.ERROR, "Termination date must be no earlier than the next day.");
            throw new BadEarlyTerminationRequestException("Termination must be no earlier than the next day.");
        }
        EarlyTerminationRequest request = new EarlyTerminationRequest();
        if (bookingService.getBookingById(bookingId).isPresent()) {
            if (terminationDate.isEqual(bookingService.getBookingById(bookingId).get().getEndDate().toLocalDateTime())
                    || terminationDate.isAfter(bookingService.getBookingById(bookingId).get().getEndDate().toLocalDateTime())) {
                LOGGER.log(Level.ERROR, "Early termination date must be before the final date of the initial booking.");
                throw new BadEarlyTerminationRequestException("Early termination date must be before the final date of the initial booking.");
            }
            Long tenantId = bookingService.getBookingById(bookingId).get().getTenantId();
            request.setTenantId(bookingService.getBookingById(bookingId).get().getTenantId());
            try {
                emailService.sendEmail(bookingService.getBookingById(bookingId).get().getProperty().getManager().getEmail(),
                        "Early cancellation request at [Platform Name]!",
                        createEarlyTerminationLetterToManager(bookingService.getBookingById(bookingId).get().getProperty().getManager().getManagerName(), request));
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        } else {
            LOGGER.log(Level.ERROR, "Could not extract Tenant from the Booking - Booking record possibly corrupt.");
            throw new TenantNotFoundException("Failed in extracting the Tenant from the Booking");
        }
        request.setStatus(ETRequestStatus.PENDING);
        request.setManagersResponse("");
        request.setTerminationDate(terminationDate);
        request.setBookingId(bookingId);
        request.setComment(comment);
        terminationService.addETRequest(request);

    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ROLE_TENANT') and #tenantId == principal.id")
    public void processPayment(TenantPayment tenantPayment) {
        Long tenantId = tenantPayment.getTenant().getId();
        boolean paymentSuccessful = false;
        if (tenantPayment.getTenant().getCardValidityDate().isBefore(YearMonth.now()))
        {
            LOGGER.error("Payment processing failed for TenantPayment with ID {} due to the expiration of the tenant's payment card.", tenantPayment.getId());
        } else {
            // Add third-party payment processing mechanism in the service
             paymentSuccessful = paymentProviderService.stub(tenantPayment);
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
                    e.printStackTrace();
                }
                try {
                        emailService.sendEmail(booking.getProperty().getManager().getEmail(),
                                "We have received the rental fee for your property!",
                                createPaymentConfirmationLetterToManager(booking.getProperty().getManager().getManagerName(), booking));
                } catch (MessagingException e) {
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
    }
    // AUXILIARY METHODS
    public String createPaymentConfirmationLetterToTenant(String firstName, String lastName, Booking booking) {
        String greeting = "Dear " + firstName + " " + lastName + ",\n\n";
        String info = "We are happy to inform you that your payment for booking #" + booking.getId() + " has been received.\n\n";
        String closing = "Your manager will be happy to see you in " + booking.getProperty().getSettlement() + ", " + booking.getProperty().getCountry() + "!\n\nBest regards,\n[Your Company Name]";
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
        String closing = "Make sure you arrange a warm welcome for our tenant at your place!\n\nBest regards,\n[Your Company Name]";
        return greeting + info + closing;
    }

    public String createEarlyTerminationLetterToManager(String managerName, EarlyTerminationRequest request) {
        String greeting = "Dear " + managerName + ",\n\n";
        String info = "Unfortunately, your tenant with booking ID #" + request.getBookingId() + " seems to have encountered a major change of plans for some reason, and thus he requests his current booking to be terminated earlier than expected, on " + request.getTerminationDate() + ". The reason stated by the tenant is as follows:\n\n";
        String reason = request.getComment();
        String instructions = "While you do retain the right to decline this cancellation request, we kindly ask you to log in to our platform and consider whether you can be of even more help to your tenant and have his or her request granted. All relevant details are displayed in the respective notification at the site.\n\n";
        String closing = "We do apologize for this unfortunate turn of events, and hope that the situation will be resolved with as little inconvenience to you as possible.\n\nBest regards,\n[Your Company Name]";
        return greeting + info + reason + instructions + closing;
    }
}
