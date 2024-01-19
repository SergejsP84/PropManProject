package lv.emendatus.Destiny_PropMan.util;
import lv.emendatus.Destiny_PropMan.domain.entity.*;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.BookingStatus;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.ManagerType;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.PropertyType;
import lv.emendatus.Destiny_PropMan.service.implementation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;
@Component
public class TestDataInitializer {
    @Autowired
    private JpaBillService billService;
    @Autowired
    private JpaPropertyService propertyService;
    @Autowired
    private JpaAmenityService amenityService;
    @Autowired
    private JpaBookingService bookingService;
    @Autowired
    private JpaCurrencyService currencyService;
    @Autowired
    private JpaLeasingHistoryService leasingHistoryService;
    @Autowired
    private JpaManagerService managerService;
    @Autowired
    private JpaNumericalConfigService numericalConfigService;
    @Autowired
    private JpaPropertyAmenityService propertyAmenityService;
    @Autowired
    private JpaTenantPaymentService tenantPaymentService;
    @Autowired
    private JpaTenantService tenantService;

    @Autowired
    public TestDataInitializer(JpaBillService billService, JpaPropertyService propertyService, JpaAmenityService amenityService, JpaBookingService bookingService, JpaCurrencyService currencyService, JpaLeasingHistoryService leasingHistoryService, JpaManagerService managerService, JpaNumericalConfigService numericalConfigService, JpaPropertyAmenityService propertyAmenityService, JpaTenantPaymentService tenantPaymentService, JpaTenantService tenantService) {
        this.billService = billService;
        this.propertyService = propertyService;
        this.amenityService = amenityService;
        this.bookingService = bookingService;
        this.currencyService = currencyService;
        this.leasingHistoryService = leasingHistoryService;
        this.managerService = managerService;
        this.numericalConfigService = numericalConfigService;
        this.propertyAmenityService = propertyAmenityService;
        this.tenantPaymentService = tenantPaymentService;
        this.tenantService = tenantService;
    }
    public void initializeData() {
        initializeManagers();
        initializeCurrencies();
        initializePropertyAmenities();
        initializeNumericalConfigs();
        initializeAmenities();
        initializeTenants();
        initializeBills();
        initializeTenantPayments();
        initializeBookings();
        initializeLeasingHistories();
        initializeProperties();
    }
    public void initializeAmenities() {
        Amenity amenity1 = new Amenity();
        Amenity amenity2 = new Amenity();
        Amenity amenity3 = new Amenity();
        Amenity amenity4 = new Amenity();
        amenity1.setId(1L);
        amenity1.setDescription("Rooftop Pool");
        amenity2.setId(2L);
        amenity2.setDescription("Shower");
        amenity3.setId(3L);
        amenity3.setDescription("Balcony");
        amenity4.setId(4L);
        amenity4.setDescription("Room Service");

        amenityService.addAmenity(amenity1);
        amenityService.addAmenity(amenity2);
        amenityService.addAmenity(amenity3);
        amenityService.addAmenity(amenity4);
    }

