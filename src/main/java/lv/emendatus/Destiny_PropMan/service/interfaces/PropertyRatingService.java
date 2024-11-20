package lv.emendatus.Destiny_PropMan.service.interfaces;

import lv.emendatus.Destiny_PropMan.domain.entity.PropertyRating;
import java.util.List;
import java.util.Optional;

public interface PropertyRatingService {
    List<PropertyRating> getAllPropertyRatings();
    Optional<PropertyRating> getPropertyRatingById(Long id);
    void addPropertyRating(PropertyRating propertyRating);
    void deletePropertyRating(Long id);
    List<PropertyRating> getRatingsForProperty(Long propertyId);
    boolean bookingAlreadyRated(Long bookingId);
}
