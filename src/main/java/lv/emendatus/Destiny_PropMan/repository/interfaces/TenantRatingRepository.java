package lv.emendatus.Destiny_PropMan.repository.interfaces;

import lv.emendatus.Destiny_PropMan.domain.entity.TenantRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TenantRatingRepository extends JpaRepository<TenantRating, Long> {
}
