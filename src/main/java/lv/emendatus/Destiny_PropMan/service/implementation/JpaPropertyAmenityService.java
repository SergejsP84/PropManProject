package lv.emendatus.Destiny_PropMan.service.implementation;

import lv.emendatus.Destiny_PropMan.domain.entity.Property;
import lv.emendatus.Destiny_PropMan.domain.entity.PropertyAmenity;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.PropertyStatus;
import lv.emendatus.Destiny_PropMan.repository.interfaces.PropertyAmenityRepository;
import lv.emendatus.Destiny_PropMan.service.interfaces.PropertyAmenityService;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class JpaPropertyAmenityService implements PropertyAmenityService {
    private final PropertyAmenityRepository repository;
    public JpaPropertyAmenityService(PropertyAmenityRepository repository) {
        this.repository = repository;
    }
    @Override
    public List<PropertyAmenity> getAllPropertyAmenities() {
        return repository.findAll();
    }
    @Override
    public Optional<PropertyAmenity> getPropertyAmenityById(Long id) {
        return repository.findById(id);
    }
    @Override
    public void addPropertyAmenity(PropertyAmenity propertyAmenity) {
        repository.save(propertyAmenity);
    }
    @Override
    public void deletePropertyAmenity(Long id) {
        repository.deleteById(id);
    }
    @Override
    public List<PropertyAmenity> getPropertyAmenitiesByAmenity(Long amenityId) {
        List<PropertyAmenity> propertiesWithAmenity = getAllPropertyAmenities();
        return propertiesWithAmenity.stream()
                .filter(propertyAmenity -> propertyAmenity.getAmenity_id().equals(amenityId))
                .collect(Collectors.toList());
    }
    @Override
    public Set<PropertyAmenity> getPropertyAmenitiesByProperty(Long propertyId) {
        List<PropertyAmenity> allRelations = getAllPropertyAmenities();
        Set<PropertyAmenity> amenitiesOfProperty = new HashSet<>(allRelations);
        return amenitiesOfProperty.stream()
                .filter(propertyAmenity -> propertyAmenity.getProperty_id().equals(propertyId))
                .collect(Collectors.toSet());
    }
}
