package lv.emendatus.Destiny_PropMan.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lv.emendatus.Destiny_PropMan.domain.entity.Admin;
import lv.emendatus.Destiny_PropMan.domain.entity.NumericalConfig;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.NumConfigType;
import lv.emendatus.Destiny_PropMan.repository.interfaces.*;
import lv.emendatus.Destiny_PropMan.service.implementation.JpaAdminAccountsService;
import lv.emendatus.Destiny_PropMan.service.implementation.JpaNumericalConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


@Service
public class DatabasePurgeUtility {
    private final PasswordEncoder passwordEncoder;
    @Autowired
    private JpaAdminAccountsService adminAccountsService;
    @Autowired
    private JpaNumericalConfigService configService;
    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private TokenResetterRepository tokenResetterRepository;
    @Autowired
    private PropertyAmenityRepository propertyAmenityRepository;
    @Autowired
    private AmenityRepository amenityRepository;
    @Autowired
    private BillRepository billRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private ClaimRepository claimRepository;
    @Autowired
    private CurrencyRepository currencyRepository;
    @Autowired
    private EarlyTerminationRequestRepository earlyTerminationRequestRepository;
    @Autowired
    private LeasingHistoryRepository leasingHistoryRepository;
    @Autowired
    private ManagerRepository managerRepository;
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private NumericDataMappingRepository numericDataMappingRepository;
    @Autowired
    private PayoutRepository payoutRepository;
    @Autowired
    private PropertyRepository propertyRepository;
    @Autowired
    private PropertyDiscountRepository propertyDiscountRepository;
    @Autowired
    private PropertyLockRepository propertyLockRepository;
    @Autowired
    private PropertyRatingRepository propertyRatingRepository;
    @Autowired
    private RefundRepository refundRepository;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private TenantRepository tenantRepository;
    @Autowired
    private TenantFavoritesRepository tenantFavoritesRepository;
    @Autowired
    private TenantPaymentRepository tenantPaymentRepository;
    @Autowired
    private TenantRatingRepository tenantRatingRepository;
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private KeyLinkRepository keyLinkRepository;

