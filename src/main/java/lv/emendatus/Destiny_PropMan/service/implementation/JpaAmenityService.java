package lv.emendatus.Destiny_PropMan.service.implementation;

import lv.emendatus.Destiny_PropMan.domain.entity.Amenity;
import lv.emendatus.Destiny_PropMan.repository.interfaces.AmenityRepository;
import lv.emendatus.Destiny_PropMan.service.interfaces.AmenityService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class JpaAmenityService implements AmenityService {
    private final AmenityRepository amenityRepository;
    public JpaAmenityService(AmenityRepository amenityRepository) {
        this.amenityRepository = amenityRepository;
    }
    @Override
    public List<Amenity> getAllAmenities() {
        return amenityRepository.findAll();
    }
    @Override
    public Optional<Amenity> getAmenityById(Long id) {
        return amenityRepository.findById(id);
    }
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Override
    public void addAmenity(Amenity amenity) {
        amenityRepository.save(amenity);
    }
    @Override
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void deleteAmenity(Long id) {
        amenityRepository.deleteById(id);
    }
}
