package lv.emendatus.Destiny_PropMan.domain.dto.Admin;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdminTenantStatusDTO {
    private Long id;
    private boolean isActive;
}