    public DatabasePurgeUtility(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public boolean purgeDatabase() {
        boolean purgeSucceeded = false;
        System.out.println();
        System.out.println("This utility will erase all records from the program's database.");
        System.out.println("The DefaultAdmin user and the NumericalConfig responsible for prompting the user whether to generate a sample database shall be retained.");
        System.out.println("Please enter the DefaultAdmin password to proceed");

        boolean purgeAttemptConfigPresent = false;
        Double attemptCountLeft = 3.00;
        Optional<NumericalConfig> purgeAttemptConfigOpt = configService.getNumericalConfigByName("RemainingPurgeAttemptCount");
        if (purgeAttemptConfigOpt.isPresent()) {
            purgeAttemptConfigPresent = true;
            attemptCountLeft = purgeAttemptConfigOpt.get().getValue();
        } else {
            NumericalConfig config = new NumericalConfig();
            config.setType(NumConfigType.SYSTEM_SETTING);
            config.setValue(3.00);
            config.setName("RemainingPurgeAttemptCount");
            configService.addNumericalConfig(config);
        }

        Optional<Admin> defaultAdminOptional = adminAccountsService.findByLogin("DefaultAdmin");
        if (defaultAdminOptional.isPresent()) {
            Scanner scanner = new Scanner(System.in);
            String input = scanner.nextLine();
            while (!passwordEncoder.matches(input, defaultAdminOptional.get().getPassword()) && attemptCountLeft > 0) {
                attemptCountLeft--;
                if (purgeAttemptConfigOpt.isPresent()) {
                    NumericalConfig config = purgeAttemptConfigOpt.get();
                    config.setValue(attemptCountLeft);
                    configService.addNumericalConfig(config);
                }
                System.out.println("Access denied: wrong password. Enter the Default Admin's password to proceed");
                System.out.println("You have " + attemptCountLeft.intValue() + " attempt(s) left");
                input = scanner.nextLine();
            }
            if (attemptCountLeft < 1) {
                System.out.println("Access sealed off after 3 failed attempts.");
            } else {
                System.out.println("Password accepted. Clearing the database...");
                // IMMEDIATE DATABASE PURGING LOGIC HERE
                List<Admin> admins = adminAccountsService.findAll();
                System.out.println("Deleting Admins except for DefaultAdmin");
                Optional<Admin> optionalAdmin = adminAccountsService.findByLogin("DefaultAdmin");
                if (optionalAdmin.isPresent()) {
                    Admin preservedAdmin = optionalAdmin.get();
                    String adminLogin = preservedAdmin.getLogin();
                    String adminPassword = preservedAdmin.getPassword();
                    String adminName = preservedAdmin.getName();
                    String adminEmail = preservedAdmin.getEmail();
                    List<String> adminIPs = preservedAdmin.getKnownIps();
                    adminRepository.deleteAll();
                    entityManager.createNativeQuery("ALTER TABLE admins AUTO_INCREMENT = 1").executeUpdate();
                    Admin addedAdmin = new Admin();
                    addedAdmin.setLogin(adminLogin);
                    addedAdmin.setPassword(adminPassword);
                    addedAdmin.setName(adminName);
                    addedAdmin.setEmail(adminEmail);
                    addedAdmin.setKnownIps(adminIPs);
                    adminAccountsService.addAdmin(addedAdmin);
                } else {
                    System.out.println("Could not obtain the Default Admin!");
                }

                System.out.println("Deleting NumericalConfigs except for the one responsible for proposing the creation of a sample database");
                List<NumericalConfig> configs = configService.getAllNumericalConfigs();
                for (NumericalConfig config : configs) {
                    if (!config.getName().equals("ProposeSampleDatabaseCreation")) {
                        configService.deleteNumericalConfig(config.getId());
                    }
                }
                Optional<NumericalConfig> optionalConfig = configService.getNumericalConfigByName("ProposeSampleDatabaseCreation");
                if (optionalConfig.isPresent()) {
                    NumericalConfig preservedConfig = optionalConfig.get();
                    Long id = preservedConfig.getId();
                    configService.deleteNumericalConfig(id);
                    preservedConfig.setId(null);
                    entityManager.createNativeQuery("ALTER TABLE numerical_config AUTO_INCREMENT = 1").executeUpdate();
                    configService.addNumericalConfig(preservedConfig);
                }
                System.out.println("Deleting Token Resetters...");
                tokenResetterRepository.deleteAll();
                entityManager.createNativeQuery("ALTER TABLE token_resetters AUTO_INCREMENT = 1").executeUpdate();
                System.out.println("Deleting Property-Amenity Relations...");
                propertyAmenityRepository.deleteAll();
                entityManager.createNativeQuery("ALTER TABLE property_amenities AUTO_INCREMENT = 1").executeUpdate();
                System.out.println("Deleting KeyLinks...");
                keyLinkRepository.deleteAll();
                entityManager.createNativeQuery("ALTER TABLE key_links AUTO_INCREMENT = 1").executeUpdate();
                System.out.println("Deleting Early Termination Requests...");
                earlyTerminationRequestRepository.deleteAll();
                entityManager.createNativeQuery("ALTER TABLE early_termination_requests AUTO_INCREMENT = 1").executeUpdate();
                System.out.println("Deleting Amenities...");
                amenityRepository.deleteAll();
                entityManager.createNativeQuery("ALTER TABLE amenities AUTO_INCREMENT = 1").executeUpdate();
                System.out.println("Deleting Reviews...");
                reviewRepository.deleteAll();
                entityManager.createNativeQuery("ALTER TABLE review AUTO_INCREMENT = 1").executeUpdate();
                System.out.println("Deleting Tenant Ratings...");
                tenantRatingRepository.deleteAll();
                entityManager.createNativeQuery("ALTER TABLE tenant_ratings AUTO_INCREMENT = 1").executeUpdate();
                System.out.println("Deleting Property Ratings...");
                propertyRatingRepository.deleteAll();
                entityManager.createNativeQuery("ALTER TABLE property_ratings AUTO_INCREMENT = 1").executeUpdate();
                System.out.println("Deleting Property Locks...");
                propertyLockRepository.deleteAll();
                entityManager.createNativeQuery("ALTER TABLE property_locks AUTO_INCREMENT = 1").executeUpdate();
                System.out.println("Deleting Claims...");
                claimRepository.deleteAll();
                entityManager.createNativeQuery("ALTER TABLE claims AUTO_INCREMENT = 1").executeUpdate();
                System.out.println("Deleting Tenant Favorites...");
                tenantFavoritesRepository.deleteAll();
                entityManager.createNativeQuery("ALTER TABLE tenant_favorites AUTO_INCREMENT = 1").executeUpdate();
                System.out.println("Deleting Messages...");
                messageRepository.deleteAll();
                entityManager.createNativeQuery("ALTER TABLE messages AUTO_INCREMENT = 1").executeUpdate();
                System.out.println("Deleting Property Discounts...");
                propertyDiscountRepository.deleteAll();
                entityManager.createNativeQuery("ALTER TABLE property_discounts AUTO_INCREMENT = 1").executeUpdate();
                System.out.println("Deleting NumericDataMappings...");
                numericDataMappingRepository.deleteAll();
                entityManager.createNativeQuery("ALTER TABLE numerical_data_mapping AUTO_INCREMENT = 1").executeUpdate();
                System.out.println("Deleting Refunds...");
                refundRepository.deleteAll();
                entityManager.createNativeQuery("ALTER TABLE refunds AUTO_INCREMENT = 1").executeUpdate();
                System.out.println("Deleting Payouts...");
                payoutRepository.deleteAll();
                entityManager.createNativeQuery("ALTER TABLE payouts AUTO_INCREMENT = 1").executeUpdate();
                System.out.println("Deleting Bookings...");
                bookingRepository.deleteAll();
                entityManager.createNativeQuery("ALTER TABLE booking AUTO_INCREMENT = 1").executeUpdate();
                System.out.println("Deleting Properties...");
                propertyRepository.deleteAll();
                entityManager.createNativeQuery("ALTER TABLE properties AUTO_INCREMENT = 1").executeUpdate();
                System.out.println("Deleting Leasing Histories...");
                leasingHistoryRepository.deleteAll();
                entityManager.createNativeQuery("ALTER TABLE leasing_history AUTO_INCREMENT = 1").executeUpdate();
                System.out.println("Deleting Tenant Payments...");
                tenantPaymentRepository.deleteAll();
                entityManager.createNativeQuery("ALTER TABLE tenant_payments AUTO_INCREMENT = 1").executeUpdate();
                System.out.println("Deleting Bills...");
                billRepository.deleteAll();
                entityManager.createNativeQuery("ALTER TABLE bill AUTO_INCREMENT = 1").executeUpdate();
                System.out.println("Deleting Managers...");
                managerRepository.deleteAll();
                entityManager.createNativeQuery("ALTER TABLE managers AUTO_INCREMENT = 1").executeUpdate();
                System.out.println("Deleting Tenants...");
                tenantRepository.deleteAll();
                entityManager.createNativeQuery("ALTER TABLE tenant AUTO_INCREMENT = 1").executeUpdate();
                System.out.println("Deleting Currencies...");
                currencyRepository.deleteAll();
                entityManager.createNativeQuery("ALTER TABLE currencies AUTO_INCREMENT = 1").executeUpdate();
                System.out.println("Truncating Admin authorities...");
                entityManager.createNativeQuery("TRUNCATE TABLE admin_authorities").executeUpdate();
                System.out.println("Truncating Manager authorities...");
                entityManager.createNativeQuery("TRUNCATE TABLE manager_authorities").executeUpdate();
                System.out.println("Truncating Tenant authorities...");
                entityManager.createNativeQuery("TRUNCATE TABLE tenant_authorities").executeUpdate();
                if (optionalAdmin.isPresent()) {
                    Admin updatedAdmin = optionalAdmin.get();
                    List<GrantedAuthority> adminAuthorities = new ArrayList<>();
                    adminAuthorities.add(new SimpleGrantedAuthority("ADMIN"));
                    updatedAdmin.setAuthorities(adminAuthorities);
                    adminAccountsService.addAdmin(updatedAdmin);
                } else {
                    System.out.println("Could not obtain the Default Admin!");
                }
                Path directory = Paths.get("./Extrastore");
                try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(directory)) {
                    for (Path path : directoryStream) {
                        try {
                            Files.delete(path);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    System.out.println("Deleted all files from the Extrastore directory.");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                purgeSucceeded = true;
            }
        } else {
            System.out.println("Could not retrieve the DefaultAdmin");
        }
        return purgeSucceeded;
    }
}
