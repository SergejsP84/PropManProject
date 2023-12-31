package lv.emendatus.Destiny_PropMan.serviceTests;

import lv.emendatus.Destiny_PropMan.domain.entity.Amenity;
import lv.emendatus.Destiny_PropMan.domain.entity.Tenant;
import lv.emendatus.Destiny_PropMan.repository.interfaces.AmenityRepository;
import lv.emendatus.Destiny_PropMan.service.implementation.JpaAmenityService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AmenityServiceTest {
    @InjectMocks
    private JpaAmenityService jpaAmenityService;
    @Mock
    private AmenityRepository amenityRepository;

    @Test
    void getAllAmenities() {
        List<Amenity> amenities = Arrays.asList(new Amenity(), new Amenity());
        when(amenityRepository.findAll()).thenReturn(amenities);
        List<Amenity> result = jpaAmenityService.getAllAmenities();
        assertEquals(amenities, result);
    }
    @Test
    void getAmenityById() {
        Amenity fifthAmenity = new Amenity();
        fifthAmenity.setId(5L);
        when(amenityRepository.findById(5L)).thenReturn(Optional.of(fifthAmenity));
        Optional<Amenity> obtainedAmenity = jpaAmenityService.getAmenityById(5L);
        assertEquals(Optional.of(fifthAmenity), obtainedAmenity);
    }
    @Test
    void addAmenity() {
        Amenity newAmenity = new Amenity();
        newAmenity.setId(6L);
        newAmenity.setDescription("Balcony");
        when(amenityRepository.save(newAmenity)).thenReturn(newAmenity);
        jpaAmenityService.addAmenity(newAmenity);
        verify(amenityRepository).save(newAmenity);
    }
    @Test
    void deleteAmenity() {
        Amenity newAmenity = new Amenity();
        newAmenity.setId(7L);
        newAmenity.setDescription("Rooftop pool");
        jpaAmenityService.deleteAmenity(7L);
        verify(amenityRepository).deleteById(7L);
    }
}

