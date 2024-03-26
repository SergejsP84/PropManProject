package lv.emendatus.Destiny_PropMan.service.implementation;

import lv.emendatus.Destiny_PropMan.domain.entity.PropertyDiscount;
import lv.emendatus.Destiny_PropMan.repository.interfaces.PropertyDiscountRepository;
import lv.emendatus.Destiny_PropMan.service.interfaces.PropertyDiscountService;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class JpaPropertyDiscountService implements PropertyDiscountService{
    private final PropertyDiscountRepository repository;
    public JpaPropertyDiscountService(PropertyDiscountRepository repository) {
        this.repository = repository;
    }
    @Override
    public List<PropertyDiscount> getAllPropertyDiscounts() {
        return repository.findAll();
    }
    @Override
    public Optional<PropertyDiscount> getPropertyDiscountById(Long id) {
        return repository.findById(id);
    }
    @Override
    public void addPropertyDiscount(PropertyDiscount propertyDiscount) {
        repository.save(propertyDiscount);
    }
    @Override
    public void deletePropertyDiscount(Long id) {
        repository.deleteById(id);
    }
    @Override
    public List<PropertyDiscount> getDiscountsForProperty(Long propertyId) {
        return getAllPropertyDiscounts().stream().filter(propertyDiscount -> propertyDiscount.getProperty().getId().equals(propertyId)).toList();
    }
    @Override
    public List<PropertyDiscount> getDiscountsForPropertyWithinPeriod(Long propertyId, LocalDate periodStart, LocalDate periodEnd) {
        List<PropertyDiscount> propertyDiscounts = getDiscountsForProperty(propertyId);
        List<PropertyDiscount> result = new ArrayList<>();
        for (PropertyDiscount discount : propertyDiscounts) {
            if (isDateRangeOverlap(periodStart, periodEnd, discount.getPeriodStart(), discount.getPeriodEnd())) result.add(discount);
        }
        return result;
    }

    private boolean isDateRangeOverlap(LocalDate start1, LocalDate end1, LocalDate start2, LocalDate end2) {
        return !(end1.isBefore(start2) || start1.isAfter(end2));
    }

    @Override
    public int getDiscountOrSurchargeForCalculations(Long propertyId, LocalDate periodStart, LocalDate periodEnd, LocalDate specificDay) {
        List<PropertyDiscount> applicableDiscounts = getDiscountsForPropertyWithinPeriod(propertyId, periodStart, periodEnd);
        if (applicableDiscounts.isEmpty()) return 0;
        List<PropertyDiscount> discountsForSpecificDay = applicableDiscounts.stream()
                .filter(discount -> isDateWithinPeriod(discount.getPeriodStart(), discount.getPeriodEnd(), specificDay))
                .toList();
        if (discountsForSpecificDay.isEmpty()) return 0;
        PropertyDiscount latestDiscount = discountsForSpecificDay.stream()
                .max(Comparator.comparing(PropertyDiscount::getCreatedAt))
                .orElseThrow();
        // TODO: Handle NoSuchElementException
        return latestDiscount.getPercentage().intValue();
    }

    private boolean isDateWithinPeriod (LocalDate periodStart, LocalDate periodEnd, LocalDate specificDay) {
        if ( (specificDay.isAfter(periodStart) || specificDay.isEqual(periodStart) ) && ( specificDay.isBefore(periodEnd) || specificDay.isEqual(periodEnd)) ) return true;
        return false;
    }

}

// SPARE VERSION FOR getDiscountOrSurchargeForCalculations:
/*
    @Override
    public int getDiscountOrSurchargeForCalculations(Long propertyId, LocalDate periodStart, LocalDate periodEnd, LocalDate specificDay) {
        List<PropertyDiscount> applicableDiscounts = getDiscountsForPropertyWithinPeriod(propertyId, periodStart, periodEnd);
        if (applicableDiscounts.isEmpty()) return 0;
        if (applicableDiscounts.size() == 1) return applicableDiscounts.get(0).getPercentage().intValue();
        List<PropertyDiscount> sievedList = new ArrayList<>();
        for (PropertyDiscount discount : applicableDiscounts) {
            if (isDateWithinPeriod(discount.getPeriodStart(), discount.getPeriodEnd(), specificDay)) sievedList.add(discount);
        }
        if (sievedList.isEmpty()) return 0;
        if (sievedList.size() == 1) return sievedList.get(0).getPercentage().intValue();
        int finalValue = 0;
        Timestamp latestTimestamp = Timestamp.valueOf("2000-01-01 00:00:01.0");
        for (PropertyDiscount discount : sievedList) {
            if (discount.getCreatedAt().toLocalDateTime().isAfter(latestTimestamp.toLocalDateTime())) {
                latestTimestamp = discount.getCreatedAt();
                finalValue = discount.getPercentage().intValue();
            }
        }
        return finalValue;
    }
 */