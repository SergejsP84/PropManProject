package lv.emendatus.Destiny_PropMan.service.implementation;

import lv.emendatus.Destiny_PropMan.domain.dto.managerial.FinancialStatementDTO;
import lv.emendatus.Destiny_PropMan.domain.dto.managerial.ManagerProfileDTO;
import lv.emendatus.Destiny_PropMan.domain.dto.managerial.ManagerPropertyDTO;
import lv.emendatus.Destiny_PropMan.domain.dto.managerial.ManagerReservationDTO;
import lv.emendatus.Destiny_PropMan.domain.dto.reservation.BookingDTO_Reservation;
import lv.emendatus.Destiny_PropMan.domain.entity.*;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.BookingStatus;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.ClaimStatus;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.ClaimantType;
import lv.emendatus.Destiny_PropMan.mapper.BookingMapper;
import lv.emendatus.Destiny_PropMan.mapper.ManagerMapper;
import lv.emendatus.Destiny_PropMan.mapper.ManagerPropertyMapper;
import lv.emendatus.Destiny_PropMan.repository.interfaces.ManagerRepository;
import lv.emendatus.Destiny_PropMan.service.interfaces.AdvancedManagerService;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class JpaAdvancedManagerService implements AdvancedManagerService {

    private final ManagerRepository managerRepository;
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
    @Autowired
    private final ManagerMapper managerMapper;
    @Autowired
    private final ManagerPropertyMapper managerPropertyMapper;
    @Autowired
    private final BookingMapper bookingMapper;
    public JpaAdvancedManagerService(ManagerRepository managerRepository, JpaManagerService managerService, JpaBookingService bookingService, JpaPropertyService propertyService, JpaNumericalConfigService configService, JpaClaimService claimService, JpaTenantService tenantService, JpaLeasingHistoryService leasingHistoryService, JpaTenantPaymentService tenantPaymentService, JpaBillService billService, ManagerMapper managerMapper, ManagerPropertyMapper managerPropertyMapper, BookingMapper bookingMapper) {
        this.managerRepository = managerRepository;
        this.managerService = managerService;
        this.bookingService = bookingService;
        this.propertyService = propertyService;
        this.configService = configService;
        this.claimService = claimService;
        this.tenantService = tenantService;
        this.leasingHistoryService = leasingHistoryService;
        this.tenantPaymentService = tenantPaymentService;
        this.billService = billService;
        this.managerMapper = managerMapper;
        this.managerPropertyMapper = managerPropertyMapper;
        this.bookingMapper = bookingMapper;
    }

    @Override
    public ManagerProfileDTO getManagerProfile(Long managerId) {
        Optional<Manager> managerOptional = managerRepository.findById(managerId);
        if (managerOptional.isPresent()) {
            Manager manager = managerOptional.get();
            return managerMapper.INSTANCE.toDTO(manager);
        } else {
            // TODO Handle exception
            LOGGER.log(Level.ERROR, "No manager with the {} ID exists in the database.", managerId);
            return new ManagerProfileDTO();
        }
    }

    @Override
    public void updateManagerProfile(Long managerId, ManagerProfileDTO updatedProfile) {
        Optional<Manager> managerOptional = managerRepository.findById(managerId);
        if (managerOptional.isPresent()) {
            Manager existingManager = managerOptional.get();
            managerMapper.updateManagerFromDTO(existingManager, updatedProfile);
            managerRepository.save(existingManager);
        } else {
            // TODO: Handle exception
            LOGGER.log(Level.ERROR, "No manager with the {} ID exists in the database.", managerId);
        }
    }

    @Override
    public List<ManagerPropertyDTO> getManagerPropertyPortfolio(Long managerId) {
        Set<Property> properties = managerService.getManagerProperties(managerId);
        return properties.stream()
                .map(managerPropertyMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ManagerReservationDTO viewReservationsForProperty(Long propertyId) {
        Optional<Property> property = propertyService.getPropertyById(propertyId);
        if (property.isPresent()) {
            Set<Long> reservationIds = getReservationIdsForProperty(property.get());
            return new ManagerReservationDTO(propertyId, reservationIds);
        } else {
            //TODO: Handle exception
            LOGGER.error("No property with the {} ID exists in the database.", propertyId);
            return new ManagerReservationDTO();
        }
    }

    @Override
    public ManagerReservationDTO viewReservationsForManager(Long managerId) {
        Optional<Manager> manager = managerService.getManagerById(managerId);
        if (manager.isPresent()) {
            Set<Long> reservationIds = new HashSet<>();
            Set<Property> properties = managerService.getManagerProperties(managerId);
            for (Property property : properties) {
                reservationIds.addAll(getReservationIdsForProperty(property));
            }
            return new ManagerReservationDTO(managerId, reservationIds);
        } else {
            // TODO: Handle exception
            LOGGER.error("No manager with the {} ID exists in the database.", managerId);
            return new ManagerReservationDTO();
        }
    }

    @Override
    public List<BookingDTO_Reservation> getBookingsForProperty(Long propertyId) {
        ManagerReservationDTO bunch = viewReservationsForProperty(propertyId);
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
    }

    @Override
    public List<BookingDTO_Reservation> getBookingsForManager(Long managerId) {
        ManagerReservationDTO bunch = viewReservationsForManager(managerId);
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
    }

    @Override
    public void submitClaimfromManager(Long bookingId, String description) {
        Optional<Booking> booking = bookingService.getBookingById(bookingId);
        if (booking.isEmpty()) {
            LOGGER.log(Level.WARN, "No booking with the {} ID exists in the database.", bookingId);
            System.out.println("Booking not found");
        } else {
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
    public void closeBookingByManager(Long bookingId) {
        int delaySetOrDefault = 7;
        for (NumericalConfig config : configService.getSystemSettings()) {
            if (config.getName().equals("ClaimPeriodDays")) delaySetOrDefault = config.getValue().intValue();
        }
        Optional<Booking> optionalBooking = bookingService.getBookingById(bookingId);
        if (optionalBooking.isPresent()) {
            Booking booking = optionalBooking.get();
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
                LeasingHistory history = new LeasingHistory();
                Optional<Tenant> optionalTenant = tenantService.getTenantById(booking.getTenantId());
                optionalTenant.ifPresent(history::setTenant);
                history.setPropertyId(booking.getProperty().getId());
                history.setStartDate(booking.getStartDate());
                history.setEndDate(booking.getEndDate());
                leasingHistoryService.addLeasingHistory(history);
                bookingService.deleteBooking(bookingId);
            }
        } else {
            LOGGER.log(Level.ERROR, "No booking with the specified ID exists in the database.");
            // TODO: Handle the case where the booking with the given ID is not found
        }
    }

    @Override
    public FinancialStatementDTO generateFinancialStatement(LocalDate periodStart, LocalDate periodEnd, Long managerId) {
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
            } else {
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
    }

    public List<Bill> getUnpaidBillsForProperty(Long propertyId) {
        if (propertyService.getPropertyById(propertyId).isPresent()) return billService.getBillsByProperty(propertyService.getPropertyById(propertyId).get())
                .stream()
                .filter(bill -> !bill.isPaid())
                .collect(Collectors.toList());
        else return new ArrayList<>();
    }

    public List<Bill> getUnpaidBillsForManager(Long managerId) {
        List<Property> managerProperties = propertyService.getPropertiesByManager(managerId);
        List<Bill> unpaidBills = new ArrayList<>();
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
    }

}
