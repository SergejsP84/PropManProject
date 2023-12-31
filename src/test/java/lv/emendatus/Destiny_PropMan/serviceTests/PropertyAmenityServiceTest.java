package lv.emendatus.Destiny_PropMan.serviceTests;


import lv.emendatus.Destiny_PropMan.domain.entity.Amenity;
import lv.emendatus.Destiny_PropMan.domain.entity.Property;
import lv.emendatus.Destiny_PropMan.domain.entity.PropertyAmenity;
import lv.emendatus.Destiny_PropMan.domain.entity.Tenant;
import lv.emendatus.Destiny_PropMan.repository.interfaces.PropertyAmenityRepository;
import lv.emendatus.Destiny_PropMan.service.implementation.JpaPropertyAmenityService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PropertyAmenityServiceTest {
    @InjectMocks
    private JpaPropertyAmenityService jpaPropertyAmenityService;
    @Mock
    private PropertyAmenityRepository propertyAmenityRepository;

    @Test
    void getAllPropertyAmenities() {
        List<PropertyAmenity> propertyAmenities = Arrays.asList(new PropertyAmenity(), new PropertyAmenity());
        when(propertyAmenityRepository.findAll()).thenReturn(propertyAmenities);
        List<PropertyAmenity> result = jpaPropertyAmenityService.getAllPropertyAmenities();
        assertEquals(propertyAmenities, result);
    }
    @Test
    void getPropertyAmenityById() {
        PropertyAmenity firstPropertyAmenity = new PropertyAmenity();
        firstPropertyAmenity.setId(1L);
        when(propertyAmenityRepository.findById(1L)).thenReturn(Optional.of(firstPropertyAmenity));
        Optional<PropertyAmenity> obtainedPropertyAmenity = jpaPropertyAmenityService.getPropertyAmenityById(1L);
        assertEquals(Optional.of(firstPropertyAmenity), obtainedPropertyAmenity);
    }
    @Test
    void addPropertyAmenity() {
        PropertyAmenity newPropertyAmenity = new PropertyAmenity();
        newPropertyAmenity.setId(2L);
        newPropertyAmenity.setProperty_id(3L);
        newPropertyAmenity.setAmenity_id(5L);
        when(propertyAmenityRepository.save(newPropertyAmenity)).thenReturn(newPropertyAmenity);
        jpaPropertyAmenityService.addPropertyAmenity(newPropertyAmenity);
        verify(propertyAmenityRepository).save(newPropertyAmenity);
    }
    @Test
    void deletePropertyAmenity() {
        PropertyAmenity newPropertyAmenity = new PropertyAmenity();
        newPropertyAmenity.setId(3L);
        newPropertyAmenity.setProperty_id(4L);
        newPropertyAmenity.setAmenity_id(6L);
        jpaPropertyAmenityService.deletePropertyAmenity(3L);
        verify(propertyAmenityRepository).deleteById(3L);
    }
    @Test
    void getPropertyAmenitiesByAmenity() {
        PropertyAmenity newPropertyAmenity1 = new PropertyAmenity();
        PropertyAmenity newPropertyAmenity2 = new PropertyAmenity();
        PropertyAmenity newPropertyAmenity3 = new PropertyAmenity();
        PropertyAmenity newPropertyAmenity4 = new PropertyAmenity();
        Amenity amenity = new Amenity();
        amenity.setId(10L);
        newPropertyAmenity1.setId(1L);
        newPropertyAmenity2.setId(2L);
        newPropertyAmenity3.setId(3L);
        newPropertyAmenity4.setId(4L);
        newPropertyAmenity1.setProperty_id(1L);
        newPropertyAmenity2.setProperty_id(2L);
        newPropertyAmenity3.setProperty_id(1L);
        newPropertyAmenity4.setProperty_id(2L);
        newPropertyAmenity1.setAmenity_id(7L);
        newPropertyAmenity2.setAmenity_id(10L);
        newPropertyAmenity3.setAmenity_id(11L);
        newPropertyAmenity4.setAmenity_id(10L);
        when(propertyAmenityRepository.findAll()).thenReturn(Arrays.asList(newPropertyAmenity1, newPropertyAmenity2, newPropertyAmenity3, newPropertyAmenity4));
        List<PropertyAmenity> result = jpaPropertyAmenityService.getPropertyAmenitiesByAmenity(amenity.getId());
        assertEquals(2, result.size());
        assertTrue(result.contains(newPropertyAmenity2));
        assertTrue(result.contains(newPropertyAmenity4));
    }
    @Test
    void getPropertyAmenitiesByProperty() {
        PropertyAmenity newPropertyAmenity1 = new PropertyAmenity();
        PropertyAmenity newPropertyAmenity2 = new PropertyAmenity();
        PropertyAmenity newPropertyAmenity3 = new PropertyAmenity();
        PropertyAmenity newPropertyAmenity4 = new PropertyAmenity();
        Property property = new Property();
        property.setId(1L);
        newPropertyAmenity1.setId(1L);
        newPropertyAmenity2.setId(2L);
        newPropertyAmenity3.setId(3L);
        newPropertyAmenity4.setId(4L);
        newPropertyAmenity1.setProperty_id(1L);
        newPropertyAmenity2.setProperty_id(2L);
        newPropertyAmenity3.setProperty_id(1L);
        newPropertyAmenity4.setProperty_id(2L);
        newPropertyAmenity1.setAmenity_id(7L);
        newPropertyAmenity2.setAmenity_id(10L);
        newPropertyAmenity3.setAmenity_id(11L);
        newPropertyAmenity4.setAmenity_id(10L);
        when(propertyAmenityRepository.findAll()).thenReturn(Arrays.asList(newPropertyAmenity1, newPropertyAmenity2, newPropertyAmenity3, newPropertyAmenity4));
        Set<PropertyAmenity> result = jpaPropertyAmenityService.getPropertyAmenitiesByProperty(property.getId());
        assertEquals(2, result.size());
        assertTrue(result.contains(newPropertyAmenity1));
        assertTrue(result.contains(newPropertyAmenity3));
    }
}

