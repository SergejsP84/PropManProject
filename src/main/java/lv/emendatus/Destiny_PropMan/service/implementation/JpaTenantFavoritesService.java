package lv.emendatus.Destiny_PropMan.service.implementation;

import lv.emendatus.Destiny_PropMan.domain.entity.TenantFavorites;
import lv.emendatus.Destiny_PropMan.repository.interfaces.TenantFavoritesRepository;
import lv.emendatus.Destiny_PropMan.service.interfaces.TenantFavoritesService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class JpaTenantFavoritesService implements TenantFavoritesService {
    private final TenantFavoritesRepository repository;
    private final Logger LOGGER = LogManager.getLogger(JpaPropertyService.class);

    public JpaTenantFavoritesService(TenantFavoritesRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<TenantFavorites> getAllTenantFavorites() {
        return repository.findAll();
    }

    @Override
    public Optional<TenantFavorites> getTenantFavoritesById(Long id) {
        return repository.findById(id);
    }

    @Override
    public void addTenantFavorites(TenantFavorites tenantFavorites) {
        repository.save(tenantFavorites);
    }

    @Override
    public void deleteTenantFavorites(Long id) {
        repository.deleteById(id);
    }

    @Override
    public Optional<TenantFavorites> getTenantFavoritesByTenant(Long tenantId) {
        return repository.findByTenantId(tenantId);
    }

    @Override
    public void removePropertyFromFavorites(Long tenantId, Long propertyId) {
        getTenantFavoritesByTenant(tenantId).ifPresent(favorites -> {
            List<Long> updatedFavorites = new ArrayList<>(favorites.getFavoritePropertyIDs());
            updatedFavorites.remove(propertyId);
            favorites.setFavoritePropertyIDs(updatedFavorites);
            repository.save(favorites);
        });
    }

    @Override
    public void addPropertyToFavorites(Long tenantId, Long propertyId) {
        Optional<TenantFavorites> optionalFavorites = getTenantFavoritesByTenant(tenantId);
        if (optionalFavorites.isPresent()) {
            TenantFavorites currentFavorites = optionalFavorites.get();
            List<Long> favoritePropertyIDs = currentFavorites.getFavoritePropertyIDs();
            if (!favoritePropertyIDs.contains(propertyId)) {
                favoritePropertyIDs.add(propertyId);
                currentFavorites.setFavoritePropertyIDs(favoritePropertyIDs);
                repository.save(currentFavorites);
            } else {
                LOGGER.info("Property is already on the favorites list");
            }
        } else {
            LOGGER.info("No favorites found for the given tenant");
        }
    }
}

