package lv.emendatus.Destiny_PropMan.service.interfaces;

import lv.emendatus.Destiny_PropMan.domain.entity.PropertyRating;
import lv.emendatus.Destiny_PropMan.domain.entity.TenantRating;

import java.util.List;
import java.util.Optional;

public interface TenantRatingService {
    List<TenantRating> getAllTenantRatings();
    Optional<TenantRating> getTenantRatingById(Long id);
    void addTenantRating(TenantRating tenantRating);
    void deleteTenantRating(Long id);
    List<TenantRating> getRatingsForTenant(Long tenantId);
    boolean bookingAlreadyRated(Long bookingId);
}
