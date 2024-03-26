package lv.emendatus.Destiny_PropMan.service.interfaces;

import lv.emendatus.Destiny_PropMan.domain.entity.PropertyAmenity;
import lv.emendatus.Destiny_PropMan.domain.entity.PropertyDiscount;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PropertyDiscountService {
    List<PropertyDiscount> getAllPropertyDiscounts();
    Optional<PropertyDiscount> getPropertyDiscountById(Long id);
    void addPropertyDiscount(PropertyDiscount propertyDiscount);
    void deletePropertyDiscount(Long id);
    List<PropertyDiscount> getDiscountsForProperty(Long propertyId);
    List<PropertyDiscount> getDiscountsForPropertyWithinPeriod(Long propertyId, LocalDate periodStart, LocalDate periodEnd);
    int getDiscountOrSurchargeForCalculations(Long propertyId, LocalDate periodStart, LocalDate periodEnd, LocalDate specificDay);

}
