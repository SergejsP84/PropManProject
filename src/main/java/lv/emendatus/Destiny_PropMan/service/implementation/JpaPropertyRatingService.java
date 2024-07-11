package lv.emendatus.Destiny_PropMan.service.implementation;

import lv.emendatus.Destiny_PropMan.domain.entity.PropertyRating;
import lv.emendatus.Destiny_PropMan.repository.interfaces.PropertyRatingRepository;
import lv.emendatus.Destiny_PropMan.service.interfaces.PropertyRatingService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class JpaPropertyRatingService implements PropertyRatingService {

    private final PropertyRatingRepository repository;

    public JpaPropertyRatingService(PropertyRatingRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<PropertyRating> getAllPropertyRatings() {
        return repository.findAll();
    }

    @Override
    public Optional<PropertyRating> getPropertyRatingById(Long id) {
        return repository.findById(id);
    }

    @Override
    public void addPropertyRating(PropertyRating propertyRating) {
        repository.save(propertyRating);
    }

    @Override
    public void deletePropertyRating(Long id) {
        repository.deleteById(id);
    }

    @Override
    public List<PropertyRating> getRatingsForProperty(Long propertyId) {
        return getAllPropertyRatings().stream()
                .filter(propertyRating -> propertyRating.getPropertyId().equals(propertyId)).toList();
    }

    @Override
    public boolean bookingAlreadyRated(Long bookingId) {
        for (PropertyRating rating : getAllPropertyRatings()) {
           if (rating.getBookingId().equals(bookingId)) return true;
        }
        return false;
    }
}
