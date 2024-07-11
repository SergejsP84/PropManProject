package lv.emendatus.Destiny_PropMan.scheduled_tasks;

import lv.emendatus.Destiny_PropMan.domain.entity.*;
import lv.emendatus.Destiny_PropMan.domain.entity.Currency;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.*;
import lv.emendatus.Destiny_PropMan.service.implementation.*;
import lv.emendatus.Destiny_PropMan.util.DatabasePurgeUtility;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;

@Component
public class SampleBaseCreator implements ApplicationRunner {

    private final Logger LOGGER = LogManager.getLogger(JpaTenantRegistrationService.class);
    @Autowired
    private JpaAdminAccountsService adminService;
    @Autowired
    private JpaNumericalConfigService configService;
    @Autowired
    private JpaPropertyService propertyService;
    @Autowired
    private JpaAdminAccountsService adminAccountsService;
    @Autowired
    private JpaManagerService managerService;
    @Autowired
    private JpaTenantService tenantService;
    @Autowired
    private JpaPropertyAmenityService propertyAmenityService;
    @Autowired
    private JpaCurrencyService currencyService;
    @Autowired
    private JpaBillService billService;
    @Autowired
    private JpaLeasingHistoryService leasingHistoryService;
    @Autowired
    private JpaAmenityService amenityService;
    @Autowired
    private JpaNumericDataMappingService numericDataMappingService;
    @Autowired
    private JpaTenantFavoritesService tenantFavoritesService;
    @Autowired
    private JpaPropertyDiscountService propertyDiscountService;
    @Autowired
    private JpaBookingService bookingService;
    @Autowired
    private JpaTenantPaymentService paymentService;
    @Autowired
    private JpaMessageService messageService;
    @Autowired
    private JpaClaimService claimService;
    @Autowired
    private JpaEarlyTerminationRequestService earlyTerminationRequestService;
    @Autowired
    private JpaPasswordResetTokenService passwordResetTokenService;
    @Autowired
    private JpaPayoutService payoutService;
    @Autowired
    private JpaPropertyLockService lockService;
    @Autowired
    private JpaPropertyRatingService propertyRatingService;
    @Autowired
    private JpaReviewService reviewService;
    @Autowired
    private JpaRefundService refundService;
    @Autowired
    private JpaTokenResetService tokenResetService;
    @Autowired
    private DatabasePurgeUtility databasePurgeUtility;
    private final PasswordEncoder passwordEncoder;

