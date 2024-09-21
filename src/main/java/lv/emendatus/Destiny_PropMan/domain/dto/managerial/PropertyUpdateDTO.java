package lv.emendatus.Destiny_PropMan.domain.dto.managerial;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.PropertyStatus;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.PropertyType;
;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PropertyUpdateDTO {

    private PropertyStatus status;
    private PropertyType type;
    private String address;
    private String country;
    private String settlement;
    private Float sizeM2;
    private String description;
    private Double pricePerDay;
    private Double pricePerWeek;
    private Double pricePerMonth;

}
