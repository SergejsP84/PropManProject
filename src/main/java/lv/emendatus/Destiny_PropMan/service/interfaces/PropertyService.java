package lv.emendatus.Destiny_PropMan.service.interfaces;

import lv.emendatus.Destiny_PropMan.domain.entity.*;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.PropertyStatus;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.PropertyType;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface PropertyService {

    // common methods
    List<Property> getAllProperties();
    Optional<Property> getPropertyById(Long id);
    void addProperty(Property property);
    void deleteProperty(Long id);

    //search-related methods
    List<Property> getPropertiesByLocation(String location);
    List<Property> getPropertiesByType(PropertyType type);
    List<Property> getPropertiesByDailyPriceRange(double minPrice, double maxPrice);
    List<Property> getPropertiesByWeeklyPriceRange(double minPrice, double maxPrice);
    List<Property> getPropertiesByMonthlyPriceRange(double minPrice, double maxPrice);
    List<Property> getAvailableProperties(LocalDate startDate, LocalDate endDate);
    Set<Property> getPropertiesWithAmenities(List<Long> amenityIds);

    // parameter alteration methods
    void updatePropertyAddress(Long id, String newAddress);
    void updatePropertySettlement(Long id, String newSettlement);
    void updatePropertyCountry(Long id, String newCountry);
    void updatePropertyPricePerDay(Long id, double newPrice);
    void updatePropertyPricePerWeek(Long id, double newPrice);
    void updatePropertyPricePerMonth(Long id, double newPrice);
    void addAmenityToProperty(Long propertyId, Long amenityId);
    void removeAmenityFromProperty(Long propertyId, Long amenityId);
    void updateManager(Long propertyId, Long managerId);
    void setStatus(Long propertyId, PropertyStatus status);
    void updateSize(Long propertyId, Float newSizeM2);
    void updateRating(Long propertyId, Float updatedRating);
    void updateDescription(Long id, String newDescription);

    // business logic management
    void assignTenantToProperty(Long propertyId, Long tenantId);
    Tenant getCurrentTenant(Long propertyId);
    void removeTenantFromProperty(Long propertyId);
    Set<Booking> getPropertyBookings(Long propertyId);
    void addBookingToProperty(Long propertyId, Long bookingId);
    void removeBookingFromProperty(Long propertyId, Long bookingId);
    Set<Bill> getPropertyBills(Long propertyId);
    void addBillToProperty(Long propertyId, Long billId);
    void removeBillFromProperty(Long propertyId, Long billId);
    List<Property> getPropertiesByManager(Long managerId);
}
