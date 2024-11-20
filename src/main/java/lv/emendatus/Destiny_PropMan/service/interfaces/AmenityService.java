package lv.emendatus.Destiny_PropMan.service.interfaces;

import lv.emendatus.Destiny_PropMan.domain.entity.Amenity;
import java.util.List;
import java.util.Optional;

public interface AmenityService {
    List<Amenity> getAllAmenities();
    Optional<Amenity> getAmenityById(Long id);
    void addAmenity(Amenity amenity);
    void deleteAmenity(Long id);
}
