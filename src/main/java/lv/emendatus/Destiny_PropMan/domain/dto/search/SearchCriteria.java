package lv.emendatus.Destiny_PropMan.domain.dto.search;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.PropertyType;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SearchCriteria {
    private String location;
    @NotNull
    private LocalDate startDate;
    @NotNull
    private LocalDate endDate;
    private Double minPrice;
    private Double maxPrice;
    private Float minSizeM2;
    private Float maxSizeM2;
    private Float rating;
    private List<Long> amenityIds;
    @Enumerated(EnumType.STRING)
    private PropertyType type;
}