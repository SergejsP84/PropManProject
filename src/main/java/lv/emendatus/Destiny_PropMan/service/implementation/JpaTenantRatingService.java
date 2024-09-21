package lv.emendatus.Destiny_PropMan.service.implementation;

import lv.emendatus.Destiny_PropMan.domain.entity.TenantRating;
import lv.emendatus.Destiny_PropMan.repository.interfaces.TenantRatingRepository;
import lv.emendatus.Destiny_PropMan.service.interfaces.TenantRatingService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class JpaTenantRatingService implements TenantRatingService {

    private final TenantRatingRepository repository;

    public JpaTenantRatingService(TenantRatingRepository repository) {
        this.repository = repository;
    }


    @Override
    public List<TenantRating> getAllTenantRatings() {
        return repository.findAll();
    }

    @Override
    public Optional<TenantRating> getTenantRatingById(Long id) {
        return repository.findById(id);
    }

    @Override
    public void addTenantRating(TenantRating tenantRating) {
        repository.save(tenantRating);
    }

    @Override
    public void deleteTenantRating(Long id) {
        repository.deleteById(id);
    }

    @Override
    public List<TenantRating> getRatingsForTenant(Long tenantId) {
        return getAllTenantRatings().stream()
                .filter(tenantRating -> tenantRating.getTenantId().equals(tenantId)).toList();
    }

    @Override
    public boolean bookingAlreadyRated(Long bookingId) {
        for (TenantRating rating : getAllTenantRatings()) {
            if (rating.getBookingId().equals(bookingId)) return true;
        }
        return false;
    }
}
