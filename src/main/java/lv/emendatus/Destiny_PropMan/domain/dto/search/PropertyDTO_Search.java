package lv.emendatus.Destiny_PropMan.domain.dto.search;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.PropertyStatus;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.PropertyType;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PropertyDTO_Search {
    private Long id;
    @Enumerated(EnumType.STRING)
    private PropertyStatus status;
    @Enumerated(EnumType.STRING)
    private PropertyType type;
    private String address;
    private String country;
    private String settlement;
    private Float sizeM2;
    private String description;
    private Float rating;
    private Double pricePerDay;
    private Double pricePerWeek;
    private Double pricePerMonth;
    private Set<BookingDTO_Search> bookings;

}
