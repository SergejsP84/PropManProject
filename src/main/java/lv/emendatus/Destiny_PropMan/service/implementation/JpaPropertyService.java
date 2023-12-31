package lv.emendatus.Destiny_PropMan.service.implementation;

import lv.emendatus.Destiny_PropMan.domain.entity.*;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.PropertyStatus;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.PropertyType;
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

    private final Logger LOGGER = LogManager.getLogger(JpaPropertyService.class);
    public JpaPropertyService(PropertyRepository propertyRepository, AmenityRepository amenityRepository, PropertyAmenityRepository propertyAmenityRepository, BookingRepository bookingRepository, BillRepository billRepository) {
        this.propertyRepository = propertyRepository;
        this.amenityRepository = amenityRepository;
        this.propertyAmenityRepository = propertyAmenityRepository;
        this.bookingRepository = bookingRepository;
        this.billRepository = billRepository;
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
        return properties.stream()
                .filter(property -> (property.getStatus().equals(PropertyStatus.AVAILABLE)))
                .collect(Collectors.toList());
    }

    @Override
    public Set<Property> getPropertiesWithAmenities(List<Long> amenityIds) {
        List<PropertyAmenity> allAmenityRecords = propertyAmenityRepository.findAll();
        return allAmenityRecords.stream()
                .filter(propertyAmenity -> amenityIds.contains(propertyAmenity.getAmenity_id()))
                .collect(Collectors.groupingBy(PropertyAmenity::getProperty_id,
                        Collectors.mapping(PropertyAmenity::getAmenity_id, Collectors.toList())))
                .entrySet().stream()
                .filter(entry -> amenityIds.containsAll(entry.getValue()))
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
            // TODO: Handle the case where the property with the given ID is not found
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
            // TODO: Handle the case where the property with the given ID is not found
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
            // TODO: Handle the case where the property with the given ID is not found
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
            // TODO: Handle the case where the property with the given ID is not found
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
            // TODO: Handle the case where the property with the given ID is not found
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
            // TODO: Handle the case where the property with the given ID is not found
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
            // TODO: Handle the case where the property with the given ID is not found
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
            // TODO: Handle the case where the property with the given ID is not found
        }
    }

    @Override
    public void updateManager(Long propertyId, Manager manager) {
        Optional<Property> optionalProperty = getPropertyById(propertyId);
        if (optionalProperty.isPresent()) {
            Property property = optionalProperty.get();
            property.setManager(manager);
            addProperty(property);
        } else {
            LOGGER.log(Level.ERROR, "No property with the {} ID exists in the database.", propertyId);
            // TODO: Handle the case where the property with the given ID is not found
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
            // TODO: Handle the case where the property with the given ID is not found
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
            // TODO: Handle the case where the property with the given ID is not found
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
            // TODO: Handle the case where the property with the given ID is not found
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
            // TODO: Handle the case where the property with the given ID is not found
        }
    }

    @Override
    public void assignTenantToProperty(Long propertyId, Tenant tenant) {
        Optional<Property> optionalProperty = getPropertyById(propertyId);
        if (optionalProperty.isPresent()) {
            Property property = optionalProperty.get();
            property.setTenant(tenant);
            addProperty(property);
        } else {
            LOGGER.log(Level.ERROR, "No property with the {} ID exists in the database.", propertyId);
            // TODO: Handle the case where the property with the given ID is not found
        }
    }

    @Override
    public void removeTenantFromProperty(Long propertyId) {
        Optional<Property> optionalProperty = getPropertyById(propertyId);
        if (optionalProperty.isPresent()) {
            Property property = optionalProperty.get();
            property.setTenant(null);
            addProperty(property);
        } else {
            LOGGER.log(Level.ERROR, "No property with the {} ID exists in the database.", propertyId);
            // TODO: Handle the case where the property with the given ID is not found
        }
    }

    @Override
    public Set<Booking> getPropertyBookings(Long propertyId) {
        Optional<Property> optionalProperty = getPropertyById(propertyId);
        if (optionalProperty.isPresent()) {
            return optionalProperty.get().getBookings();
        } else {
            LOGGER.log(Level.ERROR, "No property with the {} ID exists in the database.", propertyId);
            // TODO: Handle the case where the property with the given ID is not found
            return Collections.emptySet();
        }
    }

    @Override
    public void addBookingToProperty(Long propertyId, Booking booking) {
        Optional<Property> optionalProperty = getPropertyById(propertyId);
        if (optionalProperty.isPresent()) {
            Set<Booking> existingBookings = getPropertyBookings(propertyId);
            existingBookings.add(booking);
            optionalProperty.get().setBookings(existingBookings);
            propertyRepository.save(optionalProperty.get());
        } else {
            LOGGER.log(Level.ERROR, "No property with the {} ID exists in the database.", propertyId);
            // TODO: Handle the case where the property with the given ID is not found
        }
    }

    @Override
    public void removeBookingFromProperty(Long propertyId, Long bookingId) {
        Optional<Property> optionalProperty = getPropertyById(propertyId);
        Optional<Booking> optionalBooking = bookingRepository.findById(bookingId);
        if (optionalProperty.isPresent()) {
            if (optionalBooking.isPresent()) {
                Set<Booking> existingBookings = getPropertyBookings(propertyId);
                existingBookings.removeIf(booking -> booking.getId().equals(bookingId));
                propertyRepository.save(optionalProperty.get());
            } else {
                LOGGER.log(Level.ERROR, "No booking with the {} ID exists in the database.", bookingId);
                // TODO: Handle the case where the booking with the given ID is not found
            }
        } else {
            LOGGER.log(Level.ERROR, "No property with the {} ID exists in the database.", propertyId);
            // TODO: Handle the case where the property with the given ID is not found
        }
    }

    @Override
    public Set<Bill> getPropertyBills(Long propertyId) {
        Optional<Property> optionalProperty = getPropertyById(propertyId);
        if (optionalProperty.isPresent()) {
            return optionalProperty.get().getBills();
        } else {
            LOGGER.log(Level.ERROR, "No property with the {} ID exists in the database.", propertyId);
            // TODO: Handle the case where the property with the given ID is not found
            return Collections.emptySet();
        }
    }

    @Override
    public void addBillToProperty(Long propertyId, Bill bill) {
        Optional<Property> optionalProperty = getPropertyById(propertyId);
        if (optionalProperty.isPresent()) {
            Set<Bill> existingBills = getPropertyBills(propertyId);
            existingBills.add(bill);
            optionalProperty.get().setBills(existingBills);
            propertyRepository.save(optionalProperty.get());
        } else {
            LOGGER.log(Level.ERROR, "No property with the {} ID exists in the database.", propertyId);
            // TODO: Handle the case where the property with the given ID is not found
        }
    }

    @Override
    public void removeBillFromProperty(Long propertyId, Long billId) {
        Optional<Property> optionalProperty = getPropertyById(propertyId);
        Optional<Bill> optionalBill = billRepository.findById(billId);
        if (optionalProperty.isPresent()) {
            if (optionalBill.isPresent()) {
                Set<Bill> existingBills = getPropertyBills(propertyId);
                existingBills.removeIf(bill -> bill.getId().equals(billId));
                propertyRepository.save(optionalProperty.get());
            } else {
                LOGGER.log(Level.ERROR, "No bill with the {} ID exists in the database.", billId);
                // TODO: Handle the case where the bill with the given ID is not found
            }
        } else {
            LOGGER.log(Level.ERROR, "No property with the {} ID exists in the database.", propertyId);
            // TODO: Handle the case where the property with the given ID is not found
        }
    }
}
