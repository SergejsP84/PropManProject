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
        List<Property> properties = getAllProperties();
        return properties.stream()
                .filter(property -> property.getAddress().contains(location)
                        || property.getCountry().contains(location)
                        || property.getSettlement().contains(location))
                .collect(Collectors.toList());
    }
    @Override
    public List<Property> getPropertiesByType(PropertyType type) {
        List<Property> properties = getAllProperties();
        return properties.stream()
                .filter(property -> property.getType().equals(type))
                .collect(Collectors.toList());
    }

    @Override
    public List<Property> getPropertiesByDailyPriceRange(double minPrice, double maxPrice) {
        List<Property> properties = getAllProperties();
        return properties.stream()
                .filter(property -> (property.getPricePerDay() >= minPrice && property.getPricePerDay() <= maxPrice))
                .collect(Collectors.toList());
    }

    @Override
    public List<Property> getPropertiesByWeeklyPriceRange(double minPrice, double maxPrice) {
        List<Property> properties = getAllProperties();
        return properties.stream()
                .filter(property -> (property.getPricePerWeek() >= minPrice && property.getPricePerWeek() <= maxPrice))
                .collect(Collectors.toList());
    }

    @Override
    public List<Property> getPropertiesByMonthlyPriceRange(double minPrice, double maxPrice) {
        List<Property> properties = getAllProperties();
        return properties.stream()
                .filter(property -> (property.getPricePerMonth() >= minPrice && property.getPricePerMonth() <= maxPrice))
                .collect(Collectors.toList());
    }

    @Override
    public List<Property> getAvailableProperties(LocalDate startDate, LocalDate endDate) {
        List<Property> properties = getAllProperties();
        List<Booking> bookingsWithinTime = bookingService.getBookingsByDateRangeWithOverlaps(startDate, endDate);
        List<Property> occupiedProperties = new ArrayList<>();
        for (Booking booking : bookingsWithinTime) {
            LocalDate bookingStartDate = booking.getStartDate().toLocalDateTime().toLocalDate();
            LocalDate bookingEndDate = booking.getEndDate().toLocalDateTime().toLocalDate();
            if (!(endDate.isBefore(bookingStartDate) || startDate.isAfter(bookingEndDate))) {
                occupiedProperties.add(booking.getProperty());
            }
        }
        properties.removeAll(occupiedProperties);
        return properties;
    }

    @Override
    public Set<Property> getPropertiesWithAmenities(List<Long> amenityIds) {
        List<PropertyAmenity> allAmenityRecords = propertyAmenityRepository.findAll();
        Map<Long, List<Long>> propertyAmenitiesMap = allAmenityRecords.stream()
                .collect(Collectors.groupingBy(PropertyAmenity::getProperty_id,
                        Collectors.mapping(PropertyAmenity::getAmenity_id, Collectors.toList())));

        return propertyAmenitiesMap.entrySet().stream()
                .filter(entry -> entry.getValue().containsAll(amenityIds))
                .map(entry -> propertyRepository.findById(entry.getKey()).orElse(null))
                .filter(Objects::nonNull)
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
    public void addAmenityToProperty(Long propertyId, Long amenityId) {
        PropertyAmenity propertyAmenity = new PropertyAmenity();
        Optional<Property> optionalProperty = propertyRepository.findById(propertyId);
        if (optionalProperty.isPresent()) {
            propertyAmenity.setProperty_id(propertyId);
            propertyAmenity.setAmenity_id(amenityId);
            propertyAmenityRepository.save(propertyAmenity);
        } else {
            LOGGER.log(Level.ERROR, "No property with the {} ID exists in the database.", propertyId);
            throw new PropertyNotFoundException("No property found with ID: " + propertyId);
        }
    }

    @Override
    public void removeAmenityFromProperty(Long propertyId, Long amenityId) {
        List<PropertyAmenity> propertyAmenities = propertyAmenityRepository.findAll();
        Optional<Property> optionalProperty = propertyRepository.findById(propertyId);
        if (optionalProperty.isPresent()) {
        Property property = optionalProperty.get();
            for (PropertyAmenity propertyAmenity : propertyAmenities) {
            if (propertyAmenity.getProperty_id().equals(propertyId)
                && propertyAmenity.getAmenity_id().equals(amenityId)) {
                propertyAmenityRepository.deleteById(propertyAmenity.getId());
                }
            }
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
                System.out.println("Tenant " + tenant.getFirstName() + " moved into Property " + property.getDescription());
                System.out.println("The property is now occupied by " + property.getTenant().getFirstName() + " " + property.getTenant().getLastName());
                tenant.setCurrentProperty(property);
                tenantService.addTenant(tenant);
                property.removeTenantReference();
                property.setTenant(tenant);
                addProperty(property);
                tenant.removePropertyReference(); // to avoid circular reference
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
            Tenant tenant = optionalProperty.get().getTenant();
            if (!tenant.equals(null)) {
                System.out.println("Current tenant: " + tenant.getFirstName() + " " + tenant.getLastName());
                return tenant;
            }
            else {
                System.out.println("These premises are unoccupied");
                return null;
            }
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
            if (!property.getTenant().equals(null)) {
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
    public void addBookingToProperty(Long propertyId, Long bookingIdd) {
        Optional<Property> optionalProperty = getPropertyById(propertyId);
        Optional<Booking> optionalBooking = bookingService.getBookingById(bookingIdd);
        if (optionalProperty.isPresent() && optionalBooking.isPresent()) {
            Property property = optionalProperty.get();
            Booking booking = optionalBooking.get();
            booking.setProperty(property);
            bookingService.addBooking(booking);
        } else {
            LOGGER.log(Level.ERROR, "Missing property or bill");
            throw new EntityNotFoundException("Either the property or the booking could not be found");
        }
    }

    @Override
    public void removeBookingFromProperty(Long propertyId, Long bookingId) {
        Optional<Property> optionalProperty = getPropertyById(propertyId);
        Optional<Booking> optionalBooking = bookingService.getBookingById(bookingId);
        if (optionalProperty.isPresent() && optionalBooking.isPresent()) {
            optionalBooking.get().setProperty(null);
            bookingService.addBooking(optionalBooking.get());
        } else {
            LOGGER.log(Level.ERROR, "Missing property or bill");
            throw new EntityNotFoundException("Either the property or the bill could not be found");
        }
    }

    @Override
    public Set<Bill> getPropertyBills(Long propertyId) {
        Optional<Property> optionalProperty = getPropertyById(propertyId);
        if (optionalProperty.isPresent()) {
            Set<Bill> crutch = new HashSet<>();
            for (Bill bill : billService.getBillsByProperty(optionalProperty.get())) {
                crutch.add(bill);
            }
            return crutch;
        } else {
            LOGGER.log(Level.ERROR, "No property with the {} ID exists in the database.", propertyId);
            throw new PropertyNotFoundException("No property found with ID: " + propertyId);
        }
    }

    @Override
    public void addBillToProperty(Long propertyId, Long billId) { //F**KING CIRCULAR REFERENCE ERRORS!!!
        Optional<Property> optionalProperty = getPropertyById(propertyId);
        Optional<Bill> optionalBill = billService.getBillById(billId);
        if (optionalProperty.isPresent() && optionalBill.isPresent()) {
            Property property = optionalProperty.get();
            Bill bill = optionalBill.get();
            bill.setProperty(property);
            billService.addBill(bill);
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
            List<Bill> existing = billService.getBillsByProperty(optionalProperty.get());
            Set<Bill> updated = new HashSet<>();
            for (Bill bill : existing) {
                updated.add(bill);
            }
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

    @Override
    public List<Property> getPropertiesByManager(Long managerId) {
        return getAllProperties().stream().filter(property -> property.getManager().getId().equals(managerId)).toList();
    }
}