    public SampleBaseCreator(BCryptPasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
//        System.out.println("!!! - Sample database creator INVOKED!");
        boolean proposeDBCreation = true;
        boolean configExists = false;
        boolean createDBNow = false;

        for (NumericalConfig config : configService.getSystemSettings()) {
            if (config.getName().equals("ProposeSampleDatabaseCreation")) {
                configExists = true;
                if (config.getValue().equals(0.00)) proposeDBCreation = false;
            }
        }

        if (proposeDBCreation) {
            if (!configExists) {
                NumericalConfig newConfig = new NumericalConfig();
                newConfig.setType(NumConfigType.SYSTEM_SETTING);
                newConfig.setName("ProposeSampleDatabaseCreation");
                newConfig.setValue(1.00);
                configService.addNumericalConfig(newConfig);
            }

            Scanner sc = new Scanner(System.in);
            System.out.println();
            System.out.println(" *** Would you like to generate a sample database of entities for familiarization and testing purposes?");
            System.out.println(" *** Warning: the generator is intended for use with a clean database. If there are any other entities in the database already, entity conflicts or ID mismatches may occur.");
            System.out.println(" *** Please respond with a Yes or No.");
            System.out.println();

            String input = sc.nextLine();
            while (!(input.equalsIgnoreCase("yes") || input.equalsIgnoreCase("no"))) {
                System.out.println(" *** Please respond with a Yes or No.");
                input = sc.nextLine();
            }

            if (input.equalsIgnoreCase("yes")) {
                createDBNow = true;
            } else {
                System.out.println();
                System.out.println(" *** Would you like the system to propose the generation of a sample database on next startup?");
                System.out.println(" *** Please respond with a Yes or No.");
                System.out.println();

                String input2 = sc.nextLine();
                while (!(input2.equalsIgnoreCase("yes") || input2.equalsIgnoreCase("no"))) {
                    System.out.println(" *** Please respond with a Yes or No.");
                    input2 = sc.nextLine();
                }
                if (input2.equalsIgnoreCase("yes")) {
                    System.out.println(" *** Got it! Will propose it next time.");
                    sc.close();
                }
                if (input2.equalsIgnoreCase("no")) {
                    for (NumericalConfig config : configService.getSystemSettings()) {
                        if (config.getName().equals("ProposeSampleDatabaseCreation")) {
                            config.setValue(0.00);
                            configService.addNumericalConfig(config);
                        }
                    }
                    System.out.println(" *** Sample database generator disabled. To enable, please delete the NumericalConfig named ProposeSampleDatabaseCreation from the database or set its value to 1.00.");
                    sc.close();
                }
            }


            if (createDBNow) {
                System.out.println();
                System.out.println(" *** Checking the existing database...");
                boolean databaseNotEmpty = false;
                if (adminAccountsService.findAll().size() > 0) {
                    if (adminAccountsService.findAll().size() == 1 && adminAccountsService.findAll().get(0).getName().equals("DefaultAdmin")) {
                        System.out.println("   * No other admins found besides the Default Admin");
                    } else {
                        System.out.println("   * Found " + (adminAccountsService.findAll().size() - 1) + " Admin(s) besides the DefaultAdmin");
                        databaseNotEmpty = true;
                    }
                }
                if (amenityService.getAllAmenities().size() > 0) {
                    System.out.println("   * Found " + amenityService.getAllAmenities().size() + " Amenity(ies)");
                    databaseNotEmpty = true;
                } else {
                    System.out.println("   * No Amenities found");
                }
                if (billService.getAllBills().size() > 0) {
                    System.out.println("   * Found " + billService.getAllBills().size() + " Bill(s)");
                    databaseNotEmpty = true;
                } else {
                    System.out.println("   * No Bills found");
                }
                if (bookingService.getAllBookings().size() > 0) {
                    System.out.println("   * Found " + bookingService.getAllBookings().size() + " Booking(s)");
                    databaseNotEmpty = true;
                } else {
                    System.out.println("   * No Bookings found");
                }
                if (claimService.getAllClaims().size() > 0) {
                    System.out.println("   * Found " + claimService.getAllClaims().size() + " Claim(s)");
                    databaseNotEmpty = true;
                } else {
                    System.out.println("   * No Claims found");
                }
                if (currencyService.getAllCurrencies().size() > 0) {
                    System.out.println("   * Found " + currencyService.getAllCurrencies().size() + " Currency(ies)");
                    databaseNotEmpty = true;
                } else {
                    System.out.println("   * No Currencies found");
                }
                if (earlyTerminationRequestService.getAllETRequests().size() > 0) {
                    System.out.println("   * Found " + earlyTerminationRequestService.getAllETRequests().size() + " Early Termination Request(s)");
                    databaseNotEmpty = true;
                } else {
                    System.out.println("   * No EarlyTerminationRequests found");
                }
                if (leasingHistoryService.getAllLeasingHistories().size() > 0) {
                    System.out.println("   * Found " + leasingHistoryService.getAllLeasingHistories().size() + " Leasing History(ies)");
                    databaseNotEmpty = true;
                } else {
                    System.out.println("   * No LeasingHistories found");
                }
                if (managerService.getAllManagers().size() > 0) {
                    System.out.println("   * Found " + managerService.getAllManagers().size() + " Manager(s)");
                    databaseNotEmpty = true;
                } else {
                    System.out.println("   * No Managers found");
                }
                if (messageService.getAllMessages().size() > 0) {
                    System.out.println("   * Found " + messageService.getAllMessages().size() + " Message(s)");
                    databaseNotEmpty = true;
                } else {
                    System.out.println("   * No Messages found");
                }
                if (configService.getAllNumericalConfigs().size() > 0) {
                    if (configService.getAllNumericalConfigs().size() == 1 && configService.getAllNumericalConfigs().get(0).getName().equals("ProposeSampleDatabaseCreation")) {
                        System.out.println("   * No other NumericalConfigs found besides the configuration for proposing the creation of a sample database");
                    } else {
                        System.out.println("   * Found " + (configService.getAllNumericalConfigs().size() - 1) + " NumericalConfigs(s) besides the configuration for proposing the creation of a sample database");
                        databaseNotEmpty = true;
                    }
                }
                if (messageService.getAllMessages().size() > 0) {
                    System.out.println("   * Found " + messageService.getAllMessages().size() + " Message(s)");
                    databaseNotEmpty = true;
                } else {
                    System.out.println("   * No Messages found");
                }
                if (numericDataMappingService.getAllMappings().size() > 0) {
                    System.out.println("   * Found " + numericDataMappingService.getAllMappings().size() + " Numeric Data Mapping(s)");
                    databaseNotEmpty = true;
                } else {
                    System.out.println("   * No Numeric Data Mappings found");
                }
                if (passwordResetTokenService.getAllPasswordResetTokens().size() > 0) {
                    System.out.println("   * Found " + passwordResetTokenService.getAllPasswordResetTokens().size() + " Password Reset Token(s)");
                    databaseNotEmpty = true;
                } else {
                    System.out.println("   * No Password Reset Tokens found");
                }
                if (payoutService.getAllPayouts().size() > 0) {
                    System.out.println("   * Found " + payoutService.getAllPayouts().size() + " Payout(s)");
                    databaseNotEmpty = true;
                } else {
                    System.out.println("   * No Payouts found");
                }
                if (propertyService.getAllProperties().size() > 0) {
                    System.out.println("   * Found " + propertyService.getAllProperties().size() + " Property(ies)");
                    databaseNotEmpty = true;
                } else {
                    System.out.println("   * No Properties found");
                }
                if (propertyAmenityService.getAllPropertyAmenities().size() > 0) {
                    System.out.println("   * Found " + propertyAmenityService.getAllPropertyAmenities().size() + " Property-Amenity relation(s)");
                    databaseNotEmpty = true;
                } else {
                    System.out.println("   * No Property-Amenity relations found");
                }
                if (propertyDiscountService.getAllPropertyDiscounts().size() > 0) {
                    System.out.println("   * Found " + propertyDiscountService.getAllPropertyDiscounts().size() + " Property Discount(s)");
                    databaseNotEmpty = true;
                } else {
                    System.out.println("   * No Property Discounts found");
                }
                if (lockService.getAllPropertyLocks().size() > 0) {
                    System.out.println("   * Found " + lockService.getAllPropertyLocks().size() + " Property Lock(s)");
                    databaseNotEmpty = true;
                } else {
                    System.out.println("   * No Property Locks found");
                }
                if (lockService.getAllPropertyLocks().size() > 0) {
                    System.out.println("   * Found " + lockService.getAllPropertyLocks().size() + " Property Lock(s)");
                    databaseNotEmpty = true;
                } else {
                    System.out.println("   * No Property Locks found");
                }
                if (propertyRatingService.getAllPropertyRatings().size() > 0) {
                    System.out.println("   * Found " + propertyRatingService.getAllPropertyRatings().size() + " Property Rating(s)");
                    databaseNotEmpty = true;
                } else {
                    System.out.println("   * No Property Ratings found");
                }
                if (propertyRatingService.getAllPropertyRatings().size() > 0) {
                    System.out.println("   * Found " + propertyRatingService.getAllPropertyRatings().size() + " Property Rating(s)");
                    databaseNotEmpty = true;
                } else {
                    System.out.println("   * No Property Ratings found");
                }
                if (refundService.getAllRefunds().size() > 0) {
                    System.out.println("   * Found " + refundService.getAllRefunds().size() + " Refund(s)");
                    databaseNotEmpty = true;
                } else {
                    System.out.println("   * No Refunds found");
                }
                if (reviewService.getAllReviews().size() > 0) {
                    System.out.println("   * Found " + reviewService.getAllReviews().size() + " Review(s)");
                    databaseNotEmpty = true;
                } else {
                    System.out.println("   * No Reviews found");
                }
                if (tenantService.getAllTenants().size() > 0) {
                    System.out.println("   * Found " + tenantService.getAllTenants().size() + " Tenant(s)");
                    databaseNotEmpty = true;
                } else {
                    System.out.println("   * No Tenants found");
                }
                if (tenantFavoritesService.getAllTenantFavorites().size() > 0) {
                    System.out.println("   * Found " + tenantFavoritesService.getAllTenantFavorites().size() + " Tenant Favorite(s)");
                    databaseNotEmpty = true;
                } else {
                    System.out.println("   * No Tenant Favorites found");
                }
                if (paymentService.getAllTenantPayments().size() > 0) {
                    System.out.println("   * Found " + paymentService.getAllTenantPayments().size() + " Tenant Payment(s)");
                    databaseNotEmpty = true;
                } else {
                    System.out.println("   * No Tenant Payments found");
                }
                if (tokenResetService.getAllResetters().size() > 0) {
                    System.out.println("   * Found " + tokenResetService.getAllResetters().size() + " Token Resetter(s)");
                    databaseNotEmpty = true;
                } else {
                    System.out.println("   * No TokenResetters found");
                }
                boolean goOn = true;
                if (databaseNotEmpty) {
                    System.out.println();
                    System.out.println(" *** There are entities in the database. Would you like to purge the database before generating test entities?");
                    System.out.println(" *** This will delete all existing entities from the database except for the DefaultAdmin user.");
                    System.out.println(" *** Please type Purge to purge the database, Continue to generate new entities without purging (not recommended),");
                    System.out.println(" *** or Stop to terminate the generation process.");
                    System.out.println();
                    goOn = false;
                    String input3 = sc.nextLine();
                    while (!(input3.equalsIgnoreCase("purge") || input3.equalsIgnoreCase("continue") || input3.equalsIgnoreCase("stop"))) {
                        System.out.println(" *** Please type Purge to purge the database, Continue to generate new entities without purging (not recommended),");
                        System.out.println(" *** or Stop to terminate the generation process.");
                        input3 = sc.nextLine();
                    }
                    if (input3.equalsIgnoreCase("stop")) {
                        System.out.println(" *** Database generation process terminated.");
                        sc.close();
                    } else if (input3.equalsIgnoreCase("purge")) {
                        System.out.println("Purging the database...");
//                        sc.close();
                        goOn = databasePurgeUtility.purgeDatabase();
                        if (!goOn) {
                            System.out.println(" *** Failed to start the database purge mechanism, the database creation process will not continue.");
                        } else {
                            System.out.println(" *** Database purged successfully.");
                        }

                    } else if (input3.equalsIgnoreCase("continue")) {
                        System.out.println();
                        System.out.println(" *** WARNING: Generating a sample database without purging the existing one may result in unexpected test behavior due to index mismatches.");
                        System.out.println(" *** Do you really want to populate the database with entities in addition to the existing ones?");
                        System.out.println(" *** Please respond with a Yes or No.");
                        System.out.println();
                        String input4 = sc.nextLine();
                        while (!(input4.equalsIgnoreCase("yes") || input4.equalsIgnoreCase("no"))) {
                            System.out.println(" *** Please respond with a Yes or No.");
                            input4 = sc.nextLine();
                        }
                        if (input4.equalsIgnoreCase("yes")) {
                            goOn = true;
                            System.out.println(" *** Confirmed, populating the database without purging.");
//                            sc.close();
                        }
                        if (input4.equalsIgnoreCase("no")) {
                            System.out.println(" *** Database generation process terminated.");
                            sc.close();
                        }
                    }
                }

                if (goOn) {
                    System.out.println();
                    System.out.println(" *** Creating sample database...");

                    System.out.println();
                    System.out.println(" *** Creating Properties...");

                    Set<Booking> bookingSet = new HashSet<>();
                    Set<Bill> billSet = new HashSet<>();
                    List<String> photoLinks = new ArrayList<>();

                    Property property1 = new Property();
                    property1.setId(1L);
                    property1.setStatus(PropertyStatus.AVAILABLE);
                    property1.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
                    property1.setType(PropertyType.APARTMENT);
                    property1.setAddress("Jūrmalas gatve 57-11");
                    property1.setSettlement("Riga");
                    property1.setCountry("Latvia");
                    property1.setSizeM2(50.9F);
                    property1.setRating(0F);
                    property1.setPricePerDay(35.00);
                    property1.setPricePerWeek(120.00);
                    property1.setPricePerMonth(250.00);
                    property1.setBookings(bookingSet);
                    property1.setBills(billSet);
                    property1.setTenant(null);
                    property1.setPhotos(photoLinks);
                    property1.setDescription("A small apartment in a green neighbourhood of the city; great for business trips");

                    // NOT ADDED YET - WILL ADD AFTER MANAGERS ARE DONE
                    System.out.println("   * Property " + property1.getId() + ": " + property1.getDescription() + " set up.");

                    Property property2 = new Property();
                    property2.setId(2L);
                    property2.setStatus(PropertyStatus.AVAILABLE);
                    property2.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
                    property2.setType(PropertyType.HOUSE);
                    property2.setAddress("Konkordijas iela 45-k1");
                    property2.setSettlement("Jurmala");
                    property2.setCountry("Latvia");
                    property2.setSizeM2(61.5F);
                    property2.setRating(0F);
                    property2.setPricePerDay(120.00);
                    property2.setPricePerWeek(700.00);
                    property2.setPricePerMonth(2000.00);
                    property2.setBookings(bookingSet);
                    property2.setBills(billSet);
                    property2.setTenant(null);
                    property2.setPhotos(photoLinks);
                    property2.setDescription("A neat seaside cottage to enjoy the Jurmala resort atmosphere at");
                    // NOT ADDED YET - WILL ADD AFTER MANAGERS ARE DONE
                    System.out.println("   * Property " + property2.getId() + ": " + property2.getDescription() + " set up.");

                    Property property3 = new Property();
                    property3.setId(3L);
                    property3.setStatus(PropertyStatus.AVAILABLE);
                    property3.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
                    property3.setType(PropertyType.APARTMENT);
                    property3.setAddress("Grēcinieku iela 11-31");
                    property3.setSettlement("Riga");
                    property3.setCountry("Latvia");
                    property3.setSizeM2(74.0F);
                    property3.setRating(0F);
                    property3.setPricePerDay(60.00);
                    property3.setPricePerWeek(300.00);
                    property3.setPricePerMonth(650.00);
                    property3.setBookings(bookingSet);
                    property3.setBills(billSet);
                    property3.setTenant(null);
                    property3.setPhotos(photoLinks);
                    property3.setDescription("A spacious apartment in the very heart of the city, the medieval-spirited Old Town");
                    // NOT ADDED YET - WILL ADD AFTER MANAGERS ARE DONE
                    System.out.println("   * Property " + property3.getId() + ": " + property3.getDescription() + " set up.");

                    Property property4 = new Property();
                    property4.setId(4L);
                    property4.setStatus(PropertyStatus.AVAILABLE);
                    property4.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
                    property4.setType(PropertyType.HOTEL_ROOM);
                    property4.setAddress("Hotel Overlook, Colorado Mountains");
                    property4.setSettlement("Sidewinder");
                    property4.setCountry("USA");
                    property4.setSizeM2(28.4F);
                    property4.setRating(0F);
                    property4.setPricePerDay(750.00);
                    property4.setPricePerWeek(4000.00);
                    property4.setPricePerMonth(10000.00);
                    property4.setBookings(bookingSet);
                    property4.setBills(billSet);
                    property4.setTenant(null);
                    property4.setPhotos(photoLinks);
                    property4.setDescription("Room 217. You can check out any time you like... but you can never really leave");
                    // NOT ADDED YET - WILL ADD AFTER MANAGERS ARE DONE
                    System.out.println("   * Property " + property4.getId() + ": " + property4.getDescription() + " set up.");

                    Property property5 = new Property();
                    property5.setId(5L);
                    property5.setStatus(PropertyStatus.AVAILABLE);
                    property5.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
                    property5.setType(PropertyType.OTHER);
                    property5.setAddress("Hanging Gardens of Bali, Desa Buahan, Gianyar");
                    property5.setSettlement("Bali");
                    property5.setCountry("Indonesia");
                    property5.setSizeM2(50.9F);
                    property5.setRating(0F);
                    property5.setPricePerDay(860.00);
                    property5.setPricePerWeek(6390.00);
                    property5.setPricePerMonth(20400.00);
                    property5.setBookings(bookingSet);
                    property5.setBills(billSet);
                    property5.setTenant(null);
                    property5.setPhotos(photoLinks);
                    property5.setDescription("A luxury riverside infinity pool villa");
                    // NOT ADDED YET - WILL ADD AFTER MANAGERS ARE DONE
                    System.out.println("   * Property " + property5.getId() + ": " + property5.getDescription() + " set up.");

                    Property property6 = new Property();
                    property6.setId(6L);
                    property6.setStatus(PropertyStatus.AVAILABLE);
                    property6.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
                    property6.setType(PropertyType.ROOM);
                    property6.setAddress("192 Soi Sabai");
                    property6.setSettlement("Koh Phangan");
                    property6.setCountry("Thailand");
                    property6.setSizeM2(18.0F);
                    property6.setRating(0F);
                    property6.setPricePerDay(40.00);
                    property6.setPricePerWeek(180.00);
                    property6.setPricePerMonth(500.00);
                    property6.setBookings(bookingSet);
                    property6.setBills(billSet);
                    property6.setTenant(null);
                    property6.setPhotos(photoLinks);
                    property6.setDescription("An affordable yet cosy room to sleep off the fatigue during the crazy days of the famous Full Moon Party");
                    // NOT ADDED YET - WILL ADD AFTER MANAGERS ARE DONE
                    System.out.println("   * Property " + property6.getId() + ": " + property6.getDescription() + " set up.");

                    Property property7 = new Property();
                    property7.setId(7L);
                    property7.setStatus(PropertyStatus.AVAILABLE);
                    property7.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
                    property7.setType(PropertyType.COMMERCIAL);
                    property7.setAddress("Baltā iela 1B");
                    property7.setSettlement("Riga");
                    property7.setCountry("Latvia");
                    property7.setSizeM2(35.5F);
                    property7.setRating(0F);
                    property7.setPricePerDay(40.00);
                    property7.setPricePerWeek(130.00);
                    property7.setPricePerMonth(320.00);
                    property7.setBookings(bookingSet);
                    property7.setBills(billSet);
                    property7.setTenant(null);
                    property7.setPhotos(photoLinks);
                    property7.setDescription("An outfitted office and meeting room at the Forums Business Park, available for short-term or long-term rent");
                    // NOT ADDED YET - WILL ADD AFTER MANAGERS ARE DONE
                    System.out.println("   * Property " + property7.getId() + ": " + property7.getDescription() + " set up.");

                    Property property8 = new Property();
                    property8.setId(8L);
                    property8.setStatus(PropertyStatus.AVAILABLE);
                    property8.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
                    property8.setType(PropertyType.HOTEL_ROOM);
                    property8.setAddress("Sheepless Arms Hotel, 144 Santa Monica Boulevard");
                    property8.setSettlement("Los Angeles");
                    property8.setCountry("USA");
                    property8.setSizeM2(29.0F);
                    property8.setRating(0F);
                    property8.setPricePerDay(140.00);
                    property8.setPricePerWeek(480.00);
                    property8.setPricePerMonth(1900.00);
                    property8.setBookings(bookingSet);
                    property8.setBills(billSet);
                    property8.setTenant(null);
                    property8.setPhotos(photoLinks);
                    property8.setDescription("Room 804 - a businesslike accommodation for all sorts of visitors");
                    // NOT ADDED YET - WILL ADD AFTER MANAGERS ARE DONE
                    System.out.println("   * Property " + property8.getId() + ": " + property8.getDescription() + " set up.");

                    Property property9 = new Property();
                    property9.setId(9L);
                    property9.setStatus(PropertyStatus.AVAILABLE);
                    property9.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
                    property9.setType(PropertyType.HOTEL_ROOM);
                    property9.setAddress("Blue Marine Resort & Spa");
                    property9.setSettlement("Crete");
                    property9.setCountry("Greece");
                    property9.setSizeM2(29.0F);
                    property9.setRating(0F);
                    property9.setPricePerDay(380.00);
                    property9.setPricePerWeek(1200.00);
                    property9.setPricePerMonth(3600.00);
                    property9.setBookings(bookingSet);
                    property9.setBills(billSet);
                    property9.setTenant(null);
                    property9.setPhotos(photoLinks);
                    property9.setDescription("Room 67 - a comfortable Mediterranean-white sea-view suite");
                    // NOT ADDED YET - WILL ADD AFTER MANAGERS ARE DONE
                    System.out.println("   * Property " + property9.getId() + ": " + property9.getDescription() + " set up.");

                    Property property10 = new Property();
                    property10.setId(10L);
                    property10.setStatus(PropertyStatus.AVAILABLE);
                    property10.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
                    property10.setType(PropertyType.HOUSE);
                    property10.setAddress("254 Soi Kratong");
                    property10.setSettlement("Pattaya");
                    property10.setCountry("Thailand");
                    property10.setSizeM2(49.0F);
                    property10.setRating(0F);
                    property10.setPricePerDay(110.00);
                    property10.setPricePerWeek(560.00);
                    property10.setPricePerMonth(1900.00);
                    property10.setBookings(bookingSet);
                    property10.setBills(billSet);
                    property10.setTenant(null);
                    property10.setPhotos(photoLinks);
                    property10.setDescription("A neat house for a true Farang to stay at during yet another unforgettable Pattaya trip");
                    // NOT ADDED YET - WILL ADD AFTER MANAGERS ARE DONE
                    System.out.println("   * Property " + property10.getId() + ": " + property10.getDescription() + " set up.");

                    Property property11 = new Property();
                    property11.setId(11L);
                    property11.setStatus(PropertyStatus.AVAILABLE);
                    property11.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
                    property11.setType(PropertyType.COMMERCIAL);
                    property11.setAddress("55 Walking Street");
                    property11.setSettlement("Pattaya");
                    property11.setCountry("Thailand");
                    property11.setSizeM2(184.8F);
                    property11.setRating(0F);
                    property11.setPricePerDay(500.00);
                    property11.setPricePerWeek(1000.00);
                    property11.setPricePerMonth(3000.00);
                    property11.setBookings(bookingSet);
                    property11.setBills(billSet);
                    property11.setTenant(null);
                    property11.setPhotos(photoLinks);
                    property11.setDescription("A spacious venue for setting up one-time events or permanent entertainment facilities");
                    // NOT ADDED YET - WILL ADD AFTER MANAGERS ARE DONE
                    System.out.println("   * Property " + property11.getId() + ": " + property11.getDescription() + " set up.");

                    Property property12 = new Property();
                    property12.setId(12L);
                    property12.setStatus(PropertyStatus.AVAILABLE);
                    property12.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
                    property12.setType(PropertyType.HOTEL_ROOM);
                    property12.setAddress("Hotel Overlook, Colorado Mountains");
                    property12.setSettlement("Sidewinder");
                    property12.setCountry("USA");
                    property12.setSizeM2(87.9F);
                    property12.setRating(0F);
                    property12.setPricePerDay(2500.00);
                    property12.setPricePerWeek(8800.00);
                    property12.setPricePerMonth(27000.00);
                    property12.setBookings(bookingSet);
                    property12.setBills(billSet);
                    property12.setTenant(null);
                    property12.setPhotos(photoLinks);
                    property12.setDescription("Presidential Suite. Used to welcome many prominent and influential persons from the classic times. One can tell, at least some elements of them still remain here in a sense.");
                    // NOT ADDED YET - WILL ADD AFTER MANAGERS ARE DONE
                    System.out.println("   * Property " + property4.getId() + ": " + property4.getDescription() + " set up.");

                    // Adding Amenities
                    System.out.println();
                    System.out.println(" *** Creating Amenities...");
                    List<String> amenityDescriptions = List.of(
                            "WiFi", "Infinity Pool", "Elevator", "Gym", "Spa", "Air Conditioning",
                            "Heating", "Free Parking", "Breakfast Included", "Pet Friendly",
                            "24-Hour Security", "Balcony", "Ocean View", "Mountain View",
                            "Fireplace", "Fully Equipped Kitchen", "Washer and Dryer", "Private Entrance",
                            "BBQ Grill", "Hot Tub", "Sauna", "Bicycle Rental", "Playground",
                            "Library", "Home Theater", "Concierge Service", "Housekeeping Service",
                            "Outdoor Furniture", "Garden", "Rooftop Terrace", "Indoor Pool", "Outdoor Pool"
                    );
                    Long id = 0L;
                    for (String amenity : amenityDescriptions) {
                        id++;
                        amenityService.addAmenity(new Amenity(id, amenity));
                        System.out.println("   * Amenity " + id + ": " + amenity + " set up.");
                    }

                    // Adding another Admin
                    System.out.println();
                    System.out.println(" *** Creating another Admin...");
                    if (tenantService.getTenantByLogin("AnotherAdmin") == null && managerService.getManagerByLogin("AnotherAdmin") == null && adminAccountsService.findByLogin("AnotherAdmin").isEmpty()) {
                        Admin admin = new Admin();
                        admin.setName("Assistant Admin Jānis Počs");
                        admin.setLogin("AnotherAdmin");
                        admin.setPassword(passwordEncoder.encode("AdminPassword123"));
                        admin.setEmail("propman_testmail1@inbox.lv");
                        List<String> knownIPs = new ArrayList<>();
                        admin.setKnownIps(knownIPs);
                        List<GrantedAuthority> authorities = new ArrayList<>();
                        authorities.add(new SimpleGrantedAuthority("ADMIN"));
                        admin.setAuthorities(authorities);
                        adminAccountsService.addAdmin(admin);
                        System.out.println();
                        System.out.println("   * Admin added. Login: AnotherAdmin / Password: AdminPassword123 / Email: propman_testmail1@inbox.lv / Use the password Pattaya2016 to access the test email account at inbox.lv");
                    } else {
                        LOGGER.log(Level.INFO, "Admin not added: login already exists in the database.");
                        System.out.println("Admin not added: login already exists in the database.");
                    }

                    // Adding Currencies
                    System.out.println();
                    System.out.println(" *** Creating Currencies...");
                    Currency currency1 = new Currency();
                    currency1.setId(1L);
                    currency1.setDesignation("EUR");
                    currency1.setIsBaseCurrency(true);
                    currencyService.addCurrency(currency1);
                    System.out.println("   * Currency " + currency1.getId() + ": " + currency1.getDesignation() + " set up and assigned as the base currency.");
                    Currency currency2 = new Currency();
                    currency2.setId(2L);
                    currency2.setDesignation("USD");
                    currency2.setIsBaseCurrency(false);
                    currencyService.addCurrency(currency2);
                    System.out.println("   * Currency " + currency2.getId() + ": " + currency2.getDesignation() + " set up.");
                    Currency currency3 = new Currency();
                    currency3.setId(3L);
                    currency3.setDesignation("THB");
                    currency3.setIsBaseCurrency(false);
                    currencyService.addCurrency(currency3);
                    System.out.println("   * Currency " + currency3.getId() + ": " + currency3.getDesignation() + " set up.");
                    Currency currency4 = new Currency();
                    currency4.setId(4L);
                    currency4.setDesignation("CNU");
                    currency4.setIsBaseCurrency(false);
                    currencyService.addCurrency(currency4);
                    System.out.println("   * Currency " + currency4.getId() + ": " + currency4.getDesignation() + " set up.");

                    // Adding NumericalConfigs
                    NumericalConfig config2 = new NumericalConfig();
                    config2.setName("RefundPaymentPeriodDays");
                    config2.setValue(15.0);
                    config2.setType(NumConfigType.SYSTEM_SETTING);
                    configService.addNumericalConfig(config2);
                    LOGGER.info("New NumericalConfig entity created - Refund payment period set to 15 days");
                    System.out.println("   * New NumericalConfig entity created - Refund payment period set to 15 days");
                    NumericalConfig config3 = new NumericalConfig();
                    config3.setName("PayoutPaymentPeriodDays");
                    config3.setValue(20.0);
                    config3.setType(NumConfigType.SYSTEM_SETTING);
                    configService.addNumericalConfig(config3);
                    LOGGER.info("New NumericalConfig entity created - Payout payment period set to 20 days");
                    System.out.println("   * New NumericalConfig entity created - Payout payment period set to 20 days");
                    NumericalConfig config4 = new NumericalConfig();
                    config4.setName("ClaimPeriodDays");
                    config4.setValue(7.0);
                    config4.setType(NumConfigType.SYSTEM_SETTING);
                    configService.addNumericalConfig(config4);
                    LOGGER.info("New NumericalConfig entity created - Claim period set to 7 days");
                    System.out.println("   * New NumericalConfig entity created - Claim period set to 7 days");
                    NumericalConfig config5 = new NumericalConfig();
                    config5.setName("EarlyTerminationPenalty");
                    config5.setValue(0.0);
                    config5.setType(NumConfigType.SYSTEM_SETTING);
                    configService.addNumericalConfig(config5);
                    LOGGER.info("New NumericalConfig entity created - Early termination penalty set to 0%");
                    System.out.println("   * New NumericalConfig entity created - Early termination penalty set to 0%");
                    NumericalConfig config6 = new NumericalConfig();
                    config6.setName("PaymentPeriodDays");
                    config6.setValue(8.0);
                    config6.setType(NumConfigType.SYSTEM_SETTING);
                    configService.addNumericalConfig(config6);
                    LOGGER.info("Tenant payment period set to 8 days before arrival");
                    System.out.println("   * Tenant payment period set to 8 days before arrival");
                    NumericalConfig config7 = new NumericalConfig();
                    config7.setName("SystemInterestRate");
                    config7.setValue(10.0);
                    config7.setType(NumConfigType.SYSTEM_SETTING);
                    configService.addNumericalConfig(config7);
                    LOGGER.info("System interest rate set to 10%");
                    System.out.println("   * System interest rate set to 10%");
                    NumericalConfig config8 = new NumericalConfig();
                    config8.setName("LateCancellationPeriodInDays");
                    config8.setValue(10.0);
                    config8.setType(NumConfigType.SYSTEM_SETTING);
                    configService.addNumericalConfig(config8);
                    LOGGER.info("Late cancellation period set to 10 days");
                    System.out.println("   * Late cancellation period set to 10 days");
                    NumericalConfig config9 = new NumericalConfig();
                    config9.setName("UrgentCancellationPeriodInDays");
                    config9.setValue(3.0);
                    config9.setType(NumConfigType.SYSTEM_SETTING);
                    configService.addNumericalConfig(config9);
                    LOGGER.info("Urgent cancellation period set to 3 days");
                    System.out.println("   * Urgent cancellation period set to 3 days");
                    NumericalConfig config10 = new NumericalConfig();
                    config10.setName("UrgentCancellationPenalty");
                    config10.setValue(50.0);
                    config10.setType(NumConfigType.SYSTEM_SETTING);
                    configService.addNumericalConfig(config10);
                    LOGGER.info("Urgent cancellation penalty set to 50%");
                    System.out.println("   * Urgent cancellation penalty set to 50%");
                    NumericalConfig config11 = new NumericalConfig();
                    config11.setName("LateCancellationPenalty");
                    config11.setValue(25.0);
                    config11.setType(NumConfigType.SYSTEM_SETTING);
                    configService.addNumericalConfig(config11);
                    LOGGER.info("Late cancellation penalty set to 25%");
                    System.out.println("   * Late cancellation penalty set to 25%");
                    NumericalConfig config12 = new NumericalConfig();
                    config12.setName("RegularCancellationPenalty");
                    config12.setValue(0.0);
                    config12.setType(NumConfigType.SYSTEM_SETTING);
                    configService.addNumericalConfig(config12);
                    LOGGER.info("Regular cancellation penalty set to 0%");
                    System.out.println("   * Regular cancellation penalty set to 0%");

                    // Adding Managers
                    System.out.println();
                    System.out.println(" *** Creating Managers...");

                    Manager manager1 = new Manager();
                    manager1.setId(1L);
                    manager1.setManagerName("Sidney Nettleson");
                    manager1.setType(ManagerType.PRIVATE);
                    manager1.setPhone("+37167120000");
                    manager1.setEmail("propman_testmail1@inbox.lv");
                    manager1.setIban("DE89370400440532013000");
                    manager1.setLogin("SNettleson");
                    manager1.setDescription("Dedicated to managing your comfort to perfection");
//                    System.out.println("    -----     This is probably where the problem starts");
                    try {
                        manager1.setPaymentCardNo(encryptCardNumber(manager1.getId(), UserType.MANAGER, "4908474399435405"));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    manager1.setCardValidityDate(YearMonth.of(2025, 12));
                    try {
                        manager1.setCvv(encryptCVV(manager1.getId(), UserType.MANAGER, new char[]{9, 0, 6}).toCharArray());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    String encodedPassword = passwordEncoder.encode("Ductus");
                    manager1.setPassword(encodedPassword);
//                    System.out.println(" ??? Set a Password for Manager 1");
                    manager1.setActive(true);
//                    System.out.println(" ??? Set active status for Manager 1");
                    manager1.setJoinDate(Timestamp.valueOf(LocalDateTime.now()));
//                    System.out.println(" ??? Set join time for Manager 1");
                    manager1.setConfirmationToken("");
//                    System.out.println(" ??? Set confirmation token for Manager 1");
                    List<String> knownIPs = new ArrayList<>();
                    manager1.setKnownIps(knownIPs);
//                    System.out.println(" ??? Set known IPs for Manager 1");
                    manager1.setExpirationTime(LocalDateTime.now());
//                    System.out.println(" ??? Set expiration time for Manager 1");
//                    managerService.addManager(manager1);
//                    System.out.println(" ??? Added Manager 1 to the database");
                    List<GrantedAuthority> authoritiesM1 = new ArrayList<>();
                    authoritiesM1.add(new SimpleGrantedAuthority("MANAGER"));
                    manager1.setAuthorities(authoritiesM1);
                    System.out.println("   * Manager created: ID" + manager1.getId() + ", name: " + manager1.getManagerName() + ", Login: SNettleson, Password: Ductus");

                    Manager manager2 = new Manager();
                    manager2.setId(2L);
                    manager2.setManagerName("Stewart Ullman");
                    manager2.setType(ManagerType.CORPORATE);
                    manager2.setPhone("+37167111001");
                    manager2.setEmail("propman_testmail1@inbox.lv");
                    manager2.setIban("US82WEST12345698765432");
                    manager2.setLogin("SUllman");
                    manager2.setDescription("Proud of managing the matters of our highly esteemed guests on behalf of the majestic Overlook Hotel");
                    try {
                        manager2.setPaymentCardNo(encryptCardNumber(manager2.getId(), UserType.MANAGER, "5573672009088222"));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    manager2.setCardValidityDate(YearMonth.of(2024, 7));
                    try {
                        manager2.setCvv(encryptCVV(manager2.getId(), UserType.MANAGER, new char[]{5, 3, 3}).toCharArray());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    encodedPassword = passwordEncoder.encode("HoraceDerwent");
                    manager2.setPassword(encodedPassword);
                    manager2.setActive(true);
                    manager2.setJoinDate(Timestamp.valueOf(LocalDateTime.now()));
                    manager2.setConfirmationToken("");
                    manager2.setKnownIps(knownIPs);
                    manager2.setExpirationTime(LocalDateTime.now());
//                    managerService.addManager(manager2);
                    List<GrantedAuthority> authoritiesM2 = new ArrayList<>();
                    authoritiesM2.add(new SimpleGrantedAuthority("MANAGER"));
                    manager2.setAuthorities(authoritiesM2);
                    System.out.println("   * Manager created: ID" + manager2.getId() + ", name: " + manager2.getManagerName() + ", Login: SUllman, Password: HoraceDerwent");

                    Manager manager3 = new Manager();
                    manager3.setId(3L);
                    manager3.setManagerName("Christiane Paul");
                    manager3.setType(ManagerType.PRIVATE);
                    manager3.setPhone("+491514831975");
                    manager3.setEmail("propman_testmail1@inbox.lv");
                    manager3.setIban("FR1420041010050500013M02606");
                    manager3.setLogin("CPaul");
                    manager3.setDescription("Once the world's most attractive shop assistant, Christiane has brought her timeless charm into the real property domain");
                    try {
                        manager3.setPaymentCardNo(encryptCardNumber(manager3.getId(), UserType.MANAGER, "4921817336104919"));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    manager3.setCardValidityDate(YearMonth.of(2025, 8));
                    try {
                        manager3.setCvv(encryptCVV(manager3.getId(), UserType.MANAGER, new char[]{0, 1, 2}).toCharArray());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    encodedPassword = passwordEncoder.encode("HeavensDoor");
                    manager3.setPassword(encodedPassword);
                    manager3.setActive(true);
                    manager3.setJoinDate(Timestamp.valueOf(LocalDateTime.now()));
                    manager3.setConfirmationToken("");
                    manager3.setKnownIps(knownIPs);
                    manager3.setExpirationTime(LocalDateTime.now());
                    List<GrantedAuthority> authoritiesM3 = new ArrayList<>();
                    authoritiesM3.add(new SimpleGrantedAuthority("MANAGER"));
                    manager3.setAuthorities(authoritiesM3);
//                    managerService.addManager(manager3);
                    System.out.println("   * Manager created: ID" + manager3.getId() + ", name: " + manager3.getManagerName() + ", Login: CPaul, Password: HeavensDoor");

                    Manager manager4 = new Manager();
                    manager4.setId(4L);
                    manager4.setManagerName("Gary Golden");
                    manager4.setType(ManagerType.PRIVATE);
                    manager4.setPhone("+1800667337288");
                    manager4.setEmail("propman_testmail1@inbox.lv");
                    manager4.setIban("US9121000418450200051332");
                    manager4.setLogin("GGolden");
                    manager4.setDescription("Your friendly property manager Gary here! I'm always there for you, even when you don't see me");
                    try {
                        manager4.setPaymentCardNo(encryptCardNumber(manager4.getId(), UserType.MANAGER, "5573672000412900"));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    manager4.setCardValidityDate(YearMonth.of(2025, 2));
                    try {
                        manager4.setCvv(encryptCVV(manager4.getId(), UserType.MANAGER, new char[]{8, 5, 0}).toCharArray());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    encodedPassword = passwordEncoder.encode("Nosferatu");
                    manager4.setPassword(encodedPassword);
                    manager4.setActive(true);
                    manager4.setJoinDate(Timestamp.valueOf(LocalDateTime.now()));
                    manager4.setConfirmationToken("");
                    manager4.setKnownIps(knownIPs);
                    manager4.setExpirationTime(LocalDateTime.now());
//                    managerService.addManager(manager4);
                    List<GrantedAuthority> authoritiesM4 = new ArrayList<>();
                    authoritiesM4.add(new SimpleGrantedAuthority("MANAGER"));
                    manager4.setAuthorities(authoritiesM4);
                    System.out.println("   * Manager created: ID" + manager4.getId() + ", name: " + manager4.getManagerName() + ", Login: GGolden, Password: Nosferatu");

                    Manager manager5 = new Manager();
                    manager5.setId(5L);
                    manager5.setManagerName("Chutimon Lita");
                    manager5.setType(ManagerType.CORPORATE);
                    manager5.setPhone("+221863799045");
                    manager5.setEmail("propman_testmail1@inbox.lv");
                    manager5.setIban("NL91ABNA0417164300");
                    manager5.setLogin("LitaC");
                    manager5.setDescription("Welcoming you in the sunny lands on behalf of Thai Sabai Farang Properties!");
                    try {
                        manager5.setPaymentCardNo(encryptCardNumber(manager5.getId(), UserType.MANAGER, "4908474399439405"));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    manager5.setCardValidityDate(YearMonth.of(2025, 4));
                    try {
                        manager5.setCvv(encryptCVV(manager5.getId(), UserType.MANAGER, new char[]{7, 8, 9}).toCharArray());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    encodedPassword = passwordEncoder.encode("SanookMakMak");
                    manager5.setPassword(encodedPassword);
                    manager5.setActive(true);
                    manager5.setJoinDate(Timestamp.valueOf(LocalDateTime.now()));
                    manager5.setConfirmationToken("");
                    manager5.setKnownIps(knownIPs);
                    manager5.setExpirationTime(LocalDateTime.now());
//                    managerService.addManager(manager5);
                    List<GrantedAuthority> authoritiesM5 = new ArrayList<>();
                    authoritiesM5.add(new SimpleGrantedAuthority("MANAGER"));
                    manager5.setAuthorities(authoritiesM5);
                    System.out.println("   * Manager created: ID" + manager5.getId() + ", name: " + manager5.getManagerName() + ", Login: LitaC, Password: SanookMakMak");

                    // Adding Bills
                    System.out.println();
                    System.out.println(" *** Creating Bills...");

                    List<Currency> currencies = currencyService.getAllCurrencies();

                    List<Bill> sampleBills = new ArrayList<>();

                    // Sample data for Bills
                    Bill bill1 = new Bill();
//                    System.out.println("     --  Initiated bill1");
                    bill1.setId(1L);
//                    System.out.println("     --  Set ID for bill1");
                    bill1.setAmount(200.00);
//                    System.out.println("     --  Set Amount for bill1");
                    bill1.setCurrency(currency1);
//                    System.out.println("     --  Set Currency for bill1");
                    bill1.setProperty(property1);
//                    System.out.println("     --  Set Property for bill1");
                    bill1.setExpenseCategory("Electricity");
//                    System.out.println("     --  Set Category for bill1");
                    bill1.setPaid(false);
//                    System.out.println("     --  Set PaidStatus for bill1");
                    bill1.setIssuedAt(Timestamp.valueOf("2024-06-01 00:00:00"));
//                    System.out.println("     --  Set IssuedAt for bill1");
                    bill1.setAddedAt(Timestamp.valueOf("2024-06-02 13:00:00"));
//                    System.out.println("     --  Set AddedAt for bill1");
                    bill1.setRecipient("Electric Company");
//                    System.out.println("     --  Set Recipient for bill1");
                    bill1.setRecipientIBAN("IBAN1234567890");
//                    System.out.println("     --  Set IBAN for bill1");
                    bill1.setDueDate(Timestamp.valueOf("2024-07-02 00:00:00"));
//                    System.out.println("     --  Set DueDate for bill1");
//                    billService.addBill(bill1);
//                    System.out.println("     --  Added bill1 to the database");
                    System.out.println("   * Created Bill1 - not added to the database yet at this stage");


                    Bill bill2 = new Bill();
                    bill2.setId(2L);
                    bill2.setAmount(150.00);
                    bill2.setCurrency(currency2);
                    bill2.setProperty(property2);
                    bill2.setExpenseCategory("Water");
                    bill2.setPaid(false);
                    bill2.setIssuedAt(Timestamp.valueOf("2024-06-05 00:00:00"));
                    bill2.setAddedAt(Timestamp.valueOf("2024-06-05 00:00:00"));
                    bill2.setRecipient("Water Supplier");
                    bill2.setRecipientIBAN("IBAN2345678901");
                    bill2.setDueDate(Timestamp.valueOf("2024-07-05 00:00:00"));
//                    billService.addBill(bill2);
                    System.out.println("   * Created Bill2 - not added to the database yet at this stage");

                    Bill bill3 = new Bill();
                    bill3.setId(3L);
                    bill3.setAmount(300.00);
                    bill3.setCurrency(currencies.get(2));
                    bill3.setProperty(property3);
                    bill3.setExpenseCategory("Internet");
                    bill3.setPaid(false);
                    bill3.setIssuedAt(Timestamp.valueOf("2024-06-10 00:00:00"));
                    bill3.setAddedAt(Timestamp.valueOf("2024-06-10 00:00:00"));
                    bill3.setRecipient("Internet Provider");
                    bill3.setRecipientIBAN("IBAN3456789012");
                    bill3.setDueDate(Timestamp.valueOf("2024-07-10 00:00:00"));
//                    billService.addBill(bill3);
                    System.out.println("   * Created Bill3 - not added to the database yet at this stage");

                    Bill bill4 = new Bill();
                    bill4.setId(4L);
                    bill4.setAmount(400.00);
                    bill4.setCurrency(currencies.get(3));
                    bill4.setProperty(property4);
                    bill4.setExpenseCategory("Maintenance");
                    bill4.setPaid(false);
                    bill4.setIssuedAt(Timestamp.valueOf("2024-06-15 00:00:00"));
                    bill4.setAddedAt(Timestamp.valueOf("2024-06-15 00:00:00"));
                    bill4.setRecipient("Maintenance Company");
                    bill4.setRecipientIBAN("IBAN4567890123");
                    bill4.setDueDate(Timestamp.valueOf("2024-07-15 00:00:00"));
//                    billService.addBill(bill4);
                    System.out.println("   * Created Bill4 - not added to the database yet at this stage");

                    Bill bill5 = new Bill();
                    bill5.setId(5L);
                    bill5.setAmount(250.00);
                    bill5.setCurrency(currencies.get(0));
                    bill5.setProperty(property5);
                    bill5.setExpenseCategory("Gas");
                    bill5.setPaid(false);
                    bill5.setIssuedAt(Timestamp.valueOf("2024-06-20 00:00:00"));
                    bill5.setAddedAt(Timestamp.valueOf("2024-06-20 00:00:00"));
                    bill5.setRecipient("Gas Company");
                    bill5.setRecipientIBAN("IBAN5678901234");
                    bill5.setDueDate(Timestamp.valueOf("2024-07-20 00:00:00"));
//                    billService.addBill(bill5);
                    System.out.println("   * Created Bill5 - not added to the database yet at this stage");

                    Bill bill6 = new Bill();
                    bill6.setId(6L);
                    bill6.setAmount(100.00);
                    bill6.setCurrency(currencies.get(1));
                    bill6.setProperty(property6);
                    bill6.setExpenseCategory("Garbage");
                    bill6.setPaid(false);
                    bill6.setIssuedAt(Timestamp.valueOf("2024-06-25 00:00:00"));
                    bill6.setAddedAt(Timestamp.valueOf("2024-06-25 00:00:00"));
                    bill6.setRecipient("Garbage Collector");
                    bill6.setRecipientIBAN("IBAN6789012345");
                    bill6.setDueDate(Timestamp.valueOf("2024-07-25 00:00:00"));
//                    billService.addBill(bill6);
                    System.out.println("   * Created Bill6 - not added to the database yet at this stage");

                    Bill bill7 = new Bill();
                    bill7.setId(7L);
                    bill7.setAmount(600.00);
                    bill7.setCurrency(currencies.get(2));
                    bill7.setProperty(property7);
                    bill7.setExpenseCategory("Rent");
                    bill7.setPaid(false);
                    bill7.setIssuedAt(Timestamp.valueOf("2024-06-30 00:00:00"));
                    bill7.setAddedAt(Timestamp.valueOf("2024-06-30 00:00:00"));
                    bill7.setRecipient("Landlord");
                    bill7.setRecipientIBAN("IBAN7890123456");
                    bill7.setDueDate(Timestamp.valueOf("2024-07-30 00:00:00"));
//                    billService.addBill(bill7);
                    System.out.println("   * Created Bill7 - not added to the database yet at this stage");

                    Bill bill8 = new Bill();
                    bill8.setId(8L);
                    bill8.setAmount(50.00);
                    bill8.setCurrency(currencies.get(3));
                    bill8.setProperty(property8);
                    bill8.setExpenseCategory("Parking");
                    bill8.setPaid(false);
                    bill8.setIssuedAt(Timestamp.valueOf("2024-07-01 00:00:00"));
                    bill8.setAddedAt(Timestamp.valueOf("2024-07-01 00:00:00"));
                    bill8.setRecipient("Parking Service");
                    bill8.setRecipientIBAN("IBAN8901234567");
                    bill8.setDueDate(Timestamp.valueOf("2024-08-01 00:00:00"));
//                    billService.addBill(bill8);
                    System.out.println("   * Created Bill8 - not added to the database yet at this stage");

                    Bill bill9 = new Bill();
                    bill9.setId(9L);
                    bill9.setAmount(80.00);
                    bill9.setCurrency(currencies.get(0));
                    bill9.setProperty(property9);
                    bill9.setExpenseCategory("Cleaning");
                    bill9.setPaid(false);
                    bill9.setIssuedAt(Timestamp.valueOf("2024-07-05 00:00:00"));
                    bill9.setAddedAt(Timestamp.valueOf("2024-07-05 00:00:00"));
                    bill9.setRecipient("Cleaning Service");
                    bill9.setRecipientIBAN("IBAN9012345678");
                    bill9.setDueDate(Timestamp.valueOf("2024-08-05 00:00:00"));
//                    billService.addBill(bill9);
                    System.out.println("   * Created Bill9 - not added to the database yet at this stage");

                    Bill bill10 = new Bill();
                    bill10.setId(10L);
                    bill10.setAmount(500.00);
                    bill10.setCurrency(currencies.get(1));
                    bill10.setProperty(property10);
                    bill10.setExpenseCategory("Insurance");
                    bill10.setPaid(false);
                    bill10.setIssuedAt(Timestamp.valueOf("2024-07-10 00:00:00"));
                    bill10.setAddedAt(Timestamp.valueOf("2024-07-10 00:00:00"));
                    bill10.setRecipient("Insurance Company");
                    bill10.setRecipientIBAN("IBAN0123456789");
                    bill10.setDueDate(Timestamp.valueOf("2024-08-10 00:00:00"));
//                    billService.addBill(bill10);
                    System.out.println("   * Created Bill10 - not added to the database yet at this stage");

                    Bill bill11 = new Bill();
                    bill11.setId(11L);
                    bill11.setAmount(120.00);
                    bill11.setCurrency(currencies.get(2));
                    bill11.setProperty(property11);
                    bill11.setExpenseCategory("Water");
                    bill11.setPaid(false);
                    bill11.setIssuedAt(Timestamp.valueOf("2024-07-15 00:00:00"));
                    bill11.setAddedAt(Timestamp.valueOf("2024-07-15 00:00:00"));
                    bill11.setRecipient("Water Supplier");
                    bill11.setRecipientIBAN("IBAN1234567890");
                    bill11.setDueDate(Timestamp.valueOf("2024-08-15 00:00:00"));
//                    billService.addBill(bill11);
                    System.out.println("   * Created Bill11 - not added to the database yet at this stage");

                    Bill bill12 = new Bill();
                    bill12.setId(12L);
                    bill12.setAmount(220.00);
                    bill12.setCurrency(currencies.get(3));
                    bill12.setProperty(property12);
                    bill12.setExpenseCategory("Electricity");
                    bill12.setPaid(false);
                    bill12.setIssuedAt(Timestamp.valueOf("2024-07-20 00:00:00"));
                    bill12.setAddedAt(Timestamp.valueOf("2024-07-20 00:00:00"));
                    bill12.setRecipient("Electric Company");
                    bill12.setRecipientIBAN("IBAN2345678901");
                    bill12.setDueDate(Timestamp.valueOf("2024-08-20 00:00:00"));
//                    billService.addBill(bill12);
                    System.out.println("   * Created Bill12 - not added to the database yet at this stage");

//                    billService.addBill(new Bill(1L, 200.00, currencies.get(0), property1, "Electricity",
//                            Timestamp.valueOf("2024-07-01 00:00:00"), "Electric Company", "IBAN1234567890", false,
//                            Timestamp.valueOf("2024-06-01 00:00:00"), Timestamp.valueOf("2024-06-01 00:00:00")));
//                    billService.addBill(new Bill(2L, 150.00, currencies.get(1), property2, "Water",
//                            Timestamp.valueOf("2024-07-05 00:00:00"), "Water Supplier", "IBAN2345678901", false,
//                            Timestamp.valueOf("2024-06-05 00:00:00"), Timestamp.valueOf("2024-06-05 00:00:00")));
//                    billService.addBill(new Bill(3L, 300.00, currencies.get(2), property3, "Internet",
//                            Timestamp.valueOf("2024-07-10 00:00:00"), "Internet Provider", "IBAN3456789012", false,
//                            Timestamp.valueOf("2024-06-10 00:00:00"), Timestamp.valueOf("2024-06-10 00:00:00")));
//                    billService.addBill(new Bill(4L, 400.00, currencies.get(3), property4, "Maintenance",
//                            Timestamp.valueOf("2024-07-15 00:00:00"), "Maintenance Company", "IBAN4567890123", false,
//                            Timestamp.valueOf("2024-06-15 00:00:00"), Timestamp.valueOf("2024-06-15 00:00:00")));
//                    billService.addBill(new Bill(5L, 250.00, currencies.get(0), property5, "Gas",
//                            Timestamp.valueOf("2024-07-20 00:00:00"), "Gas Company", "IBAN5678901234", false,
//                            Timestamp.valueOf("2024-06-20 00:00:00"), Timestamp.valueOf("2024-06-20 00:00:00")));
//                    billService.addBill(new Bill(6L, 100.00, currencies.get(1), property6, "Garbage",
//                            Timestamp.valueOf("2024-07-25 00:00:00"), "Garbage Collector", "IBAN6789012345", false,
//                            Timestamp.valueOf("2024-06-25 00:00:00"), Timestamp.valueOf("2024-06-25 00:00:00")));
//                    billService.addBill(new Bill(7L, 600.00, currencies.get(2), property7, "Rent",
//                            Timestamp.valueOf("2024-07-30 00:00:00"), "Landlord", "IBAN7890123456", false,
//                            Timestamp.valueOf("2024-06-30 00:00:00"), Timestamp.valueOf("2024-06-30 00:00:00")));
//                    billService.addBill(new Bill(8L, 50.00, currencies.get(3), property8, "Parking",
//                            Timestamp.valueOf("2024-08-01 00:00:00"), "Parking Service", "IBAN8901234567", false,
//                            Timestamp.valueOf("2024-07-01 00:00:00"), Timestamp.valueOf("2024-07-01 00:00:00")));
//                    billService.addBill(new Bill(9L, 80.00, currencies.get(0), property9, "Cleaning",
//                            Timestamp.valueOf("2024-08-05 00:00:00"), "Cleaning Service", "IBAN9012345678", false,
//                            Timestamp.valueOf("2024-07-05 00:00:00"), Timestamp.valueOf("2024-07-05 00:00:00")));
//                    billService.addBill(new Bill(10L, 500.00, currencies.get(1), property10, "Insurance",
//                            Timestamp.valueOf("2024-08-10 00:00:00"), "Insurance Company", "IBAN0123456789", false,
//                            Timestamp.valueOf("2024-07-10 00:00:00"), Timestamp.valueOf("2024-07-10 00:00:00")));
//                    billService.addBill(new Bill(11L, 120.00, currencies.get(2), property11, "Water",
//                            Timestamp.valueOf("2024-08-15 00:00:00"), "Water Supplier", "IBAN1234567890", false,
//                            Timestamp.valueOf("2024-07-15 00:00:00"), Timestamp.valueOf("2024-07-15 00:00:00")));
//                    billService.addBill(new Bill(12L, 220.00, currencies.get(3), property12, "Electricity",
//                            Timestamp.valueOf("2024-08-20 00:00:00"), "Electric Company", "IBAN2345678901", false,
//                            Timestamp.valueOf("2024-07-20 00:00:00"), Timestamp.valueOf("2024-07-20 00:00:00")));

//                    List<Property> premadeProperties = new ArrayList<>();
//                    premadeProperties.add(property1);
//                    premadeProperties.add(property2);
//                    premadeProperties.add(property3);
//                    premadeProperties.add(property4);
//                    premadeProperties.add(property5);
//                    premadeProperties.add(property6);
//                    premadeProperties.add(property7);
//                    premadeProperties.add(property8);
//                    premadeProperties.add(property9);
//                    premadeProperties.add(property10);
//                    premadeProperties.add(property11);
//                    premadeProperties.add(property12);
//                    int propertyIndexForBills = 0;
//                    for (Bill bill : sampleBills) {
//                        Set<Bill> billset = new HashSet<>();
//                        billset.add(bill);
//                        premadeProperties.get(propertyIndexForBills).setBills(billset);
//                        System.out.println("   * Bill created: ID" + bill.getId() + ", category: " + bill.getExpenseCategory() + ", for Property " + bill.getProperty().getId());
//                        propertyIndexForBills++;
//                    }


                    // Adding Tenants
                    System.out.println();
                    System.out.println(" *** Creating Tenants...");

                    Tenant tenant1 = new Tenant();
                    tenant1.setId(1L);
//                    System.out.println("Tenant 1 ID is now " + tenant1.getId());
                    tenant1.setFirstName("Kenny");
//                    System.out.println("Tenant 1 first name is now " + tenant1.getFirstName());
                    tenant1.setLastName("McCormick");
//                    System.out.println("Tenant 1 last name is now " + tenant1.getLastName());
                    tenant1.setPhone("+1800111111");
//                    System.out.println("Tenant 1 phone number is now " + tenant1.getPhone());
                    tenant1.setEmail("propman_testmail1@inbox.lv");
//                    System.out.println("Tenant 1 email is now " + tenant1.getEmail());
                    tenant1.setIban("FR1420041010050500013M02606");
//                    System.out.println("Tenant 1 IBAN is now " + tenant1.getIban());
                    tenant1.setLogin("KennyM");
//                    System.out.println("Tenant 1 login is now " + tenant1.getLogin());
                    encodedPassword = passwordEncoder.encode("CheatingDeath");
                    tenant1.setPassword(encodedPassword);
//                    System.out.println("Tenant 1 password is now " + tenant1.getPassword());
                    tenant1.setRating(0F);
//                    System.out.println("Tenant 1 rating is now " + tenant1.getRating());
                    tenant1.setActive(true);
//                    System.out.println("Tenant 1 active status is now " + tenant1.isActive());
                    tenant1.setTenantPayments(new HashSet<>());
//                    System.out.println("Tenant 1 payments list is now " + tenant1.getTenantPayments().toString());
                    tenant1.setCurrentProperty(null);
//                    System.out.println("Set current property for Tenant 1 to 0");
                    tenant1.setLeasingHistories(new ArrayList<>());
//                    System.out.println("Tenant 1 leasing histories list is now " + tenant1.getLeasingHistories().toString());
                    try {
                        tenant1.setPaymentCardNo(encryptCardNumber(tenant1.getId(), UserType.TENANT, "5573672009088222"));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
//                    System.out.println("Tenant 1 card number is now " + tenant1.getPaymentCardNo());
                    tenant1.setCardValidityDate(YearMonth.of(2026, 3));
//                    System.out.println("Tenant 1 card validity date is now " + tenant1.getCardValidityDate().toString());
                    try {
                        tenant1.setCvv(encryptCVV(tenant1.getId(), UserType.TENANT, new char[]{6, 6, 6}).toCharArray());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
//                    System.out.println("Tenant 1 CVV is now " + Arrays.toString(tenant1.getCvv()));
                    tenant1.setConfirmationToken("");
//                    System.out.println("Tenant 1 confirmation token is now " + tenant1.getConfirmationToken());
                    tenant1.setPreferredCurrency(currencyService.returnBaseCurrency());
//                    System.out.println("Tenant 1 preferred currency is now " + tenant1.getPreferredCurrency().getDesignation());
                    knownIPs = new ArrayList<>();
                    tenant1.setKnownIps(knownIPs);
//                    System.out.println("Tenant 1 known IPs list is now " + tenant1.getKnownIps().toString());
                    tenant1.setExpirationTime(LocalDateTime.now());
//                    System.out.println("Tenant 1 token expiration time is now " + tenant1.getExpirationTime());
                    List<GrantedAuthority> authoritiesT = new ArrayList<>();
                    authoritiesT.add(new SimpleGrantedAuthority("TENANT"));
                    tenant1.setAuthorities(authoritiesT);
                    tenantService.addTenant(tenant1);
                    System.out.println("   * New tenant added to the database: ID" + tenant1.getId() + ", First name / surname: " + tenant1.getFirstName() + " " + tenant1.getLastName());
                    LOGGER.info("New tenant added to the database: ID" + tenant1.getId() + ", First name / surname: " + tenant1.getFirstName() + " " + tenant1.getLastName());

                    Tenant tenant2 = new Tenant();
                    tenant2.setId(2L);
                    tenant2.setFirstName("Stan");
                    tenant2.setLastName("Marsh");
                    tenant2.setPhone("+1800222222");
                    tenant2.setEmail("propman_testmail1@inbox.lv");
                    tenant2.setIban("FR7630006000011234567890189");
                    tenant2.setLogin("StanM");
                    encodedPassword = passwordEncoder.encode("PinewoodDerby");
                    tenant2.setPassword(encodedPassword);
                    tenant2.setRating(0F);
                    tenant2.setActive(true);
                    tenant2.setTenantPayments(new HashSet<>());
                    tenant2.setCurrentProperty(null);
                    tenant2.setLeasingHistories(new ArrayList<>());
                    try {
                        tenant2.setPaymentCardNo(encryptCardNumber(tenant2.getId(), UserType.TENANT, "4532421174341271"));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    tenant2.setCardValidityDate(YearMonth.of(2025, 10));
                    try {
                        tenant2.setCvv(encryptCVV(tenant2.getId(), UserType.TENANT, new char[]{7, 7, 7}).toCharArray());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    tenant2.setConfirmationToken("");
                    tenant2.setPreferredCurrency(currencyService.returnBaseCurrency());
                    knownIPs = new ArrayList<>();
                    tenant2.setKnownIps(knownIPs);
                    tenant2.setExpirationTime(LocalDateTime.now());
                    tenant2.setAuthorities(authoritiesT);
                    tenantService.addTenant(tenant2);
                    System.out.println("   * New tenant added to the database: ID" + tenant2.getId() + ", First name / surname: " + tenant2.getFirstName() + " " + tenant2.getLastName());
                    LOGGER.info("New tenant added to the database: ID" + tenant2.getId() + ", First name / surname: " + tenant2.getFirstName() + " " + tenant2.getLastName());

                    Tenant tenant3 = new Tenant();
                    tenant3.setId(3L);
                    tenant3.setFirstName("Kyle");
                    tenant3.setLastName("Broflowski");
                    tenant3.setPhone("+1800333333");
                    tenant3.setEmail("propman_testmail1@inbox.lv");
                    tenant3.setIban("GB33BUKB20201555555555");
                    tenant3.setLogin("KyleB");
                    encodedPassword = passwordEncoder.encode("Jewpacabra");
                    tenant3.setPassword(encodedPassword);
                    tenant3.setRating(0F);
                    tenant3.setActive(true);
                    tenant3.setTenantPayments(new HashSet<>());
                    tenant3.setCurrentProperty(null);
                    tenant3.setLeasingHistories(new ArrayList<>());
                    try {
                        tenant3.setPaymentCardNo(encryptCardNumber(tenant3.getId(), UserType.TENANT, "4929875080372167"));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    tenant3.setCardValidityDate(YearMonth.of(2024, 12));
                    try {
                        tenant3.setCvv(encryptCVV(tenant3.getId(), UserType.TENANT, new char[]{8, 8, 8}).toCharArray());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    tenant3.setConfirmationToken("");
                    tenant3.setPreferredCurrency(currencyService.returnBaseCurrency());
                    knownIPs = new ArrayList<>();
                    tenant3.setKnownIps(knownIPs);
                    tenant3.setExpirationTime(LocalDateTime.now());
                    tenant3.setAuthorities(authoritiesT);
                    tenantService.addTenant(tenant3);
                    System.out.println("   * New tenant added to the database: ID" + tenant3.getId() + ", First name / surname: " + tenant3.getFirstName() + " " + tenant3.getLastName());
                    LOGGER.info("New tenant added to the database: ID" + tenant3.getId() + ", First name / surname: " + tenant3.getFirstName() + " " + tenant3.getLastName());

                    Tenant tenant4 = new Tenant();
                    tenant4.setId(4L);
                    tenant4.setFirstName("Eric");
                    tenant4.setLastName("Cartman");
                    tenant4.setPhone("+1800444444");
                    tenant4.setEmail("propman_testmail1@inbox.lv");
                    tenant4.setIban("DE89370400440532013000");
                    tenant4.setLogin("EricC");
                    encodedPassword = passwordEncoder.encode("RespectMyAuth");
                    tenant4.setPassword(encodedPassword);
                    tenant4.setRating(0F);
                    tenant4.setActive(true);
                    tenant4.setTenantPayments(new HashSet<>());
                    tenant4.setCurrentProperty(null);
                    tenant4.setLeasingHistories(new ArrayList<>());
                    try {
                        tenant4.setPaymentCardNo(encryptCardNumber(tenant4.getId(), UserType.TENANT, "4532650411587694"));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    tenant4.setCardValidityDate(YearMonth.of(2025, 6));
                    try {
                        tenant4.setCvv(encryptCVV(tenant4.getId(), UserType.TENANT, new char[]{9, 9, 9}).toCharArray());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    tenant4.setConfirmationToken("");
                    tenant4.setPreferredCurrency(currencyService.returnBaseCurrency());
                    knownIPs = new ArrayList<>();
                    tenant4.setKnownIps(knownIPs);
                    tenant4.setExpirationTime(LocalDateTime.now());
                    tenant4.setAuthorities(authoritiesT);
                    tenantService.addTenant(tenant4);
                    System.out.println("   * New tenant added to the database: ID" + tenant4.getId() + ", First name / surname: " + tenant4.getFirstName() + " " + tenant4.getLastName());
                    LOGGER.info("New tenant added to the database: ID" + tenant4.getId() + ", First name / surname: " + tenant4.getFirstName() + " " + tenant4.getLastName());

                    Tenant tenant5 = new Tenant();
                    tenant5.setId(5L);
                    tenant5.setFirstName("Wendy");
                    tenant5.setLastName("Testaburger");
                    tenant5.setPhone("+1800555555");
                    tenant5.setEmail("propman_testmail1@inbox.lv");
                    tenant5.setIban("NL39RABO0300065264");
                    tenant5.setLogin("WendyT");
                    encodedPassword = passwordEncoder.encode("ScienceRules");
                    tenant5.setPassword(encodedPassword);
                    tenant5.setRating(0F);
                    tenant5.setActive(true);
                    tenant5.setTenantPayments(new HashSet<>());
                    tenant5.setCurrentProperty(null);
                    tenant5.setLeasingHistories(new ArrayList<>());
                    try {
                        tenant5.setPaymentCardNo(encryptCardNumber(tenant5.getId(), UserType.TENANT, "4916300086767492"));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    tenant5.setCardValidityDate(YearMonth.of(2027, 8));
                    try {
                        tenant5.setCvv(encryptCVV(tenant5.getId(), UserType.TENANT, new char[]{2, 3, 5}).toCharArray());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    tenant5.setConfirmationToken("");
                    tenant5.setPreferredCurrency(currencyService.returnBaseCurrency());
                    knownIPs = new ArrayList<>();
                    tenant5.setKnownIps(knownIPs);
                    tenant5.setExpirationTime(LocalDateTime.now());
                    tenant5.setAuthorities(authoritiesT);
                    tenantService.addTenant(tenant5);
                    System.out.println("   * New tenant added to the database: ID" + tenant5.getId() + ", First name / surname: " + tenant5.getFirstName() + " " + tenant5.getLastName());
                    LOGGER.info("New tenant added to the database: ID" + tenant5.getId() + ", First name / surname: " + tenant5.getFirstName() + " " + tenant5.getLastName());

                    Tenant tenant6 = new Tenant();
                    tenant6.setId(6L);
                    tenant6.setFirstName("Butters");
                    tenant6.setLastName("Stotch");
                    tenant6.setPhone("+1800111115");
                    tenant6.setEmail("propman_testmail1@inbox.lv");
                    tenant6.setIban("BE71096123456769");
                    tenant6.setLogin("ButtersS");
                    encodedPassword = passwordEncoder.encode("AwGeeWhiz");
                    tenant6.setPassword(encodedPassword);
                    tenant6.setRating(0F);
                    tenant6.setActive(true);
                    tenant6.setTenantPayments(new HashSet<>());
                    tenant6.setCurrentProperty(null);
                    tenant6.setLeasingHistories(new ArrayList<>());
                    try {
                        tenant6.setPaymentCardNo(encryptCardNumber(tenant6.getId(), UserType.TENANT, "5152029829375563"));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    tenant6.setCardValidityDate(YearMonth.of(2028, 6));
                    try {
                        tenant6.setCvv(encryptCVV(tenant6.getId(), UserType.TENANT, new char[]{5, 5, 5}).toCharArray());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    tenant6.setConfirmationToken("");
                    tenant6.setPreferredCurrency(currencyService.returnBaseCurrency());
                    tenant6.setKnownIps(new ArrayList<>());
                    tenant6.setExpirationTime(LocalDateTime.now());
                    tenant6.setAuthorities(authoritiesT);
                    tenantService.addTenant(tenant6);
                    System.out.println("   * New tenant added to the database: ID" + tenant6.getId() + ", First name / surname: " + tenant6.getFirstName() + " " + tenant6.getLastName());
                    LOGGER.info("New tenant added to the database: ID" + tenant6.getId() + ", First name / surname: " + tenant6.getFirstName() + " " + tenant6.getLastName());

                    Tenant tenant7 = new Tenant();
                    tenant7.setId(7L);
                    tenant7.setFirstName("Tolkien");
                    tenant7.setLastName("Black");
                    tenant7.setPhone("+1800111116");
                    tenant7.setEmail("propman_testmail1@inbox.lv");
                    tenant7.setIban("BG80BNBG96611020345678");
                    tenant7.setLogin("TolkienB");
                    encodedPassword = passwordEncoder.encode("TokenOfHope");
                    tenant7.setPassword(encodedPassword);
                    tenant7.setRating(0F);
                    tenant7.setActive(true);
                    tenant7.setTenantPayments(new HashSet<>());
                    tenant7.setCurrentProperty(null);
                    tenant7.setLeasingHistories(new ArrayList<>());
                    try {
                        tenant7.setPaymentCardNo(encryptCardNumber(tenant7.getId(), UserType.TENANT, "5317141249889643"));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    tenant7.setCardValidityDate(YearMonth.of(2027, 9));
                    try {
                        tenant7.setCvv(encryptCVV(tenant7.getId(), UserType.TENANT, new char[]{3, 1, 5}).toCharArray());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    tenant7.setConfirmationToken("");
                    tenant7.setPreferredCurrency(currencyService.returnBaseCurrency());
                    tenant7.setKnownIps(new ArrayList<>());
                    tenant7.setExpirationTime(LocalDateTime.now());
                    tenant7.setAuthorities(authoritiesT);
                    tenantService.addTenant(tenant7);
                    System.out.println("   * New tenant added to the database: ID" + tenant7.getId() + ", First name / surname: " + tenant7.getFirstName() + " " + tenant7.getLastName());
                    LOGGER.info("New tenant added to the database: ID" + tenant7.getId() + ", First name / surname: " + tenant7.getFirstName() + " " + tenant7.getLastName());

                    Tenant tenant8 = new Tenant();
                    tenant8.setId(8L);
                    tenant8.setFirstName("Craig");
                    tenant8.setLastName("Tucker");
                    tenant8.setPhone("+1800111117");
                    tenant8.setEmail("propman_testmail1@inbox.lv");
                    tenant8.setIban("ES7921000813610123456789");
                    tenant8.setLogin("CraigT");
                    encodedPassword = passwordEncoder.encode("TouchTheSky");
                    tenant8.setPassword(encodedPassword);
                    tenant8.setRating(0F);
                    tenant8.setActive(true);
                    tenant8.setTenantPayments(new HashSet<>());
                    tenant8.setCurrentProperty(null);
                    tenant8.setLeasingHistories(new ArrayList<>());
                    try {
                        tenant8.setPaymentCardNo(encryptCardNumber(tenant8.getId(), UserType.TENANT, "5281588740347845"));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    tenant8.setCardValidityDate(YearMonth.of(2025, 12));
                    try {
                        tenant8.setCvv(encryptCVV(tenant8.getId(), UserType.TENANT, new char[]{7, 8, 9}).toCharArray());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    tenant8.setConfirmationToken("");
                    tenant8.setPreferredCurrency(currencyService.returnBaseCurrency());
                    tenant8.setKnownIps(new ArrayList<>());
                    tenant8.setExpirationTime(LocalDateTime.now());
                    tenant8.setAuthorities(authoritiesT);
                    tenantService.addTenant(tenant8);
                    System.out.println("   * New tenant added to the database: ID" + tenant8.getId() + ", First name / surname: " + tenant8.getFirstName() + " " + tenant8.getLastName());
                    LOGGER.info("New tenant added to the database: ID" + tenant8.getId() + ", First name / surname: " + tenant8.getFirstName() + " " + tenant8.getLastName());

                    Tenant tenant9 = new Tenant();
                    tenant9.setId(9L);
                    tenant9.setFirstName("Tweek");
                    tenant9.setLastName("Tweak");
                    tenant9.setPhone("+1800111118");
                    tenant9.setEmail("propman_testmail1@inbox.lv");
                    tenant9.setIban("IT60X0542811101000000123456");
                    tenant9.setLogin("TweekT");
                    encodedPassword = passwordEncoder.encode("StayAwake");
                    tenant9.setPassword(encodedPassword);
                    tenant9.setRating(0F);
                    tenant9.setActive(true);
                    tenant9.setTenantPayments(new HashSet<>());
                    tenant9.setCurrentProperty(null);
                    tenant9.setLeasingHistories(new ArrayList<>());
                    try {
                        tenant9.setPaymentCardNo(encryptCardNumber(tenant9.getId(), UserType.TENANT, "4532513983922493"));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    tenant9.setCardValidityDate(YearMonth.of(2024, 7));
                    try {
                        tenant9.setCvv(encryptCVV(tenant9.getId(), UserType.TENANT, new char[]{1, 2, 3}).toCharArray());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    tenant9.setConfirmationToken("");
                    tenant9.setPreferredCurrency(currencyService.returnBaseCurrency());
                    tenant9.setKnownIps(new ArrayList<>());
                    tenant9.setExpirationTime(LocalDateTime.now());
                    tenant9.setAuthorities(authoritiesT);
                    tenantService.addTenant(tenant9);
                    System.out.println("   * New tenant added to the database: ID" + tenant9.getId() + ", First name / surname: " + tenant9.getFirstName() + " " + tenant9.getLastName());
                    LOGGER.info("New tenant added to the database: ID" + tenant9.getId() + ", First name / surname: " + tenant9.getFirstName() + " " + tenant9.getLastName());

                    Tenant tenant10 = new Tenant();
                    tenant10.setId(10L);
                    tenant10.setFirstName("Gregory");
                    tenant10.setLastName("Yardale");
                    tenant10.setPhone("+1800111119");
                    tenant10.setEmail("propman_testmail1@inbox.lv");
                    tenant10.setIban("DE89370400440532013000");
                    tenant10.setLogin("GregoryY");
                    encodedPassword = passwordEncoder.encode("ClassyTouch");
                    tenant10.setPassword(encodedPassword);
                    tenant10.setRating(0F);
                    tenant10.setActive(true);
                    tenant10.setTenantPayments(new HashSet<>());
                    tenant10.setCurrentProperty(null);
                    tenant10.setLeasingHistories(new ArrayList<>());
                    try {
                        tenant10.setPaymentCardNo(encryptCardNumber(tenant10.getId(), UserType.TENANT, "6011111111111117"));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    tenant10.setCardValidityDate(YearMonth.of(2027, 8));
                    try {
                        tenant10.setCvv(encryptCVV(tenant10.getId(), UserType.TENANT, new char[]{3, 4, 5}).toCharArray());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    tenant10.setConfirmationToken("");
                    tenant10.setPreferredCurrency(currencyService.returnBaseCurrency());
                    tenant10.setKnownIps(new ArrayList<>());
                    tenant10.setExpirationTime(LocalDateTime.now());
                    tenant10.setAuthorities(authoritiesT);
                    tenantService.addTenant(tenant10);
                    System.out.println("   * New tenant added to the database: ID" + tenant10.getId() + ", First name / surname: " + tenant10.getFirstName() + " " + tenant10.getLastName());
                    LOGGER.info("New tenant added to the database: ID" + tenant10.getId() + ", First name / surname: " + tenant10.getFirstName() + " " + tenant10.getLastName());

                    Tenant tenant11 = new Tenant();
                    tenant11.setId(11L);
                    tenant11.setFirstName("Bebe");
                    tenant11.setLastName("Stevens");
                    tenant11.setPhone("+1800111120");
                    tenant11.setEmail("propman_testmail1@inbox.lv");
                    tenant11.setIban("FR7630006000011234567890189");
                    tenant11.setLogin("BebeS");
                    encodedPassword = passwordEncoder.encode("BestFriend");
                    tenant11.setPassword(encodedPassword);
                    tenant11.setRating(0F);
                    tenant11.setActive(true);
                    tenant11.setTenantPayments(new HashSet<>());
                    tenant11.setCurrentProperty(null);
                    tenant11.setLeasingHistories(new ArrayList<>());
                    try {
                        tenant11.setPaymentCardNo(encryptCardNumber(tenant11.getId(), UserType.TENANT, "30569309025904"));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    tenant11.setCardValidityDate(YearMonth.of(2025, 12));
                    try {
                        tenant11.setCvv(encryptCVV(tenant11.getId(), UserType.TENANT, new char[]{4, 7, 3}).toCharArray());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    tenant11.setConfirmationToken("");
                    tenant11.setPreferredCurrency(currencyService.returnBaseCurrency());
                    tenant11.setKnownIps(new ArrayList<>());
                    tenant11.setExpirationTime(LocalDateTime.now());
                    tenant11.setAuthorities(authoritiesT);
                    tenantService.addTenant(tenant11);
                    System.out.println("   * New tenant added to the database: ID" + tenant11.getId() + ", First name / surname: " + tenant11.getFirstName() + " " + tenant11.getLastName());
                    LOGGER.info("New tenant added to the database: ID" + tenant11.getId() + ", First name / surname: " + tenant11.getFirstName() + " " + tenant11.getLastName());

                    Tenant tenant12 = new Tenant();
                    tenant12.setId(12L);
                    tenant12.setFirstName("Rebecca");
                    tenant12.setLastName("Red");
                    tenant12.setPhone("+1800111121");
                    tenant12.setEmail("propman_testmail1@inbox.lv");
                    tenant12.setIban("GB33BUKB20201555555555");
                    tenant12.setLogin("RebeccaR");
                    encodedPassword = passwordEncoder.encode("RedHair");
                    tenant12.setPassword(encodedPassword);
                    tenant12.setRating(0F);
                    tenant12.setActive(true);
                    tenant12.setTenantPayments(new HashSet<>());
                    tenant12.setCurrentProperty(null);
                    tenant12.setLeasingHistories(new ArrayList<>());
                    try {
                        tenant12.setPaymentCardNo(encryptCardNumber(tenant12.getId(), UserType.TENANT, "6011000990139424"));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    tenant12.setCardValidityDate(YearMonth.of(2026, 7));
                    try {
                        tenant12.setCvv(encryptCVV(tenant12.getId(), UserType.TENANT, new char[]{1, 2, 3}).toCharArray());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    tenant12.setConfirmationToken("");
                    tenant12.setPreferredCurrency(currencyService.returnBaseCurrency());
                    tenant12.setKnownIps(new ArrayList<>());
                    tenant12.setExpirationTime(LocalDateTime.now());
                    tenant12.setAuthorities(authoritiesT);
                    tenantService.addTenant(tenant12);
                    System.out.println("   * New tenant added to the database: ID" + tenant12.getId() + ", First name / surname: " + tenant12.getFirstName() + " " + tenant12.getLastName());
                    LOGGER.info("New tenant added to the database: ID" + tenant12.getId() + ", First name / surname: " + tenant12.getFirstName() + " " + tenant12.getLastName());

                    Tenant tenant13 = new Tenant();
                    tenant13.setId(13L);
                    tenant13.setFirstName("Clyde");
                    tenant13.setLastName("Donovan");
                    tenant13.setPhone("+1800111122");
                    tenant13.setEmail("propman_testmail1@inbox.lv");
                    tenant13.setIban("ES9121000418450200051332");
                    tenant13.setLogin("ClydeD");
                    encodedPassword = passwordEncoder.encode("Mysterion");
                    tenant13.setPassword(encodedPassword);
                    tenant13.setRating(0F);
                    tenant13.setActive(true);
                    tenant13.setTenantPayments(new HashSet<>());
                    tenant13.setCurrentProperty(null);
                    tenant13.setLeasingHistories(new ArrayList<>());
                    try {
                        tenant13.setPaymentCardNo(encryptCardNumber(tenant13.getId(), UserType.TENANT, "3541599999998329"));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    tenant13.setCardValidityDate(YearMonth.of(2024, 11));
                    try {
                        tenant13.setCvv(encryptCVV(tenant13.getId(), UserType.TENANT, new char[]{5, 4, 2}).toCharArray());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    tenant13.setConfirmationToken("");
                    tenant13.setPreferredCurrency(currencyService.returnBaseCurrency());
                    tenant13.setKnownIps(new ArrayList<>());
                    tenant13.setExpirationTime(LocalDateTime.now());
                    tenant13.setAuthorities(authoritiesT);
                    tenantService.addTenant(tenant13);
                    System.out.println("   * New tenant added to the database: ID" + tenant13.getId() + ", First name / surname: " + tenant13.getFirstName() + " " + tenant13.getLastName());
                    LOGGER.info("New tenant added to the database: ID" + tenant13.getId() + ", First name / surname: " + tenant13.getFirstName() + " " + tenant13.getLastName());

                    Tenant tenant14 = new Tenant();
                    tenant14.setId(14L);
                    tenant14.setFirstName("Annie");
                    tenant14.setLastName("Knitts");
                    tenant14.setPhone("+1800111133");
                    tenant14.setEmail("propman_testmail1@inbox.lv");
                    tenant14.setIban("DE89370400440532013000");
                    tenant14.setLogin("AnnieK");
                    encodedPassword = passwordEncoder.encode("Butterfly");
                    tenant14.setPassword(encodedPassword);
                    tenant14.setRating(0F);
                    tenant14.setActive(true);
                    tenant14.setTenantPayments(new HashSet<>());
                    tenant14.setCurrentProperty(null);
                    tenant14.setLeasingHistories(new ArrayList<>());
                    try {
                        tenant14.setPaymentCardNo(encryptCardNumber(tenant14.getId(), UserType.TENANT, "5555555555554444"));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    tenant14.setCardValidityDate(YearMonth.of(2025, 7));
                    try {
                        tenant14.setCvv(encryptCVV(tenant14.getId(), UserType.TENANT, new char[]{1, 2, 3}).toCharArray());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    tenant14.setConfirmationToken("");
                    tenant14.setPreferredCurrency(currencyService.returnBaseCurrency());
                    tenant14.setKnownIps(new ArrayList<>());
                    tenant14.setExpirationTime(LocalDateTime.now());
                    tenant14.setAuthorities(authoritiesT);
                    tenantService.addTenant(tenant14);
                    System.out.println("   * New tenant added to the database: ID" + tenant14.getId() + ", First name / surname: " + tenant14.getFirstName() + " " + tenant14.getLastName());
                    LOGGER.info("New tenant added to the database: ID" + tenant14.getId() + ", First name / surname: " + tenant14.getFirstName() + " " + tenant14.getLastName());

                    // Adding LeasingHistories
                    System.out.println();
                    System.out.println(" *** Creating LeasingHistories...");

                    // LeasingHistory for Tenant 1
                    LeasingHistory leasingHistory1 = new LeasingHistory();
                    leasingHistory1.setId(1L);
                    leasingHistory1.setPropertyId(4L);
                    leasingHistory1.setTenant(tenant1);
                    leasingHistory1.setStartDate(Timestamp.valueOf("2022-09-04 12:00:00"));
                    leasingHistory1.setEndDate(Timestamp.valueOf("2022-09-20 12:00:00"));
                    leasingHistoryService.addLeasingHistory(leasingHistory1);
                    System.out.println("   * New LeasingHistory generated: ID" + leasingHistory1.getId() + ", tenant " + tenant1.getFirstName() + " " + tenant1.getLastName() + " stayed at Property " + leasingHistory1.getPropertyId());

                    LeasingHistory leasingHistory2 = new LeasingHistory();
                    leasingHistory2.setId(2L);
                    leasingHistory2.setPropertyId(7L);
                    leasingHistory2.setTenant(tenant1);
                    leasingHistory2.setStartDate(Timestamp.valueOf("2023-05-01 12:00:00"));
                    leasingHistory2.setEndDate(Timestamp.valueOf("2023-05-15 12:00:00"));
                    leasingHistoryService.addLeasingHistory(leasingHistory2);
                    System.out.println("   * New LeasingHistory generated: ID" + leasingHistory2.getId() + ", tenant " + tenant1.getFirstName() + " " + tenant1.getLastName() + " stayed at Property " + leasingHistory2.getPropertyId());

                    List<LeasingHistory> leasingHistoriesTenant1 = new ArrayList<>();
                    leasingHistoriesTenant1.add(leasingHistory1);
                    leasingHistoriesTenant1.add(leasingHistory2);
                    tenant1.setLeasingHistories(leasingHistoriesTenant1);

                    // LeasingHistory for Tenant 2
                    LeasingHistory leasingHistory3 = new LeasingHistory();
                    leasingHistory3.setId(3L);
                    leasingHistory3.setPropertyId(1L);
                    leasingHistory3.setTenant(tenant2);
                    leasingHistory3.setStartDate(Timestamp.valueOf("2023-01-10 12:00:00"));
                    leasingHistory3.setEndDate(Timestamp.valueOf("2023-01-25 12:00:00"));
                    leasingHistoryService.addLeasingHistory(leasingHistory3);
                    System.out.println("   * New LeasingHistory generated: ID" + leasingHistory3.getId() + ", tenant " + tenant2.getFirstName() + " " + tenant2.getLastName() + " stayed at Property " + leasingHistory3.getPropertyId());

                    LeasingHistory leasingHistory4 = new LeasingHistory();
                    leasingHistory4.setId(4L);
                    leasingHistory4.setPropertyId(5L);
                    leasingHistory4.setTenant(tenant2);
                    leasingHistory4.setStartDate(Timestamp.valueOf("2022-11-15 12:00:00"));
                    leasingHistory4.setEndDate(Timestamp.valueOf("2022-11-30 12:00:00"));
                    leasingHistoryService.addLeasingHistory(leasingHistory4);
                    System.out.println("   * New LeasingHistory generated: ID" + leasingHistory4.getId() + ", tenant " + tenant2.getFirstName() + " " + tenant2.getLastName() + " stayed at Property " + leasingHistory4.getPropertyId());

                    List<LeasingHistory> leasingHistoriesTenant2 = new ArrayList<>();
                    leasingHistoriesTenant2.add(leasingHistory3);
                    leasingHistoriesTenant2.add(leasingHistory4);
                    tenant2.setLeasingHistories(leasingHistoriesTenant2);

                    // LeasingHistory for Tenant 3
                    LeasingHistory leasingHistory5 = new LeasingHistory();
                    leasingHistory5.setId(5L);
                    leasingHistory5.setPropertyId(3L);
                    leasingHistory5.setTenant(tenant3);
                    leasingHistory5.setStartDate(Timestamp.valueOf("2023-03-05 12:00:00"));
                    leasingHistory5.setEndDate(Timestamp.valueOf("2023-03-20 12:00:00"));
                    leasingHistoryService.addLeasingHistory(leasingHistory5);
                    System.out.println("   * New LeasingHistory generated: ID" + leasingHistory5.getId() + ", tenant " + tenant3.getFirstName() + " " + tenant3.getLastName() + " stayed at Property " + leasingHistory5.getPropertyId());

                    LeasingHistory leasingHistory6 = new LeasingHistory();
                    leasingHistory6.setId(6L);
                    leasingHistory6.setPropertyId(6L);
                    leasingHistory6.setTenant(tenant3);
                    leasingHistory6.setStartDate(Timestamp.valueOf("2022-08-10 12:00:00"));
                    leasingHistory6.setEndDate(Timestamp.valueOf("2022-08-25 12:00:00"));
                    leasingHistoryService.addLeasingHistory(leasingHistory6);
                    System.out.println("   * New LeasingHistory generated: ID" + leasingHistory6.getId() + ", tenant " + tenant3.getFirstName() + " " + tenant3.getLastName() + " stayed at Property " + leasingHistory6.getPropertyId());

                    List<LeasingHistory> leasingHistoriesTenant3 = new ArrayList<>();
                    leasingHistoriesTenant3.add(leasingHistory5);
                    leasingHistoriesTenant3.add(leasingHistory6);
                    tenant3.setLeasingHistories(leasingHistoriesTenant3);

                    // LeasingHistory for Tenant 4
                    LeasingHistory leasingHistory7 = new LeasingHistory();
                    leasingHistory7.setId(7L);
                    leasingHistory7.setPropertyId(2L);
                    leasingHistory7.setTenant(tenant4);
                    leasingHistory7.setStartDate(Timestamp.valueOf("2023-07-10 12:00:00"));
                    leasingHistory7.setEndDate(Timestamp.valueOf("2023-07-25 12:00:00"));
                    leasingHistoryService.addLeasingHistory(leasingHistory7);
                    System.out.println("   * New LeasingHistory generated: ID" + leasingHistory7.getId() + ", tenant " + tenant4.getFirstName() + " " + tenant4.getLastName() + " stayed at Property " + leasingHistory7.getPropertyId());

                    LeasingHistory leasingHistory8 = new LeasingHistory();
                    leasingHistory8.setId(8L);
                    leasingHistory8.setPropertyId(8L);
                    leasingHistory8.setTenant(tenant4);
                    leasingHistory8.setStartDate(Timestamp.valueOf("2022-12-10 12:00:00"));
                    leasingHistory8.setEndDate(Timestamp.valueOf("2022-12-25 12:00:00"));
                    leasingHistoryService.addLeasingHistory(leasingHistory8);
                    System.out.println("   * New LeasingHistory generated: ID" + leasingHistory8.getId() + ", tenant " + tenant4.getFirstName() + " " + tenant4.getLastName() + " stayed at Property " + leasingHistory8.getPropertyId());

                    List<LeasingHistory> leasingHistoriesTenant4 = new ArrayList<>();
                    leasingHistoriesTenant4.add(leasingHistory7);
                    leasingHistoriesTenant4.add(leasingHistory8);
                    tenant4.setLeasingHistories(leasingHistoriesTenant4);

                    // LeasingHistory for Tenant 5
                    LeasingHistory leasingHistory9 = new LeasingHistory();
                    leasingHistory9.setId(9L);
                    leasingHistory9.setPropertyId(9L);
                    leasingHistory9.setTenant(tenant5);
                    leasingHistory9.setStartDate(Timestamp.valueOf("2023-04-10 12:00:00"));
                    leasingHistory9.setEndDate(Timestamp.valueOf("2023-04-25 12:00:00"));
                    leasingHistoryService.addLeasingHistory(leasingHistory9);
                    System.out.println("   * New LeasingHistory generated: ID" + leasingHistory9.getId() + ", tenant " + tenant5.getFirstName() + " " + tenant5.getLastName() + " stayed at Property " + leasingHistory9.getPropertyId());

                    LeasingHistory leasingHistory10 = new LeasingHistory();
                    leasingHistory10.setId(10L);
                    leasingHistory10.setPropertyId(12L);
                    leasingHistory10.setTenant(tenant5);
                    leasingHistory10.setStartDate(Timestamp.valueOf("2022-10-10 12:00:00"));
                    leasingHistory10.setEndDate(Timestamp.valueOf("2022-10-25 12:00:00"));
                    leasingHistoryService.addLeasingHistory(leasingHistory10);
                    System.out.println("   * New LeasingHistory generated: ID" + leasingHistory10.getId() + ", tenant " + tenant5.getFirstName() + " " + tenant5.getLastName() + " stayed at Property " + leasingHistory10.getPropertyId());

                    List<LeasingHistory> leasingHistoriesTenant5 = new ArrayList<>();
                    leasingHistoriesTenant5.add(leasingHistory9);
                    leasingHistoriesTenant5.add(leasingHistory10);
                    tenant5.setLeasingHistories(leasingHistoriesTenant5);

                    // LeasingHistory for Tenant 6
                    LeasingHistory leasingHistory11 = new LeasingHistory();
                    leasingHistory11.setId(11L);
                    leasingHistory11.setPropertyId(10L);
                    leasingHistory11.setTenant(tenant6);
                    leasingHistory11.setStartDate(Timestamp.valueOf("2023-02-15 12:00:00"));
                    leasingHistory11.setEndDate(Timestamp.valueOf("2023-03-01 12:00:00"));
                    leasingHistoryService.addLeasingHistory(leasingHistory11);
                    System.out.println("   * New LeasingHistory generated: ID" + leasingHistory11.getId() + ", tenant " + tenant6.getFirstName() + " " + tenant6.getLastName() + " stayed at Property " + leasingHistory11.getPropertyId());

                    LeasingHistory leasingHistory12 = new LeasingHistory();
                    leasingHistory12.setId(12L);
                    leasingHistory12.setPropertyId(11L);
                    leasingHistory12.setTenant(tenant6);
                    leasingHistory12.setStartDate(Timestamp.valueOf("2022-07-15 12:00:00"));
                    leasingHistory12.setEndDate(Timestamp.valueOf("2022-07-30 12:00:00"));
                    leasingHistoryService.addLeasingHistory(leasingHistory12);
                    System.out.println("   * New LeasingHistory generated: ID" + leasingHistory12.getId() + ", tenant " + tenant6.getFirstName() + " " + tenant6.getLastName() + " stayed at Property " + leasingHistory12.getPropertyId());

                    List<LeasingHistory> leasingHistoriesTenant6 = new ArrayList<>();
                    leasingHistoriesTenant6.add(leasingHistory11);
                    leasingHistoriesTenant6.add(leasingHistory12);
                    tenant6.setLeasingHistories(leasingHistoriesTenant6);

                    // LeasingHistory for Tenant 7
                    LeasingHistory leasingHistory13 = new LeasingHistory();
                    leasingHistory13.setId(13L);
                    leasingHistory13.setPropertyId(2L);
                    leasingHistory13.setTenant(tenant7);
                    leasingHistory13.setStartDate(Timestamp.valueOf("2023-02-10 12:00:00"));
                    leasingHistory13.setEndDate(Timestamp.valueOf("2023-02-25 12:00:00"));
                    leasingHistoryService.addLeasingHistory(leasingHistory13);
                    System.out.println("   * New LeasingHistory generated: ID" + leasingHistory13.getId() + ", tenant " + tenant7.getFirstName() + " " + tenant7.getLastName() + " stayed at Property " + leasingHistory13.getPropertyId());

                    LeasingHistory leasingHistory14 = new LeasingHistory();
                    leasingHistory14.setId(14L);
                    leasingHistory14.setPropertyId(8L);
                    leasingHistory14.setTenant(tenant7);
                    leasingHistory14.setStartDate(Timestamp.valueOf("2022-12-10 12:00:00"));
                    leasingHistory14.setEndDate(Timestamp.valueOf("2022-12-25 12:00:00"));
                    leasingHistoryService.addLeasingHistory(leasingHistory14);
                    System.out.println("   * New LeasingHistory generated: ID" + leasingHistory14.getId() + ", tenant " + tenant7.getFirstName() + " " + tenant7.getLastName() + " stayed at Property " + leasingHistory14.getPropertyId());

                    List<LeasingHistory> leasingHistoriesTenant7 = new ArrayList<>();
                    leasingHistoriesTenant7.add(leasingHistory13);
                    leasingHistoriesTenant7.add(leasingHistory14);
                    tenant7.setLeasingHistories(leasingHistoriesTenant7);

                    // LeasingHistory for Tenant 8
                    LeasingHistory leasingHistory15 = new LeasingHistory();
                    leasingHistory15.setId(15L);
                    leasingHistory15.setPropertyId(3L);
                    leasingHistory15.setTenant(tenant8);
                    leasingHistory15.setStartDate(Timestamp.valueOf("2023-04-15 12:00:00"));
                    leasingHistory15.setEndDate(Timestamp.valueOf("2023-04-30 12:00:00"));
                    leasingHistoryService.addLeasingHistory(leasingHistory15);
                    System.out.println("   * New LeasingHistory generated: ID" + leasingHistory15.getId() + ", tenant " + tenant8.getFirstName() + " " + tenant8.getLastName() + " stayed at Property " + leasingHistory15.getPropertyId());

                    LeasingHistory leasingHistory16 = new LeasingHistory();
                    leasingHistory16.setId(16L);
                    leasingHistory16.setPropertyId(9L);
                    leasingHistory16.setTenant(tenant8);
                    leasingHistory16.setStartDate(Timestamp.valueOf("2022-08-15 12:00:00"));
                    leasingHistory16.setEndDate(Timestamp.valueOf("2022-08-30 12:00:00"));
                    leasingHistoryService.addLeasingHistory(leasingHistory16);
                    System.out.println("   * New LeasingHistory generated: ID" + leasingHistory16.getId() + ", tenant " + tenant8.getFirstName() + " " + tenant8.getLastName() + " stayed at Property " + leasingHistory16.getPropertyId());

                    List<LeasingHistory> leasingHistoriesTenant8 = new ArrayList<>();
                    leasingHistoriesTenant8.add(leasingHistory15);
                    leasingHistoriesTenant8.add(leasingHistory16);
                    tenant8.setLeasingHistories(leasingHistoriesTenant8);

                    // LeasingHistory for Tenant 9
                    LeasingHistory leasingHistory17 = new LeasingHistory();
                    leasingHistory17.setId(17L);
                    leasingHistory17.setPropertyId(4L);
                    leasingHistory17.setTenant(tenant9);
                    leasingHistory17.setStartDate(Timestamp.valueOf("2023-05-10 12:00:00"));
                    leasingHistory17.setEndDate(Timestamp.valueOf("2023-05-25 12:00:00"));
                    leasingHistoryService.addLeasingHistory(leasingHistory17);
                    System.out.println("   * New LeasingHistory generated: ID" + leasingHistory17.getId() + ", tenant " + tenant9.getFirstName() + " " + tenant9.getLastName() + " stayed at Property " + leasingHistory17.getPropertyId());

                    LeasingHistory leasingHistory18 = new LeasingHistory();
                    leasingHistory18.setId(18L);
                    leasingHistory18.setPropertyId(10L);
                    leasingHistory18.setTenant(tenant9);
                    leasingHistory18.setStartDate(Timestamp.valueOf("2022-09-15 12:00:00"));
                    leasingHistory18.setEndDate(Timestamp.valueOf("2022-09-30 12:00:00"));
                    leasingHistoryService.addLeasingHistory(leasingHistory18);
                    System.out.println("   * New LeasingHistory generated: ID" + leasingHistory18.getId() + ", tenant " + tenant9.getFirstName() + " " + tenant9.getLastName() + " stayed at Property " + leasingHistory18.getPropertyId());

                    List<LeasingHistory> leasingHistoriesTenant9 = new ArrayList<>();
                    leasingHistoriesTenant9.add(leasingHistory17);
                    leasingHistoriesTenant9.add(leasingHistory18);
                    tenant9.setLeasingHistories(leasingHistoriesTenant9);

                    // LeasingHistory for Tenant 10
                    LeasingHistory leasingHistory19 = new LeasingHistory();
                    leasingHistory19.setId(19L);
                    leasingHistory19.setPropertyId(5L);
                    leasingHistory19.setTenant(tenant10);
                    leasingHistory19.setStartDate(Timestamp.valueOf("2023-06-10 12:00:00"));
                    leasingHistory19.setEndDate(Timestamp.valueOf("2023-06-25 12:00:00"));
                    leasingHistoryService.addLeasingHistory(leasingHistory19);
                    System.out.println("   * New LeasingHistory generated: ID" + leasingHistory19.getId() + ", tenant " + tenant10.getFirstName() + " " + tenant10.getLastName() + " stayed at Property " + leasingHistory19.getPropertyId());

                    LeasingHistory leasingHistory20 = new LeasingHistory();
                    leasingHistory20.setId(20L);
                    leasingHistory20.setPropertyId(11L);
                    leasingHistory20.setTenant(tenant10);
                    leasingHistory20.setStartDate(Timestamp.valueOf("2022-10-15 12:00:00"));
                    leasingHistory20.setEndDate(Timestamp.valueOf("2022-10-30 12:00:00"));
                    leasingHistoryService.addLeasingHistory(leasingHistory20);
                    System.out.println("   * New LeasingHistory generated: ID" + leasingHistory20.getId() + ", tenant " + tenant10.getFirstName() + " " + tenant10.getLastName() + " stayed at Property " + leasingHistory20.getPropertyId());

                    List<LeasingHistory> leasingHistoriesTenant10 = new ArrayList<>();
                    leasingHistoriesTenant10.add(leasingHistory19);
                    leasingHistoriesTenant10.add(leasingHistory20);
                    tenant10.setLeasingHistories(leasingHistoriesTenant10);

                    // LeasingHistory for Tenant 11
                    LeasingHistory leasingHistory21 = new LeasingHistory();
                    leasingHistory21.setId(21L);
                    leasingHistory21.setPropertyId(6L);
                    leasingHistory21.setTenant(tenant11);
                    leasingHistory21.setStartDate(Timestamp.valueOf("2023-07-10 12:00:00"));
                    leasingHistory21.setEndDate(Timestamp.valueOf("2023-07-25 12:00:00"));
                    leasingHistoryService.addLeasingHistory(leasingHistory21);
                    System.out.println("   * New LeasingHistory generated: ID" + leasingHistory21.getId() + ", tenant " + tenant11.getFirstName() + " " + tenant11.getLastName() + " stayed at Property " + leasingHistory21.getPropertyId());

                    LeasingHistory leasingHistory22 = new LeasingHistory();
                    leasingHistory22.setId(22L);
                    leasingHistory22.setPropertyId(12L);
                    leasingHistory22.setTenant(tenant11);
                    leasingHistory22.setStartDate(Timestamp.valueOf("2022-11-15 12:00:00"));
                    leasingHistory22.setEndDate(Timestamp.valueOf("2022-11-30 12:00:00"));
                    leasingHistoryService.addLeasingHistory(leasingHistory22);
                    System.out.println("   * New LeasingHistory generated: ID" + leasingHistory22.getId() + ", tenant " + tenant11.getFirstName() + " " + tenant11.getLastName() + " stayed at Property " + leasingHistory22.getPropertyId());

                    List<LeasingHistory> leasingHistoriesTenant11 = new ArrayList<>();
                    leasingHistoriesTenant11.add(leasingHistory21);
                    leasingHistoriesTenant11.add(leasingHistory22);
                    tenant11.setLeasingHistories(leasingHistoriesTenant11);

                    // LeasingHistory for Tenant 12
                    LeasingHistory leasingHistory23 = new LeasingHistory();
                    leasingHistory23.setId(23L);
                    leasingHistory23.setPropertyId(7L);
                    leasingHistory23.setTenant(tenant12);
                    leasingHistory23.setStartDate(Timestamp.valueOf("2023-08-10 12:00:00"));
                    leasingHistory23.setEndDate(Timestamp.valueOf("2023-08-25 12:00:00"));
                    leasingHistoryService.addLeasingHistory(leasingHistory23);
                    System.out.println("   * New LeasingHistory generated: ID" + leasingHistory23.getId() + ", tenant " + tenant12.getFirstName() + " " + tenant12.getLastName() + " stayed at Property " + leasingHistory23.getPropertyId());

                    LeasingHistory leasingHistory24 = new LeasingHistory();
                    leasingHistory24.setId(24L);
                    leasingHistory24.setPropertyId(1L);
                    leasingHistory24.setTenant(tenant12);
                    leasingHistory24.setStartDate(Timestamp.valueOf("2022-12-15 12:00:00"));
                    leasingHistory24.setEndDate(Timestamp.valueOf("2022-12-30 12:00:00"));
                    leasingHistoryService.addLeasingHistory(leasingHistory24);
                    System.out.println("   * New LeasingHistory generated: ID" + leasingHistory24.getId() + ", tenant " + tenant12.getFirstName() + " " + tenant12.getLastName() + " stayed at Property " + leasingHistory24.getPropertyId());

                    List<LeasingHistory> leasingHistoriesTenant12 = new ArrayList<>();
                    leasingHistoriesTenant12.add(leasingHistory23);
                    leasingHistoriesTenant12.add(leasingHistory24);
                    tenant12.setLeasingHistories(leasingHistoriesTenant12);

                    // LeasingHistory for Tenant 13
                    LeasingHistory leasingHistory25 = new LeasingHistory();
                    leasingHistory25.setId(25L);
                    leasingHistory25.setPropertyId(2L);
                    leasingHistory25.setTenant(tenant13);
                    leasingHistory25.setStartDate(Timestamp.valueOf("2023-09-10 12:00:00"));
                    leasingHistory25.setEndDate(Timestamp.valueOf("2023-09-25 12:00:00"));
                    leasingHistoryService.addLeasingHistory(leasingHistory25);
                    System.out.println("   * New LeasingHistory generated: ID" + leasingHistory25.getId() + ", tenant " + tenant13.getFirstName() + " " + tenant13.getLastName() + " stayed at Property " + leasingHistory25.getPropertyId());

                    LeasingHistory leasingHistory26 = new LeasingHistory();
                    leasingHistory26.setId(26L);
                    leasingHistory26.setPropertyId(3L);
                    leasingHistory26.setTenant(tenant13);
                    leasingHistory26.setStartDate(Timestamp.valueOf("2022-11-15 12:00:00"));
                    leasingHistory26.setEndDate(Timestamp.valueOf("2022-11-30 12:00:00"));
                    leasingHistoryService.addLeasingHistory(leasingHistory26);
                    System.out.println("   * New LeasingHistory generated: ID" + leasingHistory26.getId() + ", tenant " + tenant13.getFirstName() + " " + tenant13.getLastName() + " stayed at Property " + leasingHistory26.getPropertyId());

                    List<LeasingHistory> leasingHistoriesTenant13 = new ArrayList<>();
                    leasingHistoriesTenant13.add(leasingHistory25);
                    leasingHistoriesTenant13.add(leasingHistory26);
                    tenant13.setLeasingHistories(leasingHistoriesTenant13);

                    // LeasingHistory for Tenant 14
                    LeasingHistory leasingHistory27 = new LeasingHistory();
                    leasingHistory27.setId(27L);
                    leasingHistory27.setPropertyId(4L);
                    leasingHistory27.setTenant(tenant14);
                    leasingHistory27.setStartDate(Timestamp.valueOf("2023-10-10 12:00:00"));
                    leasingHistory27.setEndDate(Timestamp.valueOf("2023-10-25 12:00:00"));
                    leasingHistoryService.addLeasingHistory(leasingHistory27);
                    System.out.println("   * New LeasingHistory generated: ID" + leasingHistory27.getId() + ", tenant " + tenant14.getFirstName() + " " + tenant14.getLastName() + " stayed at Property " + leasingHistory27.getPropertyId());

                    LeasingHistory leasingHistory28 = new LeasingHistory();
                    leasingHistory28.setId(28L);
                    leasingHistory28.setPropertyId(5L);
                    leasingHistory28.setTenant(tenant14);
                    leasingHistory28.setStartDate(Timestamp.valueOf("2022-12-10 12:00:00"));
                    leasingHistory28.setEndDate(Timestamp.valueOf("2022-12-25 12:00:00"));
                    leasingHistoryService.addLeasingHistory(leasingHistory28);
                    System.out.println("   * New LeasingHistory generated: ID" + leasingHistory28.getId() + ", tenant " + tenant14.getFirstName() + " " + tenant14.getLastName() + " stayed at Property " + leasingHistory28.getPropertyId());

                    List<LeasingHistory> leasingHistoriesTenant14 = new ArrayList<>();
                    leasingHistoriesTenant14.add(leasingHistory27);
                    leasingHistoriesTenant14.add(leasingHistory28);
                    tenant14.setLeasingHistories(leasingHistoriesTenant14);

                    // Assigning TenantFavorites for several Tenants
                    System.out.println();
                    System.out.println("Assigning TenantFavorites for several Tenants...");

                    List<Long> propertyIDs = new ArrayList<>();
                    TenantFavorites tenantFavorites1 = new TenantFavorites();
                    tenantFavorites1.setId(1L);
                    tenantFavorites1.setTenantId(1L);
                    propertyIDs.add(4L);
                    propertyIDs.add(7L);
                    tenantFavorites1.setFavoritePropertyIDs(propertyIDs);
                    propertyIDs.clear();
                    tenantFavoritesService.addTenantFavorites(tenantFavorites1);
                    System.out.println("   * Assigned Properties 4 and 7 as favorites for Tenant 1");

                    TenantFavorites tenantFavorites2 = new TenantFavorites();
                    tenantFavorites2.setId(2L);
                    tenantFavorites2.setTenantId(2L);
                    propertyIDs.add(5L);
                    propertyIDs.add(9L);
                    tenantFavorites2.setFavoritePropertyIDs(propertyIDs);
                    propertyIDs.clear();
                    tenantFavoritesService.addTenantFavorites(tenantFavorites2);
                    System.out.println("   * Assigned Properties 5 and 9 as favorites for Tenant 2");

                    TenantFavorites tenantFavorites5 = new TenantFavorites();
                    tenantFavorites5.setId(3L);
                    tenantFavorites5.setTenantId(5L);
                    propertyIDs.add(6L);
                    propertyIDs.add(10L);
                    tenantFavorites5.setFavoritePropertyIDs(propertyIDs);
                    propertyIDs.clear();
                    tenantFavoritesService.addTenantFavorites(tenantFavorites5);
                    System.out.println("   * Assigned Properties 6 and 10 as favorites for Tenant 5");

                    TenantFavorites tenantFavorites7 = new TenantFavorites();
                    tenantFavorites7.setId(4L);
                    tenantFavorites7.setTenantId(7L);
                    propertyIDs.add(2L);
                    propertyIDs.add(8L);
                    tenantFavorites7.setFavoritePropertyIDs(propertyIDs);
                    propertyIDs.clear();
                    tenantFavoritesService.addTenantFavorites(tenantFavorites7);
                    System.out.println("   * Assigned Properties 2 and 8 as favorites for Tenant 7");

                    TenantFavorites tenantFavorites9 = new TenantFavorites();
                    tenantFavorites9.setId(5L);
                    tenantFavorites9.setTenantId(9L);
                    propertyIDs.add(4L);
                    propertyIDs.add(10L);
                    tenantFavorites9.setFavoritePropertyIDs(propertyIDs);
                    propertyIDs.clear();
                    tenantFavoritesService.addTenantFavorites(tenantFavorites9);
                    System.out.println("   * Assigned Properties 4 and 10 as favorites for Tenant 9");

                    TenantFavorites tenantFavorites12 = new TenantFavorites();
                    tenantFavorites12.setId(6L);
                    tenantFavorites12.setTenantId(12L);
                    propertyIDs.add(7L);
                    propertyIDs.add(1L);
                    tenantFavorites12.setFavoritePropertyIDs(propertyIDs);
                    propertyIDs.clear();
                    tenantFavoritesService.addTenantFavorites(tenantFavorites12);
                    System.out.println("   * Assigned Properties 7 and 1 as favorites for Tenant 12");


                    // Saving Managers to the database
                    System.out.println();
                    System.out.println(" *** Saving Managers to the database...");
                    managerService.addManager(manager1);
                    managerService.addManager(manager2);
                    managerService.addManager(manager3);
                    managerService.addManager(manager4);
                    managerService.addManager(manager5);
                    System.out.println(" *** Managers added to the database");

                    // Assigning Managers to Properties and saving Properties
                    System.out.println();
                    System.out.println(" *** Assigning Managers to Properties and saving Properties...");
                    property1.setManager(manager1);
                    property2.setManager(manager3);
                    property3.setManager(manager1);
                    property4.setManager(manager2);
                    property5.setManager(manager3);
                    property6.setManager(manager4);
                    property7.setManager(manager1);
                    property8.setManager(manager4);
                    property9.setManager(manager3);
                    property10.setManager(manager5);
                    property11.setManager(manager5);
                    property12.setManager(manager2);
                    propertyService.addProperty(property1);
                    propertyService.addProperty(property2);
                    propertyService.addProperty(property3);
                    propertyService.addProperty(property4);
                    propertyService.addProperty(property5);
                    propertyService.addProperty(property6);
                    propertyService.addProperty(property7);
                    propertyService.addProperty(property8);
                    propertyService.addProperty(property9);
                    propertyService.addProperty(property10);
                    propertyService.addProperty(property11);
                    propertyService.addProperty(property12);
                    System.out.println(" *** Managers assigned to their Properties, Properties added to the database");

                    // Saving Bills to the database
                    System.out.println();
                    System.out.println(" *** Saving Bills to the database...");
                    billService.addBill(bill1);
                    billService.addBill(bill2);
                    billService.addBill(bill3);
                    billService.addBill(bill4);
                    billService.addBill(bill5);
                    billService.addBill(bill6);
                    billService.addBill(bill7);
                    billService.addBill(bill8);
                    billService.addBill(bill9);
                    billService.addBill(bill10);
                    billService.addBill(bill11);
                    billService.addBill(bill12);
                    System.out.println(" *** Bills added to the database");

                    System.out.println();
                    System.out.println(" *** Assigning Bills to Property billsets...");
                    Set<Bill> billSet1 = new HashSet<>();
//                    System.out.println("     --  Tried to obtain a billSet for property1");
                    billSet1.add(bill1);
                    property1.setBills(billSet1);
                    Set<Bill> billSet2 = new HashSet<>();
                    billSet2.add(bill2);
                    property2.setBills(billSet2);
                    Set<Bill> billSet3 = new HashSet<>();
                    billSet3.add(bill3);
                    property3.setBills(billSet3);
                    Set<Bill> billSet4 = new HashSet<>();
                    billSet4.add(bill4);
                    property4.setBills(billSet4);
                    Set<Bill> billSet5 = new HashSet<>();
                    billSet5.add(bill5);
                    property5.setBills(billSet5);
                    Set<Bill> billSet6 = new HashSet<>();
                    billSet6.add(bill6);
                    property6.setBills(billSet6);
                    Set<Bill> billSet7 = new HashSet<>();
                    billSet7.add(bill7);
                    property7.setBills(billSet7);
                    Set<Bill> billSet8 = new HashSet<>();
                    billSet8.add(bill8);
                    property8.setBills(billSet8);
                    Set<Bill> billSet9 = new HashSet<>();
                    billSet9.add(bill9);
                    property9.setBills(billSet9);
                    Set<Bill> billSet10 = new HashSet<>();
                    billSet10.add(bill10);
                    property10.setBills(billSet10);
                    Set<Bill> billSet11 = new HashSet<>();
                    billSet11.add(bill11);
                    property11.setBills(billSet11);
                    Set<Bill> billSet12 = new HashSet<>();
                    billSet12.add(bill12);
                    property12.setBills(billSet12);
                    System.out.println(" *** Bills assigned to their respective Property billsets");



                    System.out.println();
                    System.out.println(" *** Assigning Properties to Managers' Property sets...");
                    Set<Property> properties1 = new HashSet<>();
//                    System.out.println(" ??? Initiated a property set for Manager 1");
                    properties1.add(property1);
//                    System.out.println(" ??? Added property 1 to the property set for Manager 1");
                    properties1.add(property3);
//                    System.out.println(" ??? Added property 3 to the property set for Manager 1");
                    properties1.add(property7);
//                    System.out.println(" ??? Added property 7 to the property set for Manager 1");
                    manager1.setProperties(properties1);
//                    System.out.println(" ??? Assigned the filled property set to Manager 1");
                    Set<Property> properties2 = new HashSet<>();
                    properties2.add(property4);
                    properties2.add(property12);
                    manager2.setProperties(properties2);
                    Set<Property> properties3 = new HashSet<>();
                    properties3.add(property2);
                    properties3.add(property5);
                    properties3.add(property9);
                    manager3.setProperties(properties3);
                    Set<Property> properties4 = new HashSet<>();
                    properties4.add(property6);
                    properties4.add(property8);
                    manager4.setProperties(properties4);
                    Set<Property> properties5 = new HashSet<>();
                    properties5.add(property10);
                    properties5.add(property11);
                    manager5.setProperties(properties5);
                    System.out.println(" *** Properties assigned to the respective Managers' sets");

                    System.out.println();
                    System.out.println(" *** Re-saving Properties...");
                    propertyService.addProperty(property1);
                    propertyService.addProperty(property2);
                    propertyService.addProperty(property3);
                    propertyService.addProperty(property4);
                    propertyService.addProperty(property5);
                    propertyService.addProperty(property6);
                    propertyService.addProperty(property7);
                    propertyService.addProperty(property8);
                    propertyService.addProperty(property9);
                    propertyService.addProperty(property10);
                    propertyService.addProperty(property11);
                    propertyService.addProperty(property12);
                    System.out.println(" *** Properties re-saved successfully");

                    System.out.println();
                    System.out.println(" *** Re-saving Managers...");
                    managerService.addManager(manager1);
                    managerService.addManager(manager2);
                    managerService.addManager(manager3);
                    managerService.addManager(manager4);
                    managerService.addManager(manager5);
                    System.out.println(" *** Managers re-saved successfully");


                    // Creating PropertyDiscounts
                    System.out.println();
                    System.out.println(" *** Creating PropertyDiscounts...");
                    PropertyDiscount propertyDiscount1 = new PropertyDiscount();
                    propertyDiscount1.setId(1L);
                    propertyDiscount1.setProperty(property11);
                    propertyDiscount1.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
                    Random r = new Random();
                    LocalDate startDate = LocalDate.now().plusDays(3).plusDays(r.nextInt(0, 61));
                    LocalDate endDate = startDate.plusDays(r.nextInt(2, 8));
                    propertyDiscount1.setPeriodStart(startDate);
                    propertyDiscount1.setPeriodEnd(endDate);
                    Double discountPercentage = -25.0;
                    propertyDiscount1.setPercentage(discountPercentage);
                    propertyDiscountService.addPropertyDiscount(propertyDiscount1);
                    String auxiliary1 = "Discount";
                    if (discountPercentage > 0) auxiliary1 = "Surcharge";
                    System.out.println("   * " + auxiliary1 + " of " + discountPercentage + " percent set for Property 11 for the period of " + startDate.toString() + " through " + endDate.toString());

                    PropertyDiscount propertyDiscount2 = new PropertyDiscount();
                    propertyDiscount2.setId(2L);
                    propertyDiscount2.setProperty(property3);
                    propertyDiscount2.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
                    LocalDate startDate2 = LocalDate.now().plusDays(3).plusDays(r.nextInt(0, 61));
                    LocalDate endDate2 = startDate2.plusDays(r.nextInt(2, 8));
                    propertyDiscount2.setPeriodStart(startDate2);
                    propertyDiscount2.setPeriodEnd(endDate2);
                    Double discountPercentage2 = -15.0;
                    propertyDiscount2.setPercentage(discountPercentage2);
                    propertyDiscountService.addPropertyDiscount(propertyDiscount2);
                    String auxiliary2 = "Discount";
                    if (discountPercentage2 > 0) auxiliary2 = "Surcharge";
                    System.out.println("   * " + auxiliary2 + " of " + discountPercentage2 + " percent set for Property 3 for the period of " + startDate2.toString() + " through " + endDate2.toString());

                    PropertyDiscount propertyDiscount3 = new PropertyDiscount();
                    propertyDiscount3.setId(3L);
                    propertyDiscount3.setProperty(property8);
                    propertyDiscount3.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
                    LocalDate startDate3 = LocalDate.now().plusDays(3).plusDays(r.nextInt(0, 61));
                    LocalDate endDate3 = startDate3.plusDays(r.nextInt(2, 8));
                    propertyDiscount3.setPeriodStart(startDate3);
                    propertyDiscount3.setPeriodEnd(endDate3);
                    Double discountPercentage3 = 30.0;
                    propertyDiscount3.setPercentage(discountPercentage3);
                    propertyDiscountService.addPropertyDiscount(propertyDiscount3);
                    String auxiliary3 = "Discount";
                    if (discountPercentage3 > 0) auxiliary3 = "Surcharge";
                    System.out.println("   * " + auxiliary3 + " of " + discountPercentage3 + " percent set for Property 8 for the period of " + startDate3.toString() + " through " + endDate3.toString());

                    PropertyDiscount propertyDiscount4 = new PropertyDiscount();
                    propertyDiscount4.setId(4L);
                    propertyDiscount4.setProperty(property5);
                    propertyDiscount4.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
                    LocalDate startDate4 = LocalDate.now().plusDays(3).plusDays(r.nextInt(0, 61));
                    LocalDate endDate4 = startDate4.plusDays(r.nextInt(2, 8));
                    propertyDiscount4.setPeriodStart(startDate4);
                    propertyDiscount4.setPeriodEnd(endDate4);
                    Double discountPercentage4 = -20.0;
                    propertyDiscount4.setPercentage(discountPercentage4);
                    propertyDiscountService.addPropertyDiscount(propertyDiscount4);
                    String auxiliary4 = "Discount";
                    if (discountPercentage4 > 0) auxiliary4 = "Surcharge";
                    System.out.println("   * " + auxiliary4 + " of " + discountPercentage4 + " percent set for Property 5 for the period of " + startDate4.toString() + " through " + endDate4.toString());



                    // Creating Bookings
                    System.out.println();
                    System.out.println(" *** Creating Bookings...");

                    Booking booking1 = new Booking();
                    booking1.setId(1L);
                    booking1.setProperty(property1);
                    booking1.setTenantId(1L);
                    booking1.setStartDate(Timestamp.valueOf(LocalDateTime.now().plusDays(10)));
                    booking1.setEndDate(Timestamp.valueOf(LocalDateTime.now().plusDays(15)));
                    booking1.setPaid(false);
                    booking1.setStatus(BookingStatus.PENDING_APPROVAL);
                    bookingService.addBooking(booking1);
                    System.out.println("   * New Booking added: ID " + booking1.getId() + ", Status: " + booking1.getStatus());

                    Booking booking2 = new Booking();
                    booking2.setId(2L);
                    booking2.setProperty(property2);
                    booking2.setTenantId(2L);
                    booking2.setStartDate(Timestamp.valueOf(LocalDateTime.now().plusDays(20)));
                    booking2.setEndDate(Timestamp.valueOf(LocalDateTime.now().plusDays(25)));
                    booking2.setPaid(false);
                    booking2.setStatus(BookingStatus.PENDING_APPROVAL);
                    bookingService.addBooking(booking2);
                    System.out.println("   * New Booking added: ID " + booking2.getId() + ", Status: " + booking2.getStatus());

                    Booking booking3 = new Booking();
                    booking3.setId(3L);
                    booking3.setProperty(property3);
                    booking3.setTenantId(3L);
                    booking3.setStartDate(Timestamp.valueOf(LocalDateTime.now().plusDays(5)));
                    booking3.setEndDate(Timestamp.valueOf(LocalDateTime.now().plusDays(10)));
                    booking3.setPaid(false);
                    booking3.setStatus(BookingStatus.PENDING_PAYMENT);
                    bookingService.addBooking(booking3);
                    System.out.println("   * New Booking added: ID " + booking3.getId() + ", Status: " + booking3.getStatus());

                    Booking booking4 = new Booking();
                    booking4.setId(4L);
                    booking4.setProperty(property4);
                    booking4.setTenantId(4L);
                    booking4.setStartDate(Timestamp.valueOf(LocalDateTime.now().plusDays(12)));
                    booking4.setEndDate(Timestamp.valueOf(LocalDateTime.now().plusDays(18)));
                    booking4.setPaid(false);
                    booking4.setStatus(BookingStatus.PENDING_PAYMENT);
                    bookingService.addBooking(booking4);
                    System.out.println("   * New Booking added: ID " + booking4.getId() + ", Status: " + booking4.getStatus());

                    Booking booking5 = new Booking();
                    booking5.setId(5L);
                    booking5.setProperty(property5);
                    booking5.setTenantId(5L);
                    booking5.setStartDate(Timestamp.valueOf(LocalDateTime.now().plusDays(25)));
                    booking5.setEndDate(Timestamp.valueOf(LocalDateTime.now().plusDays(30)));
                    booking5.setPaid(false);
                    booking5.setStatus(BookingStatus.PENDING_PAYMENT);
                    bookingService.addBooking(booking5);
                    System.out.println("   * New Booking added: ID " + booking5.getId() + ", Status: " + booking5.getStatus());

                    Booking booking6 = new Booking();
                    booking6.setId(6L);
                    booking6.setProperty(property6);
                    booking6.setTenantId(6L);
                    booking6.setStartDate(Timestamp.valueOf(LocalDateTime.now().plusDays(8)));
                    booking6.setEndDate(Timestamp.valueOf(LocalDateTime.now().plusDays(14)));
                    booking6.setPaid(true);
                    booking6.setStatus(BookingStatus.CONFIRMED);
                    bookingService.addBooking(booking6);
                    System.out.println("   * New Booking added: ID " + booking6.getId() + ", Status: " + booking6.getStatus());

                    Booking booking7 = new Booking();
                    booking7.setId(7L);
                    booking7.setProperty(property7);
                    booking7.setTenantId(7L);
                    booking7.setStartDate(Timestamp.valueOf(LocalDateTime.now().plusDays(18)));
                    booking7.setEndDate(Timestamp.valueOf(LocalDateTime.now().plusDays(22)));
                    booking7.setPaid(true);
                    booking7.setStatus(BookingStatus.CONFIRMED);
                    bookingService.addBooking(booking7);
                    System.out.println("   * New Booking added: ID " + booking7.getId() + ", Status: " + booking7.getStatus());

                    Booking booking8 = new Booking();
                    booking8.setId(8L);
                    booking8.setProperty(property8);
                    booking8.setTenantId(8L);
                    booking8.setStartDate(Timestamp.valueOf(LocalDateTime.now().minusDays(2)));
                    booking8.setEndDate(Timestamp.valueOf(LocalDateTime.now().plusDays(3)));
                    booking8.setPaid(true);
                    booking8.setStatus(BookingStatus.CURRENT);
                    bookingService.addBooking(booking8);
                    System.out.println("   * New Booking added: ID " + booking8.getId() + ", Status: " + booking8.getStatus());

                    Booking booking9 = new Booking();
                    booking9.setId(9L);
                    booking9.setProperty(property9);
                    booking9.setTenantId(9L);
                    booking9.setStartDate(Timestamp.valueOf(LocalDateTime.now().minusDays(5)));
                    booking9.setEndDate(Timestamp.valueOf(LocalDateTime.now().plusDays(1)));
                    booking9.setPaid(true);
                    booking9.setStatus(BookingStatus.CURRENT);
                    bookingService.addBooking(booking9);
                    System.out.println("   * New Booking added: ID " + booking9.getId() + ", Status: " + booking9.getStatus());

                    Booking booking10 = new Booking();
                    booking10.setId(10L);
                    booking10.setProperty(property10);
                    booking10.setTenantId(10L);
                    booking10.setStartDate(Timestamp.valueOf(LocalDateTime.now().minusDays(1)));
                    booking10.setEndDate(Timestamp.valueOf(LocalDateTime.now().plusDays(4)));
                    booking10.setPaid(true);
                    booking10.setStatus(BookingStatus.CURRENT);
                    bookingService.addBooking(booking10);
                    System.out.println("   * New Booking added: ID " + booking10.getId() + ", Status: " + booking10.getStatus());

                    Booking booking11 = new Booking();
                    booking11.setId(11L);
                    booking11.setProperty(property11);
                    booking11.setTenantId(11L);
                    booking11.setStartDate(Timestamp.valueOf(LocalDateTime.now().minusDays(10)));
                    booking11.setEndDate(Timestamp.valueOf(LocalDateTime.now().minusDays(5)));
                    booking11.setPaid(true);
                    booking11.setStatus(BookingStatus.CANCELLED);
                    bookingService.addBooking(booking11);
                    System.out.println("   * New Booking added: ID " + booking11.getId() + ", Status: " + booking11.getStatus());

                    Booking booking12 = new Booking();
                    booking12.setId(12L);
                    booking12.setProperty(property12);
                    booking12.setTenantId(12L);
                    booking12.setStartDate(Timestamp.valueOf(LocalDateTime.now().minusDays(15)));
                    booking12.setEndDate(Timestamp.valueOf(LocalDateTime.now().minusDays(10)));
                    booking12.setPaid(false);
                    booking12.setStatus(BookingStatus.CANCELLED);
                    bookingService.addBooking(booking12);
                    System.out.println("   * New Booking added: ID " + booking12.getId() + ", Status: " + booking12.getStatus());

                    Booking booking13 = new Booking();
                    booking13.setId(13L);
                    booking13.setProperty(property11);
                    booking13.setTenantId(13L);
                    booking13.setStartDate(Timestamp.valueOf(LocalDateTime.now().minusDays(7)));
                    booking13.setEndDate(Timestamp.valueOf(LocalDateTime.now().minusDays(1)));
                    booking13.setPaid(true);
                    booking13.setStatus(BookingStatus.OVER);
                    bookingService.addBooking(booking13);
                    System.out.println("   * New Booking added: ID " + booking13.getId() + ", Status: " + booking13.getStatus());

                    Booking booking14 = new Booking();
                    booking14.setId(14L);
                    booking14.setProperty(property6);
                    booking14.setTenantId(14L);
                    booking14.setStartDate(Timestamp.valueOf(LocalDateTime.now().minusDays(15)));
                    booking14.setEndDate(Timestamp.valueOf(LocalDateTime.now().minusDays(2)));
                    booking14.setPaid(true);
                    booking14.setStatus(BookingStatus.OVER);
                    bookingService.addBooking(booking14);
                    System.out.println("   * New Booking added: ID " + booking14.getId() + ", Status: " + booking14.getStatus());

                    Booking booking15 = new Booking();
                    booking15.setId(15L);
                    booking15.setProperty(property12);
                    booking15.setTenantId(1L);
                    booking15.setStartDate(Timestamp.valueOf(LocalDateTime.now().minusDays(17)));
                    booking15.setEndDate(Timestamp.valueOf(LocalDateTime.now().minusDays(3)));
                    booking15.setPaid(true);
                    booking15.setStatus(BookingStatus.OVER);
                    bookingService.addBooking(booking15);
                    System.out.println("   * New Booking added: ID " + booking15.getId() + ", Status: " + booking15.getStatus());



                    // Creating TenantPayments
                    System.out.println();
                    System.out.println(" *** Creating TenantPayments...");

                    TenantPayment payment1 = new TenantPayment();
                    payment1.setId(1L);
                    payment1.setTenant(tenant1);
                    payment1.setFeePaidToManager(false);
                    payment1.setReceivedFromTenant(false);
                    Double amount1 = bookingService.calculateTotalPrice(1L);
                    payment1.setAmount(amount1);
                    payment1.setAssociatedPropertyId(1L);
                    payment1.setManagerId(property1.getManager().getId());
                    payment1.setAssociatedBookingId(1L);
                    payment1.setCurrency(currencyService.returnBaseCurrency());
                    int paymentPeriodDaysSetOrDefault = 15;
                    Timestamp receiptDue = Timestamp.valueOf(LocalDateTime.now());
                    if (bookingService.calculateDaysDifference(booking1.getStartDate()) < paymentPeriodDaysSetOrDefault) {
                        receiptDue = Timestamp.valueOf(LocalDateTime.now().plusDays(1));
                        System.out.println("   * Late booking - must be paid within 24 hours!");
                    } else {
                        receiptDue = Timestamp.valueOf(booking1.getStartDate().toLocalDateTime().minusDays(7));
                    }
                    payment1.setReceiptDue(receiptDue);
                    int interestChargedByTheSystemSetOrDefault = 10;
                    payment1.setManagerPayment(amount1 - (amount1 / 100 * interestChargedByTheSystemSetOrDefault));
                    paymentService.addTenantPayment(payment1);
                    Set<TenantPayment> payments = tenant1.getTenantPayments();
                    payments.add(payment1);
                    tenant1.setTenantPayments(payments);
                    tenantService.addTenant(tenant1);
                    System.out.println("   * TenantPayment added for Booking ID 1 with amount " + amount1);

                    TenantPayment payment2 = new TenantPayment();
                    payment2.setId(2L);
                    payment2.setTenant(tenant2);
                    payment2.setFeePaidToManager(false);
                    payment2.setReceivedFromTenant(false);
                    Double amount2 = bookingService.calculateTotalPrice(2L);
                    payment2.setAmount(amount2);
                    payment2.setAssociatedPropertyId(2L);
                    payment2.setManagerId(property2.getManager().getId());
                    payment2.setAssociatedBookingId(2L);
                    payment2.setCurrency(currencyService.returnBaseCurrency());
                    Timestamp receiptDue2 = Timestamp.valueOf(LocalDateTime.now());
                    if (bookingService.calculateDaysDifference(booking2.getStartDate()) < paymentPeriodDaysSetOrDefault) {
                        receiptDue2 = Timestamp.valueOf(LocalDateTime.now().plusDays(1));
                        System.out.println("   * Late booking - must be paid within 24 hours!");
                    } else {
                        receiptDue2 = Timestamp.valueOf(booking2.getStartDate().toLocalDateTime().minusDays(7));
                    }
                    payment2.setReceiptDue(receiptDue2);
                    payment2.setManagerPayment(amount2 - (amount2 / 100 * interestChargedByTheSystemSetOrDefault));
                    paymentService.addTenantPayment(payment2);
                    Set<TenantPayment> payments2 = tenant2.getTenantPayments();
                    payments2.add(payment2);
                    tenant2.setTenantPayments(payments2);
                    tenantService.addTenant(tenant2);
                    System.out.println("   * TenantPayment added for Booking ID 2 with amount " + amount2);

                    TenantPayment payment3 = new TenantPayment();
                    payment3.setId(3L);
                    payment3.setTenant(tenant3);
                    payment3.setFeePaidToManager(false);
                    payment3.setReceivedFromTenant(false);
                    Double amount3 = bookingService.calculateTotalPrice(3L);
                    payment3.setAmount(amount3);
                    payment3.setAssociatedPropertyId(3L);
                    payment3.setManagerId(property3.getManager().getId());
                    payment3.setAssociatedBookingId(3L);
                    payment3.setCurrency(currencyService.returnBaseCurrency());
                    Timestamp receiptDue3 = Timestamp.valueOf(LocalDateTime.now());
                    if (bookingService.calculateDaysDifference(booking3.getStartDate()) < paymentPeriodDaysSetOrDefault) {
                        receiptDue3 = Timestamp.valueOf(LocalDateTime.now().plusDays(1));
                        System.out.println("   * Late booking - must be paid within 24 hours!");
                    } else {
                        receiptDue3 = Timestamp.valueOf(booking3.getStartDate().toLocalDateTime().minusDays(7));
                    }
                    payment3.setReceiptDue(receiptDue3);
                    payment3.setManagerPayment(amount3 - (amount3 / 100 * interestChargedByTheSystemSetOrDefault));
                    paymentService.addTenantPayment(payment3);
                    Set<TenantPayment> payments3 = tenant3.getTenantPayments();
                    payments3.add(payment3);
                    tenant3.setTenantPayments(payments3);
                    tenantService.addTenant(tenant3);
                    System.out.println("   * TenantPayment added for Booking ID 3 with amount " + amount3);

                    TenantPayment payment4 = new TenantPayment();
                    payment4.setId(4L);
                    payment4.setTenant(tenant4);
                    payment4.setFeePaidToManager(false);
                    payment4.setReceivedFromTenant(false);
                    Double amount4 = bookingService.calculateTotalPrice(4L);
                    payment4.setAmount(amount4);
                    payment4.setAssociatedPropertyId(4L);
                    payment4.setManagerId(property4.getManager().getId());
                    payment4.setAssociatedBookingId(4L);
                    payment4.setCurrency(currencyService.returnBaseCurrency());
                    Timestamp receiptDue4 = Timestamp.valueOf(LocalDateTime.now());
                    if (bookingService.calculateDaysDifference(booking4.getStartDate()) < paymentPeriodDaysSetOrDefault) {
                        receiptDue4 = Timestamp.valueOf(LocalDateTime.now().plusDays(1));
                        System.out.println("   * Late booking - must be paid within 24 hours!");
                    } else {
                        receiptDue4 = Timestamp.valueOf(booking4.getStartDate().toLocalDateTime().minusDays(7));
                    }
                    payment4.setReceiptDue(receiptDue4);
                    payment4.setManagerPayment(amount4 - (amount4 / 100 * interestChargedByTheSystemSetOrDefault));
                    paymentService.addTenantPayment(payment4);
                    Set<TenantPayment> payments4 = tenant4.getTenantPayments();
                    payments4.add(payment4);
                    tenant4.setTenantPayments(payments4);
                    tenantService.addTenant(tenant4);
                    System.out.println("   * TenantPayment added for Booking ID 4 with amount " + amount4);

                    TenantPayment payment5 = new TenantPayment();
                    payment5.setId(5L);
                    payment5.setTenant(tenant5);
                    payment5.setFeePaidToManager(false);
                    payment5.setReceivedFromTenant(false);
                    Double amount5 = bookingService.calculateTotalPrice(5L);
                    payment5.setAmount(amount5);
                    payment5.setAssociatedPropertyId(5L);
                    payment5.setManagerId(property5.getManager().getId());
                    payment5.setAssociatedBookingId(5L);
                    payment5.setCurrency(currencyService.returnBaseCurrency());
                    Timestamp receiptDue5 = Timestamp.valueOf(LocalDateTime.now());
                    if (bookingService.calculateDaysDifference(booking5.getStartDate()) < paymentPeriodDaysSetOrDefault) {
                        receiptDue5 = Timestamp.valueOf(LocalDateTime.now().plusDays(1));
                        System.out.println("   * Late booking - must be paid within 24 hours!");
                    } else {
                        receiptDue5 = Timestamp.valueOf(booking5.getStartDate().toLocalDateTime().minusDays(7));
                    }
                    payment5.setReceiptDue(receiptDue5);
                    payment5.setManagerPayment(amount5 - (amount5 / 100 * interestChargedByTheSystemSetOrDefault));
                    paymentService.addTenantPayment(payment5);
                    Set<TenantPayment> payments5 = tenant5.getTenantPayments();
                    payments5.add(payment5);
                    tenant5.setTenantPayments(payments5);
                    tenantService.addTenant(tenant5);
                    System.out.println("   * TenantPayment added for Booking ID 5 with amount " + amount5);

                    TenantPayment payment6 = new TenantPayment();
                    payment6.setId(6L);
                    payment6.setTenant(tenant6);
                    payment6.setFeePaidToManager(false);
                    payment6.setReceivedFromTenant(true);
                    Double amount6 = bookingService.calculateTotalPrice(6L);
                    payment6.setAmount(amount6);
                    payment6.setAssociatedPropertyId(6L);
                    payment6.setManagerId(property6.getManager().getId());
                    payment6.setAssociatedBookingId(6L);
                    payment6.setCurrency(currencyService.returnBaseCurrency());
                    Timestamp receiptDue6 = Timestamp.valueOf(LocalDateTime.now());
                    if (bookingService.calculateDaysDifference(booking6.getStartDate()) < paymentPeriodDaysSetOrDefault) {
                        receiptDue6 = Timestamp.valueOf(LocalDateTime.now().plusDays(1));
                        System.out.println("   * Late booking - must be paid within 24 hours!");
                    } else {
                        receiptDue6 = Timestamp.valueOf(booking6.getStartDate().toLocalDateTime().minusDays(7));
                    }
                    payment6.setReceiptDue(receiptDue6);
                    payment6.setManagerPayment(amount6 - (amount6 / 100 * interestChargedByTheSystemSetOrDefault));
                    paymentService.addTenantPayment(payment6);
                    Set<TenantPayment> payments6 = tenant6.getTenantPayments();
                    payments6.add(payment6);
                    tenant6.setTenantPayments(payments6);
                    tenantService.addTenant(tenant6);
                    System.out.println("   * TenantPayment added for Booking ID 6 with amount " + amount6);

                    TenantPayment payment7 = new TenantPayment();
                    payment7.setId(7L);
                    payment7.setTenant(tenant7);
                    payment7.setFeePaidToManager(false);
                    payment7.setReceivedFromTenant(true);
                    Double amount7 = bookingService.calculateTotalPrice(7L);
                    payment7.setAmount(amount7);
                    payment7.setAssociatedPropertyId(7L);
                    payment7.setManagerId(property7.getManager().getId());
                    payment7.setAssociatedBookingId(7L);
                    payment7.setCurrency(currencyService.returnBaseCurrency());
                    Timestamp receiptDue7 = Timestamp.valueOf(LocalDateTime.now());
                    if (bookingService.calculateDaysDifference(booking7.getStartDate()) < paymentPeriodDaysSetOrDefault) {
                        receiptDue7 = Timestamp.valueOf(LocalDateTime.now().plusDays(1));
                        System.out.println("   * Late booking - must be paid within 24 hours!");
                    } else {
                        receiptDue7 = Timestamp.valueOf(booking7.getStartDate().toLocalDateTime().minusDays(7));
                    }
                    payment7.setReceiptDue(receiptDue7);
                    payment7.setManagerPayment(amount7 - (amount7 / 100 * interestChargedByTheSystemSetOrDefault));
                    paymentService.addTenantPayment(payment7);
                    Set<TenantPayment> payments7 = tenant7.getTenantPayments();
                    payments7.add(payment7);
                    tenant7.setTenantPayments(payments7);
                    tenantService.addTenant(tenant7);
                    System.out.println("   * TenantPayment added for Booking ID 7 with amount " + amount7);

                    TenantPayment payment8 = new TenantPayment();
                    payment8.setId(8L);
                    payment8.setTenant(tenant8);
                    payment8.setFeePaidToManager(false);
                    payment8.setReceivedFromTenant(true);
                    Double amount8 = bookingService.calculateTotalPrice(8L);
                    payment8.setAmount(amount8);
                    payment8.setAssociatedPropertyId(8L);
                    payment8.setManagerId(property8.getManager().getId());
                    payment8.setAssociatedBookingId(8L);
                    payment8.setCurrency(currencyService.returnBaseCurrency());
                    Timestamp receiptDue8 = Timestamp.valueOf(LocalDateTime.now());
                    if (bookingService.calculateDaysDifference(booking8.getStartDate()) < paymentPeriodDaysSetOrDefault) {
                        receiptDue8 = Timestamp.valueOf(LocalDateTime.now().plusDays(1));
                        System.out.println("   * Late booking - must be paid within 24 hours!");
                    } else {
                        receiptDue8 = Timestamp.valueOf(booking8.getStartDate().toLocalDateTime().minusDays(7));
                    }
                    payment8.setReceiptDue(receiptDue8);
                    payment8.setManagerPayment(amount8 - (amount8 / 100 * interestChargedByTheSystemSetOrDefault));
                    paymentService.addTenantPayment(payment8);
                    Set<TenantPayment> payments8 = tenant8.getTenantPayments();
                    payments8.add(payment8);
                    tenant8.setTenantPayments(payments8);
                    tenantService.addTenant(tenant8);
                    System.out.println("   * TenantPayment added for Booking ID 8 with amount " + amount8);

                    TenantPayment payment9 = new TenantPayment();
                    payment9.setId(9L);
                    payment9.setTenant(tenant9);
                    payment9.setFeePaidToManager(false);
                    payment9.setReceivedFromTenant(true);
                    Double amount9 = bookingService.calculateTotalPrice(9L);
                    payment9.setAmount(amount9);
                    payment9.setAssociatedPropertyId(9L);
                    payment9.setManagerId(property9.getManager().getId());
                    payment9.setAssociatedBookingId(9L);
                    payment9.setCurrency(currencyService.returnBaseCurrency());
                    Timestamp receiptDue9 = Timestamp.valueOf(LocalDateTime.now());
                    if (bookingService.calculateDaysDifference(booking9.getStartDate()) < paymentPeriodDaysSetOrDefault) {
                        receiptDue9 = Timestamp.valueOf(LocalDateTime.now().plusDays(1));
                        System.out.println("   * Late booking - must be paid within 24 hours!");
                    } else {
                        receiptDue9 = Timestamp.valueOf(booking9.getStartDate().toLocalDateTime().minusDays(7));
                    }
                    payment9.setReceiptDue(receiptDue9);
                    payment9.setManagerPayment(amount9 - (amount9 / 100 * interestChargedByTheSystemSetOrDefault));
                    paymentService.addTenantPayment(payment9);
                    Set<TenantPayment> payments9 = tenant9.getTenantPayments();
                    payments9.add(payment9);
                    tenant9.setTenantPayments(payments9);
                    tenantService.addTenant(tenant9);
                    System.out.println("   * TenantPayment added for Booking ID 9 with amount " + amount9);

                    TenantPayment payment10 = new TenantPayment();
                    payment10.setId(10L);
                    payment10.setTenant(tenant10);
                    payment10.setFeePaidToManager(false);
                    payment10.setReceivedFromTenant(true);
                    Double amount10 = bookingService.calculateTotalPrice(10L);
                    payment10.setAmount(amount10);
                    payment10.setAssociatedPropertyId(10L);
                    payment10.setManagerId(property10.getManager().getId());
                    payment10.setAssociatedBookingId(10L);
                    payment10.setCurrency(currencyService.returnBaseCurrency());
                    Timestamp receiptDue10 = Timestamp.valueOf(LocalDateTime.now());
                    if (bookingService.calculateDaysDifference(booking10.getStartDate()) < paymentPeriodDaysSetOrDefault) {
                        receiptDue10 = Timestamp.valueOf(LocalDateTime.now().plusDays(1));
                        System.out.println("   * Late booking - must be paid within 24 hours!");
                    } else {
                        receiptDue10 = Timestamp.valueOf(booking10.getStartDate().toLocalDateTime().minusDays(7));
                    }
                    payment10.setReceiptDue(receiptDue10);
                    payment10.setManagerPayment(amount10 - (amount10 / 100 * interestChargedByTheSystemSetOrDefault));
                    paymentService.addTenantPayment(payment10);
                    Set<TenantPayment> payments10 = tenant10.getTenantPayments();
                    payments10.add(payment10);
                    tenant10.setTenantPayments(payments10);
                    tenantService.addTenant(tenant10);
                    System.out.println("   * TenantPayment added for Booking ID 10 with amount " + amount10);

                    TenantPayment payment11 = new TenantPayment();
                    payment11.setId(11L);
                    payment11.setTenant(tenant11);
                    payment11.setFeePaidToManager(false);
                    payment11.setReceivedFromTenant(false);
                    Double amount11 = bookingService.calculateTotalPrice(11L);
                    payment11.setAmount(amount11);
                    payment11.setAssociatedPropertyId(11L);
                    payment11.setManagerId(property11.getManager().getId());
                    payment11.setAssociatedBookingId(11L);
                    payment11.setCurrency(currencyService.returnBaseCurrency());
                    Timestamp receiptDue11 = Timestamp.valueOf(LocalDateTime.now());
                    if (bookingService.calculateDaysDifference(booking11.getStartDate()) < paymentPeriodDaysSetOrDefault) {
                        receiptDue11 = Timestamp.valueOf(LocalDateTime.now().plusDays(1));
                        System.out.println("   * Late booking - must be paid within 24 hours!");
                    } else {
                        receiptDue11 = Timestamp.valueOf(booking11.getStartDate().toLocalDateTime().minusDays(7));
                    }
                    payment11.setReceiptDue(receiptDue11);
                    payment11.setManagerPayment(amount11 - (amount11 / 100 * interestChargedByTheSystemSetOrDefault));
                    paymentService.addTenantPayment(payment11);
                    Set<TenantPayment> payments11 = tenant11.getTenantPayments();
                    payments11.add(payment11);
                    tenant11.setTenantPayments(payments11);
                    tenantService.addTenant(tenant11);
                    System.out.println("   * TenantPayment added for Booking ID 11 with amount " + amount11);

                    TenantPayment payment12 = new TenantPayment();
                    payment12.setId(12L);
                    payment12.setTenant(tenant12);
                    payment12.setFeePaidToManager(false);
                    payment12.setReceivedFromTenant(false);
                    Double amount12 = bookingService.calculateTotalPrice(12L);
                    payment12.setAmount(amount12);
                    payment12.setAssociatedPropertyId(12L);
                    payment12.setManagerId(property12.getManager().getId());
                    payment12.setAssociatedBookingId(12L);
                    payment12.setCurrency(currencyService.returnBaseCurrency());
                    Timestamp receiptDue12 = Timestamp.valueOf(LocalDateTime.now());
                    if (bookingService.calculateDaysDifference(booking12.getStartDate()) < paymentPeriodDaysSetOrDefault) {
                        receiptDue12 = Timestamp.valueOf(LocalDateTime.now().plusDays(1));
                        System.out.println("   * Late booking - must be paid within 24 hours!");
                    } else {
                        receiptDue12 = Timestamp.valueOf(booking12.getStartDate().toLocalDateTime().minusDays(7));
                    }
                    payment12.setReceiptDue(receiptDue12);
                    payment12.setManagerPayment(amount12 - (amount12 / 100 * interestChargedByTheSystemSetOrDefault));
                    paymentService.addTenantPayment(payment12);
                    Set<TenantPayment> payments12 = tenant12.getTenantPayments();
                    payments12.add(payment12);
                    tenant12.setTenantPayments(payments12);
                    tenantService.addTenant(tenant12);
                    System.out.println("   * TenantPayment added for Booking ID 12 with amount " + amount12);

                    TenantPayment payment13 = new TenantPayment();
                    payment13.setId(13L);
                    payment13.setTenant(tenant13);
                    payment13.setFeePaidToManager(false);
                    payment13.setReceivedFromTenant(true);
                    Double amount13 = bookingService.calculateTotalPrice(13L);
                    payment13.setAmount(amount13);
                    payment13.setAssociatedPropertyId(11L);
                    payment13.setManagerId(property11.getManager().getId());
                    payment13.setAssociatedBookingId(13L);
                    payment13.setCurrency(currencyService.returnBaseCurrency());
                    Timestamp receiptDue13 = Timestamp.valueOf(LocalDateTime.now());
                    if (bookingService.calculateDaysDifference(booking13.getStartDate()) < paymentPeriodDaysSetOrDefault) {
                        receiptDue13 = Timestamp.valueOf(LocalDateTime.now().plusDays(1));
                        System.out.println("   * Late booking - must be paid within 24 hours!");
                    } else {
                        receiptDue13 = Timestamp.valueOf(booking13.getStartDate().toLocalDateTime().minusDays(7));
                    }
                    payment13.setReceiptDue(receiptDue13);
                    payment13.setManagerPayment(amount13 - (amount13 / 100 * interestChargedByTheSystemSetOrDefault));
                    paymentService.addTenantPayment(payment13);
                    Set<TenantPayment> payments13 = tenant13.getTenantPayments();
                    payments13.add(payment13);
                    tenant13.setTenantPayments(payments13);
                    tenantService.addTenant(tenant13);
                    System.out.println("   * TenantPayment added for Booking ID 13 with amount " + amount13);

                    TenantPayment payment14 = new TenantPayment();
                    payment14.setId(14L);
                    payment14.setTenant(tenant14);
                    payment14.setFeePaidToManager(false);
                    payment14.setReceivedFromTenant(true);
                    Double amount14 = bookingService.calculateTotalPrice(14L);
                    payment14.setAmount(amount14);
                    payment14.setAssociatedPropertyId(6L);
                    payment14.setManagerId(property6.getManager().getId());
                    payment14.setAssociatedBookingId(14L);
                    payment14.setCurrency(currencyService.returnBaseCurrency());
                    Timestamp receiptDue14 = Timestamp.valueOf(LocalDateTime.now());
                    if (bookingService.calculateDaysDifference(booking14.getStartDate()) < paymentPeriodDaysSetOrDefault) {
                        receiptDue14 = Timestamp.valueOf(LocalDateTime.now().plusDays(1));
                        System.out.println("   * Late booking - must be paid within 24 hours!");
                    } else {
                        receiptDue14 = Timestamp.valueOf(booking14.getStartDate().toLocalDateTime().minusDays(7));
                    }
                    payment14.setReceiptDue(receiptDue14);
                    payment14.setManagerPayment(amount14 - (amount14 / 100 * interestChargedByTheSystemSetOrDefault));
                    paymentService.addTenantPayment(payment14);
                    Set<TenantPayment> payments14 = tenant14.getTenantPayments();
                    payments14.add(payment14);
                    tenant14.setTenantPayments(payments14);
                    tenantService.addTenant(tenant14);
                    System.out.println("   * TenantPayment added for Booking ID 14 with amount " + amount14);

                    TenantPayment payment15 = new TenantPayment();
                    payment15.setId(15L);
                    payment15.setTenant(tenant1);
                    payment15.setFeePaidToManager(false);
                    payment15.setReceivedFromTenant(true);
                    Double amount15 = bookingService.calculateTotalPrice(15L);
                    payment15.setAmount(amount15);
                    payment15.setAssociatedPropertyId(12L);
                    payment15.setManagerId(property12.getManager().getId());
                    payment15.setAssociatedBookingId(15L);
                    payment15.setCurrency(currencyService.returnBaseCurrency());
                    Timestamp receiptDue15 = Timestamp.valueOf(LocalDateTime.now());
                    if (bookingService.calculateDaysDifference(booking15.getStartDate()) < paymentPeriodDaysSetOrDefault) {
                        receiptDue15 = Timestamp.valueOf(LocalDateTime.now().plusDays(1));
                        System.out.println("   * Late booking - must be paid within 24 hours!");
                    } else {
                        receiptDue15 = Timestamp.valueOf(booking15.getStartDate().toLocalDateTime().minusDays(7));
                    }
                    payment15.setReceiptDue(receiptDue15);
                    payment15.setManagerPayment(amount15 - (amount15 / 100 * interestChargedByTheSystemSetOrDefault));
                    paymentService.addTenantPayment(payment15);
                    Set<TenantPayment> payments15 = tenant1.getTenantPayments();
                    payments15.add(payment15);
                    tenant1.setTenantPayments(payments15);
                    tenantService.addTenant(tenant1);
                    System.out.println("   * TenantPayment added for Booking ID 15 with amount " + amount15);

                    // Adding messages
                    System.out.println();
                    System.out.println(" *** Creating Messages...");

                    Message message1 = new Message();
                    message1.setId(1L);
                    message1.setContent("Hi Mr. Ullman, I'm really excited about my stay at the Overlook Hotel! Can you tell me more about the Presidential Suite?");
                    message1.setSenderId(tenant1.getId());
                    message1.setReceiverId(manager2.getId());
                    messageService.addMessage(message1);

                    Message message2 = new Message();
                    message2.setId(2L);
                    message2.setContent("Hello Kenny, we're glad to... have you! The Presidential Suite is our finest room, with luxurious amenities and a breathtaking view. If you find it a little too spacious, we can also offer a nice alternative... Room 217.");
                    message2.setSenderId(manager2.getId());
                    message2.setReceiverId(tenant1.getId());
                    messageService.addMessage(message2);

                    Message message3 = new Message();
                    message3.setId(3L);
                    message3.setContent("Room 217? Why, what's in there?");
                    message3.setSenderId(tenant1.getId());
                    message3.setReceiverId(manager2.getId());
                    messageService.addMessage(message3);

                    Message message4 = new Message();
                    message4.setId(4L);
                    message4.setContent("Oh, nothing to worry about. Just an old story we like to tell our guests. Enjoy your stay!");
                    message4.setSenderId(manager2.getId());
                    message4.setReceiverId(tenant1.getId());
                    messageService.addMessage(message4);

                    Message message5 = new Message();
                    message5.setId(5L);
                    message5.setContent("Alright, I can't wait to check in! See you soon!");
                    message5.setSenderId(tenant1.getId());
                    message5.setReceiverId(manager2.getId());
                    messageService.addMessage(message5);

                    Message message6 = new Message();
                    message6.setId(6L);
                    message6.setContent("Looking forward to it, Kenny. Make sure you have a safe trip! The Hotel... it is waiting for you.");
                    message6.setSenderId(manager2.getId());
                    message6.setReceiverId(tenant1.getId());
                    messageService.addMessage(message6);

                    System.out.println("   * Created a conversation between Tenant1 and Manager2");

                    Message message7 = new Message();
                    message7.setId(7L);
                    message7.setContent("Hi Ms. Lita, I'm really looking forward to my stay at the house in Pattaya. It looks fantastic!");
                    message7.setSenderId(tenant10.getId());
                    message7.setReceiverId(manager5.getId());
                    messageService.addMessage(message7);

                    Message message8 = new Message();
                    message8.setId(8L);
                    message8.setContent("Hello Gregory, we are delighted to have you stay with us. The house is perfect for a relaxing getaway. Enjoy your time in Pattaya!");
                    message8.setSenderId(manager5.getId());
                    message8.setReceiverId(tenant10.getId());
                    messageService.addMessage(message8);

                    Message message9 = new Message();
                    message9.setId(9L);
                    message9.setContent("Thanks, Ms. Lita! Just one question - is there a good place nearby to get some authentic Thai food?");
                    message9.setSenderId(tenant10.getId());
                    message9.setReceiverId(manager5.getId());
                    messageService.addMessage(message9);

                    Message message10 = new Message();
                    message10.setId(10L);
                    message10.setContent("Absolutely, Gregory! There is a great restaurant called 'Sukhumvit Soi 5' just a few minutes away. They have the best Pad Thai in town!");
                    message10.setSenderId(manager5.getId());
                    message10.setReceiverId(tenant10.getId());
                    messageService.addMessage(message10);

                    Message message11 = new Message();
                    message11.setId(11L);
                    message11.setContent("Perfect! I can't wait to try it out. Thanks for the tip, Ms. Lita!");
                    message11.setSenderId(tenant10.getId());
                    message11.setReceiverId(manager5.getId());
                    messageService.addMessage(message11);

                    System.out.println("   * Created a conversation between Tenant10 and Manager5");

                    // Assigning Amenities to Properties
                    System.out.println();
                    System.out.println(" *** Assigning Amenities to Properties...");

                    // Property 1 - Apartment
                    propertyAmenityService.addPropertyAmenity(new PropertyAmenity(1L, 1L, 1L));  // WiFi
                    propertyAmenityService.addPropertyAmenity(new PropertyAmenity(2L, 1L, 6L));  // Air Conditioning
                    propertyAmenityService.addPropertyAmenity(new PropertyAmenity(3L, 1L, 7L));  // Heating
                    propertyAmenityService.addPropertyAmenity(new PropertyAmenity(4L, 1L, 3L));  // Elevator
                    propertyAmenityService.addPropertyAmenity(new PropertyAmenity(5L, 1L, 15L)); // Fully Equipped Kitchen
                    // Property 2 - House
                    propertyAmenityService.addPropertyAmenity(new PropertyAmenity(6L, 2L, 1L));  // WiFi
                    propertyAmenityService.addPropertyAmenity(new PropertyAmenity(7L, 2L, 6L));  // Air Conditioning
                    propertyAmenityService.addPropertyAmenity(new PropertyAmenity(8L, 2L, 7L));  // Heating
                    propertyAmenityService.addPropertyAmenity(new PropertyAmenity(9L, 2L, 13L)); // Ocean View
                    propertyAmenityService.addPropertyAmenity(new PropertyAmenity(10L, 2L, 14L)); // Mountain View
                    propertyAmenityService.addPropertyAmenity(new PropertyAmenity(11L, 2L, 19L)); // BBQ Grill
                    propertyAmenityService.addPropertyAmenity(new PropertyAmenity(12L, 2L, 30L)); // Outdoor Pool
                    // Property 3 - Apartment
                    propertyAmenityService.addPropertyAmenity(new PropertyAmenity(13L, 3L, 1L)); // WiFi
                    propertyAmenityService.addPropertyAmenity(new PropertyAmenity(14L, 3L, 6L)); // Air Conditioning
                    propertyAmenityService.addPropertyAmenity(new PropertyAmenity(15L, 3L, 7L)); // Heating
                    propertyAmenityService.addPropertyAmenity(new PropertyAmenity(16L, 3L, 3L)); // Elevator
                    propertyAmenityService.addPropertyAmenity(new PropertyAmenity(17L, 3L, 15L)); // Fully Equipped Kitchen
                    // Property 4 - Hotel Room (The Overlook Hotel)
                    propertyAmenityService.addPropertyAmenity(new PropertyAmenity(18L, 4L, 1L)); // WiFi
                    propertyAmenityService.addPropertyAmenity(new PropertyAmenity(19L, 4L, 6L)); // Air Conditioning
                    propertyAmenityService.addPropertyAmenity(new PropertyAmenity(20L, 4L, 7L)); // Heating
                    propertyAmenityService.addPropertyAmenity(new PropertyAmenity(21L, 4L, 12L)); // 24-Hour Security
                    propertyAmenityService.addPropertyAmenity(new PropertyAmenity(22L, 4L, 31L)); // Indoor Pool
                    // Property 5 - Other (Luxury Villa)
                    propertyAmenityService.addPropertyAmenity(new PropertyAmenity(23L, 5L, 1L));  // WiFi
                    propertyAmenityService.addPropertyAmenity(new PropertyAmenity(24L, 5L, 2L));  // Infinity Pool
                    propertyAmenityService.addPropertyAmenity(new PropertyAmenity(25L, 5L, 6L));  // Air Conditioning
                    propertyAmenityService.addPropertyAmenity(new PropertyAmenity(26L, 5L, 7L));  // Heating
                    propertyAmenityService.addPropertyAmenity(new PropertyAmenity(27L, 5L, 19L)); // BBQ Grill
                    propertyAmenityService.addPropertyAmenity(new PropertyAmenity(28L, 5L, 20L)); // Hot Tub
                    propertyAmenityService.addPropertyAmenity(new PropertyAmenity(29L, 5L, 30L)); // Outdoor Pool
                    // Property 6 - Room (Full Moon Party)
                    propertyAmenityService.addPropertyAmenity(new PropertyAmenity(30L, 6L, 1L));  // WiFi
                    propertyAmenityService.addPropertyAmenity(new PropertyAmenity(31L, 6L, 6L));  // Air Conditioning
                    propertyAmenityService.addPropertyAmenity(new PropertyAmenity(32L, 6L, 7L));  // Heating
                    // Property 7 - Commercial (Office)
                    propertyAmenityService.addPropertyAmenity(new PropertyAmenity(33L, 7L, 1L));  // WiFi
                    propertyAmenityService.addPropertyAmenity(new PropertyAmenity(34L, 7L, 3L));  // Elevator
                    propertyAmenityService.addPropertyAmenity(new PropertyAmenity(35L, 7L, 17L)); // Private Entrance
                    // Property 8 - Hotel Room (Business Accommodation)
                    propertyAmenityService.addPropertyAmenity(new PropertyAmenity(36L, 8L, 1L));  // WiFi
                    propertyAmenityService.addPropertyAmenity(new PropertyAmenity(37L, 8L, 6L));  // Air Conditioning
                    propertyAmenityService.addPropertyAmenity(new PropertyAmenity(38L, 8L, 7L));  // Heating
                    propertyAmenityService.addPropertyAmenity(new PropertyAmenity(39L, 8L, 12L)); // 24-Hour Security
                    // Property 9 - Hotel Room (Mediterranean Suite)
                    propertyAmenityService.addPropertyAmenity(new PropertyAmenity(40L, 9L, 1L));  // WiFi
                    propertyAmenityService.addPropertyAmenity(new PropertyAmenity(41L, 9L, 6L));  // Air Conditioning
                    propertyAmenityService.addPropertyAmenity(new PropertyAmenity(42L, 9L, 7L));  // Heating
                    propertyAmenityService.addPropertyAmenity(new PropertyAmenity(43L, 9L, 13L)); // Ocean View
                    propertyAmenityService.addPropertyAmenity(new PropertyAmenity(44L, 9L, 29L)); // Rooftop Terrace
                    // Property 10 - House (Pattaya Trip)
                    propertyAmenityService.addPropertyAmenity(new PropertyAmenity(45L, 10L, 1L));  // WiFi
                    propertyAmenityService.addPropertyAmenity(new PropertyAmenity(46L, 10L, 6L));  // Air Conditioning
                    propertyAmenityService.addPropertyAmenity(new PropertyAmenity(47L, 10L, 7L));  // Heating
                    propertyAmenityService.addPropertyAmenity(new PropertyAmenity(48L, 10L, 19L)); // BBQ Grill
                    propertyAmenityService.addPropertyAmenity(new PropertyAmenity(49L, 10L, 30L)); // Outdoor Pool
                    // Property 11 - Commercial (Event Venue)
                    propertyAmenityService.addPropertyAmenity(new PropertyAmenity(50L, 11L, 1L));  // WiFi
                    propertyAmenityService.addPropertyAmenity(new PropertyAmenity(51L, 11L, 3L));  // Elevator
                    propertyAmenityService.addPropertyAmenity(new PropertyAmenity(52L, 11L, 17L)); // Private Entrance
                    propertyAmenityService.addPropertyAmenity(new PropertyAmenity(53L, 11L, 25L)); // Home Theater
                    propertyAmenityService.addPropertyAmenity(new PropertyAmenity(54L, 11L, 23L)); // Playground
                    // Property 12 - Hotel Room (Presidential Suite)
                    propertyAmenityService.addPropertyAmenity(new PropertyAmenity(55L, 12L, 1L));  // WiFi
                    propertyAmenityService.addPropertyAmenity(new PropertyAmenity(56L, 12L, 6L));  // Air Conditioning
                    propertyAmenityService.addPropertyAmenity(new PropertyAmenity(57L, 12L, 7L));  // Heating
                    propertyAmenityService.addPropertyAmenity(new PropertyAmenity(58L, 12L, 12L)); // 24-Hour Security
                    propertyAmenityService.addPropertyAmenity(new PropertyAmenity(59L, 12L, 20L)); // Hot Tub
                    propertyAmenityService.addPropertyAmenity(new PropertyAmenity(60L, 12L, 31L)); // Indoor Pool
                    propertyAmenityService.addPropertyAmenity(new PropertyAmenity(61L, 12L, 32L)); // Outdoor Pool
                    System.out.println("   * Successfully distributed Amenities across Properties");
                    System.out.println();
                    System.out.println(" *** TEST DATABASE GENERATION COMPLETE.");
                }
            }
            sc.close();
        } else {
            System.out.println(" *** Sample database creation disabled; to enable, please delete the NumericalConfig named ProposeSampleDatabaseCreation from the database or set its value to 1.00.");
        }
    }

    public String encryptCVV(Long userId, UserType userType, char[] cvv) throws Exception {
//        System.out.println("    -----     Started CVV encryption");
        try {
//            System.out.println("      ---   a) Initiated the encryptCVV method");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
//            System.out.println("      ---   b) Created a Cipher: " + cipher);
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(128);
            SecretKey secretKey = keyGenerator.generateKey();
            byte[] aesKeyBytes = secretKey.getEncoded();
//            System.out.println("      ---   c) Generated AES Key (Base64 Encoded): " + Base64.getEncoder().encodeToString(aesKeyBytes));
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
//            System.out.println("      ---   d) Cipher.init triggered successfully");
            byte[] encryptedCVVBytes = cipher.doFinal(new String(cvv).getBytes());
//            System.out.println("      ---   e) Byte array created: " + Arrays.toString(encryptedCVVBytes));
            numericDataMappingService.saveCVVSecretKey(userId, userType, secretKey);
//            System.out.println("      ---   f) Saved the secret key " + secretKey.toString() + " to the database");
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
//        System.out.println("    -----     Started card number encryption");
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
//            System.out.println("    -----     a) Created a Key Generator");
            keyGenerator.init(128);
//            System.out.println("    -----     b) Initialized a Key Generator");
            SecretKey secretKey = keyGenerator.generateKey();
//            System.out.println("    -----     c) Created a SecretKey object: " + secretKey.toString());
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
//            System.out.println("    -----     d) Created a Cipher");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
//            System.out.println("    -----     e) Initialized a Cipher");
            byte[] encryptedCardNumberBytes = cipher.doFinal(cardNumber.getBytes());
//            System.out.println("    -----     f) Assigned an encryptedCardNumberBytes array: " + encryptedCardNumberBytes.toString());
            numericDataMappingService.saveCardNumberSecretKey(userId, userType, secretKey);
//            System.out.println("    -----     п) Saved the secret key for the user's card");
            return Base64.getEncoder().encodeToString(encryptedCardNumberBytes);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException | IllegalBlockSizeException e) {
            System.out.println("Error encrypting card number: " + e.getMessage());
        }
        return null;
    }
}