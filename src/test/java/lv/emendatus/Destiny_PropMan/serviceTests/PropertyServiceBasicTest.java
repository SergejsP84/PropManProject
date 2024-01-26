package lv.emendatus.Destiny_PropMan.serviceTests;


import lv.emendatus.Destiny_PropMan.domain.entity.Booking;
import lv.emendatus.Destiny_PropMan.domain.entity.Property;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.BookingStatus;
import lv.emendatus.Destiny_PropMan.repository.interfaces.PropertyRepository;
import lv.emendatus.Destiny_PropMan.service.implementation.JpaPropertyService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
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
class PropertyServiceBasicTest {
    @InjectMocks
    private JpaPropertyService jpaPropertyService;
    @Mock
    private PropertyRepository propertyRepository;

    @Test
    void getAllProperties() {
        List<Property> properties = Arrays.asList(new Property(), new Property());
        when(propertyRepository.findAll()).thenReturn(properties);
        List<Property> result = jpaPropertyService.getAllProperties();
        assertEquals(properties, result);
    }
    @Test
    void getPropertyById() {
        Property eleventhProperty = new Property();
        eleventhProperty.setId(11L);
        when(propertyRepository.findById(11L)).thenReturn(Optional.of(eleventhProperty));
        Optional<Property> obtainedProperty = jpaPropertyService.getPropertyById(11L);
        assertEquals(Optional.of(eleventhProperty), obtainedProperty);
    }
    @Test
    void addProperty() {
        Property newProperty = new Property();
        newProperty.setId(12L);
        when(propertyRepository.save(newProperty)).thenReturn(newProperty);
        jpaPropertyService.addProperty(newProperty);
        verify(propertyRepository).save(newProperty);
    }
    @Test
    void deleteProperty() {
        Property newProperty = new Property();
        newProperty.setId(13L);
        jpaPropertyService.deleteProperty(13L);
        verify(propertyRepository).deleteById(13L);
    }

}

