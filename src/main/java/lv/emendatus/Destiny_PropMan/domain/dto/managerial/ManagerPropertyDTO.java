package lv.emendatus.Destiny_PropMan.domain.dto.managerial;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.PropertyStatus;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ManagerPropertyDTO {
    private Long propertyId;
    private String address;
    private String settlement;
    private String country;
    private PropertyStatus status;
    private Float sizeM2;
    private String description;
    private Double pricePerDay;
    private Double pricePerWeek;
    private Double pricePerMonth;
}
