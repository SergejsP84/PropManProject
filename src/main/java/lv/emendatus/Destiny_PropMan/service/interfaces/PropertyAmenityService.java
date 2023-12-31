package lv.emendatus.Destiny_PropMan.service.interfaces;

import lv.emendatus.Destiny_PropMan.domain.entity.PropertyAmenity;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface PropertyAmenityService {
    List<PropertyAmenity> getAllPropertyAmenities();
    Optional<PropertyAmenity> getPropertyAmenityById(Long id);
    void addPropertyAmenity(PropertyAmenity propertyAmenity);
    void deletePropertyAmenity(Long id);
    List<PropertyAmenity> getPropertyAmenitiesByAmenity(Long amenityId);
    Set<PropertyAmenity> getPropertyAmenitiesByProperty(Long propertyId);
}
