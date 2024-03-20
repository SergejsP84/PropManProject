package lv.emendatus.Destiny_PropMan.domain.dto.Admin;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.PropertyStatus;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdminPropertyStatusDTO {
    private Long id;
    private PropertyStatus status;
}