    public void initializeBills() {
        initializeCurrencies();
        initializeProperties();
        Currency currency1 = currencyService.getCurrencyById(1L).orElseThrow();
        Currency currency2 = currencyService.getCurrencyById(2L).orElseThrow();
        Currency currency3 = currencyService.getCurrencyById(3L).orElseThrow(() -> new RuntimeException("Currency with ID 3 not found"));
        Property property1 = propertyService.getPropertyById(1L).orElseThrow();
        Property property2 = propertyService.getPropertyById(2L).orElseThrow();
        Property property3 = propertyService.getPropertyById(3L).orElseThrow();
        Bill bill1 = new Bill();
        bill1.setId(1L);
        bill1.setAmount(3000.0);
        bill1.setDueDate(Timestamp.valueOf("2023-01-15 12:30:45"));
        bill1.setExpenseCategory("Heating");
        bill1.setPaid(false);
        bill1.setIssuedAt(Timestamp.valueOf("2023-01-02 08:45:13"));
        bill1.setRecipient("ZeroKelvin Heating Co.");
        bill1.setCurrency(currency1);
        bill1.setProperty(property1);

        Bill bill2 = new Bill();
        bill2.setId(2L);
        bill2.setAmount(280.0);
        bill2.setDueDate(Timestamp.valueOf("2020-03-23 19:27:11"));
        bill2.setExpenseCategory("Drinks");
        bill2.setPaid(false);
        bill2.setIssuedAt(Timestamp.valueOf("2020-03-15 12:03:16"));
        bill2.setRecipient("FineWine Inc.");
        bill2.setCurrency(currency2);
        bill2.setProperty(property2);

        Bill bill3 = new Bill();
        bill3.setId(3L);
        bill3.setAmount(430.0);
        bill3.setDueDate(Timestamp.valueOf("2019-07-03 12:00:00"));
        bill3.setExpenseCategory("Catering");
        bill3.setPaid(true);
        bill3.setIssuedAt(Timestamp.valueOf("2019-06-15 13:00:00"));
        bill3.setRecipient("Live Lobsters & Lingerie");
        bill3.setCurrency(currency3);
        bill3.setProperty(property3);

        billService.addBill(bill1);
        billService.addBill(bill2);
        billService.addBill(bill3);
    }

    public void initializeBookings() {
        initializeProperties();
        Property property1 = propertyService.getPropertyById(1L).orElseThrow();
        Property property2 = propertyService.getPropertyById(2L).orElseThrow();
        Property property3 = propertyService.getPropertyById(3L).orElseThrow();
        Booking booking1 = new Booking();
        Booking booking2 = new Booking();
        Booking booking3 = new Booking();

        booking1.setId(1L);
        booking1.setStartDate(Timestamp.valueOf("2022-09-01 15:00:00"));
        booking1.setEndDate(Timestamp.valueOf("2022-10-04 12:00:00"));
        booking1.setProperty(property1);
        booking1.setStatus(BookingStatus.CONFIRMED);
        booking1.setPaid(true);
        booking1.setTenantId(1L);

        booking2.setId(2L);
        booking2.setStartDate(Timestamp.valueOf("2022-12-03 18:00:00"));
        booking2.setEndDate(Timestamp.valueOf("2022-12-17 12:00:00"));
        booking2.setProperty(property2);
        booking2.setStatus(BookingStatus.PENDING_PAYMENT);
        booking2.setPaid(false);
        booking2.setTenantId(2L);

        booking3.setId(3L);
        booking3.setStartDate(Timestamp.valueOf("2024-01-16 12:00:00"));
        booking3.setEndDate(Timestamp.valueOf("2024-02-13 15:00:00"));
        booking3.setProperty(property3);
        booking3.setStatus(BookingStatus.CURRENT);
        booking3.setPaid(false);
        booking3.setTenantId(3L);

        bookingService.addBooking(booking1);
        bookingService.addBooking(booking2);
        bookingService.addBooking(booking3);
    }

    public void initializeCurrencies() {
        Currency currency1 = new Currency(1L, "EUR");
        Currency currency2 = new Currency(2L, "USD");
        Currency currency3 = new Currency(3L, "THB");
        currencyService.addCurrency(currency1);
        currencyService.addCurrency(currency2);
        currencyService.addCurrency(currency3);
    }

