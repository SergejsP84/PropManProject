package lv.emendatus.Destiny_PropMan.service.implementation;

import lv.emendatus.Destiny_PropMan.domain.dto.search.PropertySearchResultDTO;
import lv.emendatus.Destiny_PropMan.domain.dto.search.SearchCriteria;
import lv.emendatus.Destiny_PropMan.domain.entity.Property;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.PropertyStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class PropertySearchService implements lv.emendatus.Destiny_PropMan.service.interfaces.PropertySearchService {

    private final JpaPropertyService propertyService;
    private final JpaCurrencyService currencyService;

    public PropertySearchService(JpaPropertyService propertyService, JpaCurrencyService currencyService) {
        this.propertyService = propertyService;
        this.currencyService = currencyService;
    }

    @Override
    public List<PropertySearchResultDTO> searchProperties(SearchCriteria criteria) {
        LocalDate startDate = criteria.getStartDate();
        LocalDate endDate = criteria.getEndDate();
        List<Property> suitableProperties = new java.util.ArrayList<>(propertyService.getAvailableProperties(startDate, endDate)
                .stream().filter(property -> !property.getStatus().equals(PropertyStatus.BLOCKED)).toList());
        if (criteria.getLocation() != null && !criteria.getLocation().isEmpty()) {
            List<Property> locatedProperties = propertyService.getPropertiesByLocation(criteria.getLocation());
            suitableProperties.removeIf(property -> !locatedProperties.contains(property));
        }
        if (criteria.getAmenityIds() != null && !criteria.getAmenityIds().isEmpty()) {
            Set<Property> propertiesWithAmenities = propertyService.getPropertiesWithAmenities(criteria.getAmenityIds());
            suitableProperties.removeIf(property -> !propertiesWithAmenities.contains(property));
        }
        if (criteria.getType() != null) {
            List<Property> typeProperties = propertyService.getPropertiesByType(criteria.getType());
            suitableProperties.removeIf(property -> !typeProperties.contains(property));
        }
        if (criteria.getRating() != null) {
            suitableProperties.removeIf(property -> property.getRating() < criteria.getRating());
        }

        // Converting price ranges to base currency
        if (!criteria.getCurrency().equals(currencyService.returnBaseCurrency())) {
            if (criteria.getMaxPrice() != null) criteria.setMaxPrice(criteria.getMaxPrice() * criteria.getCurrency().getRateToBase());
            if (criteria.getMinPrice() != null) criteria.setMinPrice(criteria.getMinPrice() * criteria.getCurrency().getRateToBase());
        }

        if (criteria.getMaxPrice() != null || criteria.getMinPrice() != null) {
            Double minPrice = Double.MIN_VALUE;
            Double maxPrice = Double.MAX_VALUE;
            if (criteria.getMinPrice() == null) {
                maxPrice = criteria.getMaxPrice();
            }
            if (criteria.getMaxPrice() == null) {
                minPrice = criteria.getMinPrice();
                for (Property property : suitableProperties) {
                    if (property.getPricePerDay() > maxPrice) maxPrice = property.getPricePerDay();
                }
            }
            if (criteria.getMaxPrice() != null && criteria.getMinPrice() != null) {
                minPrice = criteria.getMinPrice();
                maxPrice = criteria.getMaxPrice();
            }

            List<Property> fittingProperties = propertyService.getPropertiesByDailyPriceRange(minPrice, maxPrice);

            suitableProperties.removeIf(property -> !fittingProperties.contains(property));
        }

        if (criteria.getMinSizeM2() != null || criteria.getMaxSizeM2() != null) {
            Float minSize = Float.MIN_VALUE;
            Float maxSize = Float.MAX_VALUE;
            if (criteria.getMinSizeM2() == null) {
                maxSize = criteria.getMaxSizeM2();
            }
            if (criteria.getMaxSizeM2() == null) {
                minSize = criteria.getMinSizeM2();
                for (Property property : suitableProperties) {
                    if (property.getSizeM2() > maxSize) maxSize = property.getSizeM2();
                }
            }
            if (criteria.getMinSizeM2() != null && criteria.getMaxSizeM2() != null) {
                minSize = criteria.getMinSizeM2();
                maxSize = criteria.getMaxSizeM2();
            }
            for (Property property : suitableProperties) {
                if (property.getSizeM2() < minSize || property.getSizeM2() > maxSize) suitableProperties.remove(property);
            }
        }
        List<PropertySearchResultDTO> resultDTOList = suitableProperties.stream()
                .map(property -> {
                    PropertySearchResultDTO resultDTO = new PropertySearchResultDTO();
                    resultDTO.setId(property.getId());
                    resultDTO.setType(property.getType());
                    resultDTO.setAddress(property.getAddress());
                    resultDTO.setCountry(property.getCountry());
                    resultDTO.setSettlement(property.getSettlement());
                    resultDTO.setSizeM2(property.getSizeM2());
                    resultDTO.setDescription(property.getDescription());
                    resultDTO.setRating(property.getRating());
                    resultDTO.setPricePerDay(property.getPricePerDay());
                    resultDTO.setPricePerWeek(property.getPricePerWeek());
                    resultDTO.setPricePerMonth(property.getPricePerMonth());
                    return resultDTO;
                })
                .toList();

        return resultDTOList;
    }
}
