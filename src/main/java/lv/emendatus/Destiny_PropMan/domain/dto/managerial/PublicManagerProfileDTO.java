package lv.emendatus.Destiny_PropMan.domain.dto.managerial;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PublicManagerProfileDTO {
    private String managerName;
    private String description;
    private String phone;
    private String email;
}
