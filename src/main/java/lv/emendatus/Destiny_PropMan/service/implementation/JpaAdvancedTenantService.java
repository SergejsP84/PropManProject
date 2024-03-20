package lv.emendatus.Destiny_PropMan.service.implementation;

import lv.emendatus.Destiny_PropMan.domain.dto.profile.BookingHistoryDTO;
import lv.emendatus.Destiny_PropMan.domain.dto.profile.LeasingHistoryDTO_Profile;
import lv.emendatus.Destiny_PropMan.domain.dto.profile.TenantDTO_Profile;
import lv.emendatus.Destiny_PropMan.domain.dto.view.*;
import lv.emendatus.Destiny_PropMan.domain.entity.*;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.ClaimStatus;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.ClaimantType;
import lv.emendatus.Destiny_PropMan.mapper.LeasingHistoryMapper;
import lv.emendatus.Destiny_PropMan.mapper.PropertyMapper;
import lv.emendatus.Destiny_PropMan.mapper.TenantMapper;
import lv.emendatus.Destiny_PropMan.repository.interfaces.PropertyRepository;
import lv.emendatus.Destiny_PropMan.repository.interfaces.TenantRepository;
import lv.emendatus.Destiny_PropMan.service.interfaces.AdvancedTenantService;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class JpaAdvancedTenantService implements AdvancedTenantService {

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private PropertyMapper propertyMapper;

    private final TenantRepository tenantRepository;
    private final TenantMapper tenantMapper;
    private final LeasingHistoryMapper leasingHistoryMapper;
    private final JpaLeasingHistoryService leasingHistoryService;
    private final JpaTenantFavoritesService favoritesService;
    private final JpaClaimService claimService;
    private final JpaBookingService bookingService;
    private final JpaTenantService tenantService;
    private final JpaNumericalConfigService configService;

    private final Logger LOGGER = LogManager.getLogger(JpaPropertyService.class);

    public JpaAdvancedTenantService(PropertyRepository propertyRepository, PropertyMapper propertyMapper, TenantRepository tenantRepository, TenantMapper tenantMapper, LeasingHistoryMapper leasingHistoryMapper, JpaLeasingHistoryService leasingHistoryService, JpaTenantFavoritesService favoritesService, JpaClaimService claimService, JpaBookingService bookingService, JpaTenantService tenantService, JpaNumericalConfigService configService) {
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
    }

    @Override
    public PropertiesForTenantsDTO getPropertyDetails(Long propertyId) {
        Optional<Property> propertyOptional = propertyRepository.findById(propertyId);
        if (propertyOptional.isPresent()) {
            return propertyMapper.propertyToPropertiesForTenantsDTO(propertyOptional.get());
        } else {
            LOGGER.log(Level.ERROR, "No property with the {} ID exists in the database.", propertyId);
            // TODO: Handle the case where the manager with the given ID is not found
            return new PropertiesForTenantsDTO();
        }
    }

    @Override
    public TenantDTO_Profile getTenantProfile(Long tenantId) {
        return null;
    }

    @Override
    public List<ManagersForTenantsDTO> getManagersForProperty(Long propertyId) {
        return null;
    }


    @Override
    public TenantDTO_Profile getTenantInformation(Long tenantId) {
        Optional<Tenant> tenantOptional = tenantRepository.findById(tenantId);
        if (tenantOptional.isPresent()) {
            Tenant tenant = tenantOptional.get();
            return TenantMapper.INSTANCE.toDTO(tenant);
        } else {
            // TODO Handle exception
            LOGGER.log(Level.ERROR, "No tenant with the {} ID exists in the database.", tenantId);
            return new TenantDTO_Profile();
        }
    }
    @Override
    public void updateTenantInformation(Long tenantId, TenantDTO_Profile updatedTenantInfo) {
        Optional<Tenant> tenantOptional = tenantRepository.findById(tenantId);
        if (tenantOptional.isPresent()) {
            Tenant existingTenant = tenantOptional.get();
            tenantMapper.updateTenantFromDTO(existingTenant, updatedTenantInfo);
            tenantRepository.save(existingTenant);
        } else {
            // TODO: Handle exception
            LOGGER.log(Level.ERROR, "No tenant with the {} ID exists in the database.", tenantId);
        }
    }
    @Override
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
            // TODO Handle exception
            LOGGER.log(Level.ERROR, "No tenant with the {} ID exists in the database.", tenantId);
            return null;
        }
    }


    @Override
    public void saveFavoriteProperty(FavoritePropertyDTO favoriteDTO) {
        Tenant tenant = tenantRepository.findById(favoriteDTO.getTenantId()).orElse(null);
        Property property = propertyRepository.findById(favoriteDTO.getPropertyId()).orElse(null);
        if (tenant != null && property != null) {
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
            // TODO: Exception - entity not found
        }
    }

    @Override
    public List<FavoritePropertyDTO_Profile> getFavoriteProperties(Long tenantId) {
        Tenant tenant = tenantRepository.findById(tenantId).orElse(null);
        if (tenant != null) {
            Optional<TenantFavorites> current = favoritesService.getTenantFavoritesByTenant(tenantId);
            if (current.isPresent()) {
                return current.get().getFavoritePropertyIDs().stream()
                        .map(propertyId -> propertyMapper.toFavoritePropertyDTO_Profile(propertyId, propertyRepository))
                        .collect(Collectors.toList());
            } else {
                LOGGER.log(Level.WARN, "No favorite properties listed for tenant with the ID {}.", tenantId);
                return Collections.emptyList();
            }
        } else {
            LOGGER.log(Level.WARN, "No tenant with the {} ID exists in the database.", tenantId);
            return Collections.emptyList();
        }
    }
    @Override
    public void removePropertyFromFavorites(Long tenantId, Long propertyId) {
        favoritesService.removePropertyFromFavorites(tenantId, propertyId);
    }

    @Override
    public void submitClaimfromTenant(Long bookingId, String description) {
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
                claim.setClaimantType(ClaimantType.TENANT);
                claim.setDescription(description);
                claimService.addClaim(claim);
                LOGGER.log(Level.INFO, "Your claim in regard to booking {} has been filed.", bookingId);
                System.out.println("Claim lodged successfully");
            }
        }
    }

}
