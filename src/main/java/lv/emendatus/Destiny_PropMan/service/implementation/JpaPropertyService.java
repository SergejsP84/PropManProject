package lv.emendatus.Destiny_PropMan.service.implementation;

import lv.emendatus.Destiny_PropMan.domain.entity.*;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.PropertyStatus;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.PropertyType;
import lv.emendatus.Destiny_PropMan.exceptions.EntityNotFoundException;
import lv.emendatus.Destiny_PropMan.exceptions.ManagerNotFoundException;
import lv.emendatus.Destiny_PropMan.exceptions.PropertyNotFoundException;
import lv.emendatus.Destiny_PropMan.exceptions.TenantNotFoundException;
import lv.emendatus.Destiny_PropMan.repository.interfaces.*;
import lv.emendatus.Destiny_PropMan.service.interfaces.PropertyService;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class JpaPropertyService implements PropertyService {
    private final PropertyRepository propertyRepository;
    private final AmenityRepository amenityRepository;
    private final PropertyAmenityRepository propertyAmenityRepository;
    private final BookingRepository bookingRepository;
    private final BillRepository billRepository;
    private final JpaBookingService bookingService;
    private final JpaManagerService managerService;
    private final JpaTenantService tenantService;
    private final JpaBillService billService;

    private final Logger LOGGER = LogManager.getLogger(JpaPropertyService.class);

    public JpaPropertyService(PropertyRepository propertyRepository, AmenityRepository amenityRepository, PropertyAmenityRepository propertyAmenityRepository, BookingRepository bookingRepository, BillRepository billRepository, JpaBookingService bookingService, JpaManagerService managerService, JpaTenantService tenantService, JpaBillService billService) {
        this.propertyRepository = propertyRepository;
        this.amenityRepository = amenityRepository;
        this.propertyAmenityRepository = propertyAmenityRepository;
        this.bookingRepository = bookingRepository;
        this.billRepository = billRepository;
        this.bookingService = bookingService;
        this.managerService = managerService;
        this.tenantService = tenantService;
        this.billService = billService;
    }

    @Override
    public List<Property> getAllProperties() {
        return propertyRepository.findAll();
    }

    @Override
    public Optional<Property> getPropertyById(Long id) {
        return propertyRepository.findById(id);
    }

    @Override
    public void addProperty(Property property) {
        propertyRepository.save(property);
    }

    @Override
    public void deleteProperty(Long id) {
        propertyRepository.deleteById(id);
    }

    @Override
    public List<Property> getPropertiesByLocation(String location) {
        return propertyRepository.findByAddressContainingIgnoreCaseOrCountryContainingIgnoreCaseOrSettlementContainingIgnoreCase(
                location, location, location);
    }

    @Override
    public List<Property> getPropertiesByType(PropertyType type) {
        return propertyRepository.findByType(type);
    }

    @Override
    public List<Property> getPropertiesByDailyPriceRange(double minPrice, double maxPrice) {
        return propertyRepository.findByPricePerDayBetween(minPrice, maxPrice);
    }

    @Override
    public List<Property> getPropertiesByWeeklyPriceRange(double minPrice, double maxPrice) {
        return propertyRepository.findByPricePerWeekBetween(minPrice, maxPrice);
    }

    @Override
    public List<Property> getPropertiesByMonthlyPriceRange(double minPrice, double maxPrice) {
        return propertyRepository.findByPricePerMonthBetween(minPrice, maxPrice);
    }

    @Override
    public List<Property> getAvailableProperties(LocalDate startDate, LocalDate endDate) {
        Timestamp startTs = Timestamp.valueOf(startDate.atStartOfDay());
        Timestamp endTs = Timestamp.valueOf(endDate.atStartOfDay());
        Set<Long> occupiedPropertyIds = bookingRepository.findOverlappingBookings(startTs, endTs).stream()
                .map(booking -> booking.getProperty().getId())
                .collect(Collectors.toSet());
        return getAllProperties().stream()
                .filter(property -> !occupiedPropertyIds.contains(property.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public Set<Property> getPropertiesWithAmenities(List<Long> amenityIds) {
        List<Long> propertyIds = propertyAmenityRepository.findPropertyIdsWithAllAmenities(amenityIds, amenityIds.size());
        return propertyIds.stream()
                .map(propertyRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
    }

    public void updatePropertyAddress(Long id, String newAddress) {
        Optional<Property> optionalProperty = getPropertyById(id);
        if (optionalProperty.isPresent()) {
            Property property = optionalProperty.get();
            property.setAddress(newAddress);
            addProperty(property);
        } else {
            LOGGER.log(Level.ERROR, "No property with the {} ID exists in the database.", id);
            throw new PropertyNotFoundException("No property found with ID: " + id);
        }
    }

    @Override
    public void updatePropertySettlement(Long id, String newSettlement) {
        Optional<Property> optionalProperty = getPropertyById(id);
        if (optionalProperty.isPresent()) {
            Property property = optionalProperty.get();
            property.setSettlement(newSettlement);
            addProperty(property);
        } else {
            LOGGER.log(Level.ERROR, "No property with the {} ID exists in the database.", id);
            throw new PropertyNotFoundException("No property found with ID: " + id);
        }
    }

    @Override
    public void updatePropertyCountry(Long id, String newCountry) {
        Optional<Property> optionalProperty = getPropertyById(id);
        if (optionalProperty.isPresent()) {
            Property property = optionalProperty.get();
            property.setCountry(newCountry);
            addProperty(property);
        } else {
            LOGGER.log(Level.ERROR, "No property with the {} ID exists in the database.", id);
            throw new PropertyNotFoundException("No property found with ID: " + id);
        }
    }

    @Override
    public void updatePropertyPricePerDay(Long id, double newPrice) {
        Optional<Property> optionalProperty = getPropertyById(id);
        if (optionalProperty.isPresent()) {
            Property property = optionalProperty.get();
            property.setPricePerDay(newPrice);
            addProperty(property);
        } else {
            LOGGER.log(Level.ERROR, "No property with the {} ID exists in the database.", id);
            throw new PropertyNotFoundException("No property found with ID: " + id);
        }
    }

    @Override
    public void updatePropertyPricePerWeek(Long id, double newPrice) {
        Optional<Property> optionalProperty = getPropertyById(id);
        if (optionalProperty.isPresent()) {
            Property property = optionalProperty.get();
            property.setPricePerWeek(newPrice);
            addProperty(property);
        } else {
            LOGGER.log(Level.ERROR, "No property with the {} ID exists in the database.", id);
            throw new PropertyNotFoundException("No property found with ID: " + id);
        }
    }

    @Override
    public void updatePropertyPricePerMonth(Long id, double newPrice) {
        Optional<Property> optionalProperty = getPropertyById(id);
        if (optionalProperty.isPresent()) {
            Property property = optionalProperty.get();
            property.setPricePerMonth(newPrice);
            addProperty(property);
        } else {
            LOGGER.log(Level.ERROR, "No property with the {} ID exists in the database.", id);
            throw new PropertyNotFoundException("No property found with ID: " + id);
        }
    }

    @Override
    @Transactional
    public void addAmenityToProperty(Long propertyId, Long amenityId) {
        Optional<Property> optionalProperty = propertyRepository.findById(propertyId);
        if (optionalProperty.isPresent()) {
            PropertyAmenity propertyAmenity = new PropertyAmenity();
            propertyAmenity.setProperty_id(propertyId);
            propertyAmenity.setAmenity_id(amenityId);
            propertyAmenityRepository.save(propertyAmenity);
        } else {
            LOGGER.log(Level.ERROR, "No property with the {} ID exists in the database.", propertyId);
            throw new PropertyNotFoundException("No property found with ID: " + propertyId);
        }
    }

    @Override
    @Transactional
    public void removeAmenityFromProperty(Long propertyId, Long amenityId) {
        Optional<Property> optionalProperty = propertyRepository.findById(propertyId);
        if (optionalProperty.isPresent()) {
            propertyAmenityRepository.deleteByPropertyIdAndAmenityId(propertyId, amenityId);
        } else {
            LOGGER.log(Level.ERROR, "No property with the {} ID exists in the database.", propertyId);
            throw new PropertyNotFoundException("No property found with ID: " + propertyId);
        }
    }

    @Override
    public void updateManager(Long propertyId, Long managerId) {
        Optional<Property> optionalProperty = getPropertyById(propertyId);
        Optional<Manager> optionalManager = managerService.getManagerById(managerId);
        if (optionalProperty.isPresent()) {
            if (optionalManager.isPresent()) {
                Property property = optionalProperty.get();
                Manager manager = optionalManager.get();
                property.setManager(manager);
                addProperty(property);
            } else {
                LOGGER.log(Level.ERROR, "No manager with the {} ID exists in the database.", managerId);
                throw new ManagerNotFoundException("No manager found with ID: " + managerId);
            }
        } else {
            LOGGER.log(Level.ERROR, "No property with the {} ID exists in the database.", propertyId);
            throw new PropertyNotFoundException("No property found with ID: " + propertyId);
        }
    }

    @Override
    public void setStatus(Long propertyId, PropertyStatus status) {
        Optional<Property> optionalProperty = getPropertyById(propertyId);
        if (optionalProperty.isPresent()) {
            Property property = optionalProperty.get();
            property.setStatus(status);
            addProperty(property);
        } else {
            LOGGER.log(Level.ERROR, "No property with the {} ID exists in the database.", propertyId);
            throw new PropertyNotFoundException("No property found with ID: " + propertyId);
        }
    }

    @Override
    public void updateSize(Long propertyId, Float newSizeM2) {
        Optional<Property> optionalProperty = getPropertyById(propertyId);
        if (optionalProperty.isPresent()) {
            Property property = optionalProperty.get();
            property.setSizeM2(newSizeM2);
            addProperty(property);
        } else {
            LOGGER.log(Level.ERROR, "No property with the {} ID exists in the database.", propertyId);
            throw new PropertyNotFoundException("No property found with ID: " + propertyId);
        }
    }

    @Override
    public void updateRating(Long propertyId, Float updatedRating) {
        Optional<Property> optionalProperty = getPropertyById(propertyId);
        if (optionalProperty.isPresent()) {
            Property property = optionalProperty.get();
            property.setRating(updatedRating);
            addProperty(property);
        } else {
            LOGGER.log(Level.ERROR, "No property with the {} ID exists in the database.", propertyId);
            throw new PropertyNotFoundException("No property found with ID: " + propertyId);
        }
    }

    @Override
    public void updateDescription(Long id, String newDescription) {
        Optional<Property> optionalProperty = getPropertyById(id);
        if (optionalProperty.isPresent()) {
            Property property = optionalProperty.get();
            property.setDescription(newDescription);
            addProperty(property);
        } else {
            LOGGER.log(Level.ERROR, "No property with the {} ID exists in the database.", id);
            throw new PropertyNotFoundException("No property found with ID: " + id);
        }
    }

    @Override
    public void assignTenantToProperty(Long propertyId, Long tenantId) {
        Optional<Property> optionalProperty = getPropertyById(propertyId);
        Optional<Tenant> optionalTenant = tenantService.getTenantById(tenantId);
        if (optionalProperty.isPresent()) {
            if (optionalTenant.isPresent()) {
                Property property = optionalProperty.get();
                Tenant tenant = optionalTenant.get();
                property.setTenant(tenant);
                tenant.setCurrentProperty(property);
                tenantService.addTenant(tenant);
                property.removeTenantReference();
                property.setTenant(tenant);
                addProperty(property);
                tenant.removePropertyReference();
            } else {
                LOGGER.log(Level.ERROR, "No tenant with the {} ID exists in the database.", tenantId);
                throw new TenantNotFoundException("No tenant found with ID: " + tenantId);
            }
        } else {
            LOGGER.log(Level.ERROR, "No property with the {} ID exists in the database.", propertyId);
            throw new PropertyNotFoundException("No property found with ID: " + propertyId);
        }
    }

    @Override
    public Tenant getCurrentTenant(Long propertyId) {
        Optional<Property> optionalProperty = getPropertyById(propertyId);
        if (optionalProperty.isPresent()) {
            return optionalProperty.get().getTenant();
        } else {
            LOGGER.log(Level.ERROR, "No property with the {} ID exists in the database.", propertyId);
            throw new PropertyNotFoundException("No property found with ID: " + propertyId);
        }
    }

    @Override
    public void removeTenantFromProperty(Long propertyId) {
        Optional<Property> optionalProperty = getPropertyById(propertyId);
        if (optionalProperty.isPresent()) {
            Property property = optionalProperty.get();
            if (property.getTenant() != null) {
                Optional<Tenant> tenant = tenantService.getTenantById(property.getTenant().getId());
                if (tenant.isPresent()) {
                    tenant.get().setCurrentProperty(null);
                    tenantService.addTenant(tenant.get());
                }
            }
            property.setTenant(null);
            addProperty(property);
        } else {
            LOGGER.log(Level.ERROR, "No property with the {} ID exists in the database.", propertyId);
            throw new PropertyNotFoundException("No property found with ID: " + propertyId);
        }
    }

    @Override
    public Set<Booking> getPropertyBookings(Long propertyId) {
        Optional<Property> optionalProperty = getPropertyById(propertyId);
        if (optionalProperty.isPresent()) {
            return bookingService.getBookingsByProperty(optionalProperty.get());
        } else {
            LOGGER.log(Level.ERROR, "No property with the {} ID exists in the database.", propertyId);
            throw new PropertyNotFoundException("No property found with ID: " + propertyId);
        }
    }

    @Override
    public void addBookingToProperty(Long propertyId, Long bookingId) {
        Optional<Property> optionalProperty = getPropertyById(propertyId);
        Optional<Booking> optionalBooking = bookingService.getBookingById(bookingId);
        if (optionalProperty.isPresent() && optionalBooking.isPresent()) {
            Property property = optionalProperty.get();
            Booking booking = optionalBooking.get();
            booking.setProperty(property);
            bookingService.addBooking(booking);
            Set<Booking> existingBookings = property.getBookings() != null ? property.getBookings() : new HashSet<>();
            existingBookings.add(booking);
            property.setBookings(existingBookings);
            propertyRepository.save(property);
        } else {
            LOGGER.log(Level.ERROR, "Missing property or booking");
            throw new EntityNotFoundException("Either the property or the booking could not be found");
        }
    }

    @Override
    public void removeBookingFromProperty(Long propertyId, Long bookingId) {
        Optional<Property> optionalProperty = getPropertyById(propertyId);
        Optional<Booking> optionalBooking = bookingService.getBookingById(bookingId);
        if (optionalProperty.isPresent() && optionalBooking.isPresent()) {
            Property property = optionalProperty.get();
            Set<Booking> existingBookings = property.getBookings() != null ? property.getBookings() : new HashSet<>();
            existingBookings.removeIf(booking -> booking.equals(optionalBooking.get()));
            property.setBookings(existingBookings);
            propertyRepository.save(property);
        } else {
            LOGGER.log(Level.ERROR, "Missing property or booking");
            throw new EntityNotFoundException("Either the property or the booking could not be found");
        }
    }

    @Override
    public Set<Bill> getPropertyBills(Long propertyId) {
        Optional<Property> optionalProperty = getPropertyById(propertyId);
        if (optionalProperty.isPresent()) {
            return new HashSet<>(billService.getBillsByProperty(optionalProperty.get()));
        } else {
            LOGGER.log(Level.ERROR, "No property with the {} ID exists in the database.", propertyId);
            throw new PropertyNotFoundException("No property found with ID: " + propertyId);
        }
    }

    @Override
    public void addBillToProperty(Long propertyId, Long billId) {
        Optional<Property> optionalProperty = getPropertyById(propertyId);
        Optional<Bill> optionalBill = billService.getBillById(billId);
        if (optionalProperty.isPresent() && optionalBill.isPresent()) {
            Property property = optionalProperty.get();
            Bill bill = optionalBill.get();
            bill.setProperty(property);
            billService.addBill(bill);
            Set<Bill> existingBills = property.getBills() != null ? property.getBills() : new HashSet<>();
            existingBills.add(bill);
            property.setBills(existingBills);
            propertyRepository.save(property);
        } else {
            LOGGER.log(Level.ERROR, "Missing property or bill");
            throw new EntityNotFoundException("Either the property or the bill could not be found");
        }
    }

    @Override
    public void removeBillFromProperty(Long propertyId, Long billId) {
        Optional<Property> optionalProperty = getPropertyById(propertyId);
        Optional<Bill> optionalBill = billService.getBillById(billId);
        if (optionalProperty.isPresent() && optionalBill.isPresent()) {
            Set<Bill> updated = new HashSet<>(billService.getBillsByProperty(optionalProperty.get()));
            optionalBill.get().setProperty(null);
            updated.remove(optionalBill.get());
            optionalProperty.get().setBills(updated);
            propertyRepository.save(optionalProperty.get());
            billRepository.save(optionalBill.get());
        } else {
            LOGGER.log(Level.ERROR, "Missing property or bill");
            throw new EntityNotFoundException("Either the property or the bill could not be found");
        }
    }

    // AUXILIARY METHOD
    @Override
    public List<Property> getPropertiesByManager(Long managerId) {
        return propertyRepository.findByManager_Id(managerId);
    }
}
