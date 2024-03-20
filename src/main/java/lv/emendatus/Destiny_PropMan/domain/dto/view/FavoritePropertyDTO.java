package lv.emendatus.Destiny_PropMan.domain.dto.view;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FavoritePropertyDTO {
    private Long tenantId; // ID of the tenant saving the property
    private Long propertyId; // ID of the property to be saved as a favorite
}
