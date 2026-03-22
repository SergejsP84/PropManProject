package lv.emendatus.Destiny_PropMan.repository.interfaces;

import lv.emendatus.Destiny_PropMan.domain.entity.Property;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.PropertyType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {

    List<Property> findByAddressContainingIgnoreCaseOrCountryContainingIgnoreCaseOrSettlementContainingIgnoreCase(
            String address, String country, String settlement);

    List<Property> findByType(PropertyType type);

    List<Property> findByPricePerDayBetween(Double minPrice, Double maxPrice);

    List<Property> findByPricePerWeekBetween(Double minPrice, Double maxPrice);

    List<Property> findByPricePerMonthBetween(Double minPrice, Double maxPrice);

    List<Property> findByManager_Id(Long managerId);
}