    public void initializeLeasingHistories() {
        initializeTenants();
        LeasingHistory leasingHistory1 = new LeasingHistory();
        LeasingHistory leasingHistory2 = new LeasingHistory();
        LeasingHistory leasingHistory3 = new LeasingHistory();
        Tenant tenant1 = tenantService.getTenantById(1L)
                .orElseThrow(() -> new IllegalStateException("Tenant with ID 1 not found"));
        Tenant tenant2 = tenantService.getTenantById(2L)
                .orElseThrow(() -> new IllegalStateException("Tenant with ID 2 not found"));
        Tenant tenant3 = tenantService.getTenantById(3L)
                .orElseThrow(() -> new IllegalStateException("Tenant with ID 3 not found"));

        leasingHistory1.setId(1L);
        leasingHistory1.setStartDate(Timestamp.valueOf("2021-11-04 15:00:00"));
        leasingHistory1.setEndDate(Timestamp.valueOf("2021-11-18 18:00:00"));
        leasingHistory1.setPropertyId(1L);
        leasingHistory1.setTenant(tenant1);

        leasingHistory2.setId(2L);
        leasingHistory2.setStartDate(Timestamp.valueOf("2021-10-20 15:00:00"));
        leasingHistory2.setEndDate(Timestamp.valueOf("2021-11-05 19:00:00"));
        leasingHistory2.setPropertyId(2L);
        leasingHistory2.setTenant(tenant2);

        leasingHistory3.setId(3L);
        leasingHistory3.setStartDate(Timestamp.valueOf("2022-12-28 14:00:00"));
        leasingHistory3.setEndDate(Timestamp.valueOf("2023-01-09 15:00:00"));
        leasingHistory3.setPropertyId(3L);
        leasingHistory3.setTenant(tenant3);

        leasingHistoryService.addLeasingHistory(leasingHistory1);
        leasingHistoryService.addLeasingHistory(leasingHistory2);
        leasingHistoryService.addLeasingHistory(leasingHistory3);
    }
    public void initializeManagers() {
        initializeProperties();
        Manager manager1 = new Manager();
        Manager manager2 = new Manager();
        Manager manager3 = new Manager();
        Property property1 = propertyService.getPropertyById(1L)
                .orElseThrow(() -> new IllegalStateException("Property with ID 1 not found"));
        Property property2 = propertyService.getPropertyById(2L)
                .orElseThrow(() -> new IllegalStateException("Property with ID 2 not found"));
        Property property3 = propertyService.getPropertyById(3L)
                .orElseThrow(() -> new IllegalStateException("Property with ID 3 not found"));

        manager1.setId(1L);
        manager1.setManagerName("Stewart Ullman");
        manager1.setDescription("The best man for the best hotel");
        manager1.setType(ManagerType.PRIVATE);
        manager1.setActive(true);
        manager1.setJoinDate(Timestamp.valueOf("1975-03-18 13:30:42"));
        Set<Property> man1props = new HashSet<>();
        man1props.add(property1);
        manager1.setProperties(man1props);
        manager1.setLogin("SUllman");
        manager1.setPassword("CreditWhereDue");


        manager2.setId(2L);
        manager2.setManagerName("Delbert Grady");
        manager2.setDescription("Experienced in hotel management");
        manager2.setType(ManagerType.CORPORATE);
        manager2.setActive(false);
        manager2.setJoinDate(Timestamp.valueOf("1982-07-12 10:15:28"));
        Set<Property> man2props = new HashSet<>();
        man2props.add(property2);
        manager2.setProperties(man2props);
        manager2.setLogin("DGrady");
        manager2.setPassword("AllWorkAndNoPlay");

        manager3.setId(3L);
        manager3.setManagerName("Robert Townley Watson");
        manager3.setDescription("Eager to contribute to the hotel's legacy");
        manager3.setType(ManagerType.PRIVATE);
        manager3.setActive(true);
        manager3.setJoinDate(Timestamp.valueOf("1995-11-30 09:20:15"));
        Set<Property> man3props = new HashSet<>();
        man3props.add(property3);
        manager3.setProperties(man3props);
        manager3.setLogin("RWatson");
        manager3.setPassword("RedRumIsFun");

        managerService.addManager(manager1);
        managerService.addManager(manager2);
        managerService.addManager(manager3);
    }
    public void initializeNumericalConfigs() {
        NumericalConfig numericalConfig1 = new NumericalConfig();
        NumericalConfig numericalConfig2 = new NumericalConfig();
        NumericalConfig numericalConfig3 = new NumericalConfig();

        numericalConfig1.setId(1L);
        numericalConfig1.setName("A special discount for those choosing Room 217. We'd really like to see you there.");
        numericalConfig1.setValue(0.7);

        numericalConfig2.setId(2L);
        numericalConfig2.setName("Discount for my first customer!");
        numericalConfig2.setValue(0.9);

        numericalConfig3.setId(3L);
        numericalConfig3.setName("Surcharge here, guys. Sorry, this one's way too popular.");
        numericalConfig3.setValue(1.1);

        numericalConfigService.addNumericalConfig(numericalConfig1);
        numericalConfigService.addNumericalConfig(numericalConfig2);
        numericalConfigService.addNumericalConfig(numericalConfig3);
    }
    public void initializePropertyAmenities() {
        PropertyAmenity propertyAmenity1 = new PropertyAmenity(1L, 1L, 1L);
        PropertyAmenity propertyAmenity2 = new PropertyAmenity(2L, 2L, 2L);
        PropertyAmenity propertyAmenity3 = new PropertyAmenity(3L, 3L, 3L);

        propertyAmenityService.addPropertyAmenity(propertyAmenity1);
        propertyAmenityService.addPropertyAmenity(propertyAmenity2);
        propertyAmenityService.addPropertyAmenity(propertyAmenity3);
    }
    public void initializeTenantPayments() {
        initializeTenants();
        TenantPayment tenantPayment1 = new TenantPayment();
        TenantPayment tenantPayment2 = new TenantPayment();
        TenantPayment tenantPayment3 = new TenantPayment();

        Tenant tenant1 = tenantService.getTenantById(1L)
                .orElseThrow(() -> new IllegalStateException("Tenant with ID 1 not found"));
        Tenant tenant2 = tenantService.getTenantById(2L)
                .orElseThrow(() -> new IllegalStateException("Tenant with ID 2 not found"));
        Tenant tenant3 = tenantService.getTenantById(3L)
                .orElseThrow(() -> new IllegalStateException("Tenant with ID 3 not found"));

        tenantPayment1.setId(1L);
        tenantPayment1.setAmount(100.0);
        tenantPayment1.setAssociatedPropertyId(1L);
        tenantPayment1.setReceivedFromTenant(true);
        tenantPayment1.setManagerId(1L);
        tenantPayment1.setReceiptDue(Timestamp.valueOf("2024-01-10 12:00:00"));
        tenantPayment1.setFeePaidToManager(true);
        tenantPayment1.setTenant(tenant1);

        tenantPayment2.setId(2L);
        tenantPayment2.setAmount(200.0);
        tenantPayment2.setAssociatedPropertyId(2L);
        tenantPayment1.setReceivedFromTenant(true);
        tenantPayment2.setManagerId(2L);
        tenantPayment2.setReceiptDue(Timestamp.valueOf("2024-01-20 12:00:00"));
        tenantPayment2.setFeePaidToManager(false);
        tenantPayment2.setTenant(tenant2);

        tenantPayment3.setId(3L);
        tenantPayment3.setAmount(300.0);
        tenantPayment3.setAssociatedPropertyId(3L);
        tenantPayment3.setReceivedFromTenant(false);
        tenantPayment3.setManagerId(3L);
        tenantPayment3.setReceiptDue(Timestamp.valueOf("2024-01-30 12:00:00"));
        tenantPayment3.setFeePaidToManager(false);
        tenantPayment3.setTenant(tenant3);

        tenantPaymentService.addTenantPayment(tenantPayment1);
        tenantPaymentService.addTenantPayment(tenantPayment2);
        tenantPaymentService.addTenantPayment(tenantPayment3);
    }
    public void initializeTenants() {
        initializeProperties();
        Tenant tenant1 = new Tenant();
        Tenant tenant2 = new Tenant();
        Tenant tenant3 = new Tenant();
        Property property1 = propertyService.getPropertyById(1L)
                .orElseThrow(() -> new IllegalStateException("Property with ID 1 not found"));
        Property property2 = propertyService.getPropertyById(2L)
                .orElseThrow(() -> new IllegalStateException("Property with ID 2 not found"));
        Property property3 = propertyService.getPropertyById(3L)
                .orElseThrow(() -> new IllegalStateException("Property with ID 3 not found"));

        tenant1.setId(1L);
        tenant1.setEmail("lmassey@darkpassion.com");
        tenant1.setFirstName("Lorraine");
        tenant1.setLastName("Massey");
        tenant1.setActive(true); // oh how very true...
        tenant1.setIban("US13 BLBA US39 2291 2265 6661 9");
        tenant1.setLogin("LMassey");
        tenant1.setLogin("WaspsAndDesire");
        tenant1.setPaymentCardNo("9964 5554 6781 2463");
        tenant1.setPhone("1-800-569452");
        tenant1.setRating(5.0F);
        tenant1.setCurrentProperty(property1);

        tenant2.setId(2L);
        tenant2.setEmail("jdoe@email.com");
        tenant2.setFirstName("John");
        tenant2.setLastName("Doe");
        tenant2.setActive(false);
        tenant2.setIban("US98 BANA 1234 5678 9091 0111 2222 3333");
        tenant2.setLogin("JDoe");
        tenant2.setPassword("SecretPass123");
        tenant2.setPaymentCardNo("4321 8765 1234 5678");
        tenant2.setPhone("1-800-123456");
        tenant2.setRating(4.2F);
        tenant2.setCurrentProperty(property2);

        tenant3.setId(3L);
        tenant3.setEmail("awinters@email.com");
        tenant3.setFirstName("Alice");
        tenant3.setLastName("Winters");
        tenant3.setActive(true);
        tenant3.setIban("GB29 NWBK 6016 1331 9268 19");
        tenant3.setLogin("AWinters");
        tenant3.setPassword("WinterIsComing");
        tenant3.setPaymentCardNo("7890 1234 5678 4321");
        tenant3.setPhone("1-800-987654");
        tenant3.setRating(4.8F);
        tenant3.setCurrentProperty(property3);

        tenantService.addTenant(tenant1);
        tenantService.addTenant(tenant2);
        tenantService.addTenant(tenant3);
    }


