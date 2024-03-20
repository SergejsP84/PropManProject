package lv.emendatus.Destiny_PropMan.service.interfaces;

import lv.emendatus.Destiny_PropMan.domain.entity.PropertyAmenity;
import lv.emendatus.Destiny_PropMan.domain.entity.TenantFavorites;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface TenantFavoritesService {
    List<TenantFavorites> getAllTenantFavorites();
    Optional<TenantFavorites> getTenantFavoritesById(Long id);
    void addTenantFavorites(TenantFavorites tenantFavorites);
    void deleteTenantFavorites(Long id);
    Optional<TenantFavorites> getTenantFavoritesByTenant(Long tenantId);
    void removePropertyFromFavorites(Long tenantId, Long propertyId);
    void addPropertyToFavorites(Long tenantId, Long propertyId);
}
