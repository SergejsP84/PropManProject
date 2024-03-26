package lv.emendatus.Destiny_PropMan.domain.dto.managerial;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PropertyDiscountDTO {
    private Long propertyId;
    private Long managerId;
    private Double percentage;
    private LocalDate periodStart;
    private LocalDate periodEnd;
}