    public void initializeProperties() {
        Property property1 = new Property();
        property1.setId(1L);
        property1.setAddress("Room 217, Hotel Overlook");
        property1.setSettlement("Colorado Mountains");
        property1.setCountry("USA");
        Manager manager = new Manager();
        manager.setId(1L);
        property1.setManager(manager);
        property1.setType(PropertyType.HOTEL_ROOM);
        Timestamp createdAt = new Timestamp(System.currentTimeMillis());
        property1.setCreatedAt(createdAt);
        property1.setDescription("Luxurious hotel in the Colorado Mountains, and a room with a RICH history. Lots of surprises await");
        property1.setPricePerDay(100.0);
        property1.setPricePerWeek(700.0);
        property1.setPricePerMonth(3000.0);
        property1.setRating(4.5F);
        property1.setSizeM2(29.6F);

        Property property2 = new Property();
        property2.setId(2L);
        property2.setAddress("Konkordijas iela 45");
        property2.setSettlement("Jurmala");
        property2.setCountry("Latvia");
        Manager manager2 = new Manager();
        manager2.setId(2L);
        property2.setManager(manager2);
        property2.setType(PropertyType.APARTMENT);
        Timestamp createdAt2 = new Timestamp(System.currentTimeMillis());
        property2.setCreatedAt(createdAt2);
        property2.setDescription("Cozy apartment in the heart of Riga");
        property2.setPricePerDay(80.0);
        property2.setPricePerWeek(500.0);
        property2.setPricePerMonth(2000.0);
        property2.setRating(4.2F);
        property2.setSizeM2(53.7F);

        Property property3 = new Property();
        property3.setId(3L);
        property3.setAddress("76 Beach Road, House 42");
        property3.setSettlement("Hua Hin");
        property3.setCountry("Thailand");
        Manager manager3 = new Manager();
        manager3.setId(3L);
        property3.setManager(manager3);
        property3.setType(PropertyType.HOUSE);
        Timestamp createdAt3 = new Timestamp(System.currentTimeMillis());
        property3.setCreatedAt(createdAt3);
        property3.setDescription("Spacious house with a garden in Hua Hin");
        property3.setPricePerDay(120.0);
        property3.setPricePerWeek(800.0);
        property3.setPricePerMonth(3500.0);
        property3.setRating(4.8F);
        property3.setSizeM2(148.1F);

        propertyService.addProperty(property1);
        propertyService.addProperty(property2);
        propertyService.addProperty(property3);
    }
}