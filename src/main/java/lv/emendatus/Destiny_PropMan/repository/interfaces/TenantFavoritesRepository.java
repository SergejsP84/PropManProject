package lv.emendatus.Destiny_PropMan.repository.interfaces;

import lv.emendatus.Destiny_PropMan.domain.entity.TenantFavorites;
import lv.emendatus.Destiny_PropMan.domain.entity.TenantPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TenantFavoritesRepository extends JpaRepository<TenantFavorites, Long> {

    Optional<TenantFavorites> findByTenantId(Long tenantId);
}
