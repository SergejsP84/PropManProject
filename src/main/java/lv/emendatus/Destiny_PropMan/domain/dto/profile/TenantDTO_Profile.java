package lv.emendatus.Destiny_PropMan.domain.dto.profile;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TenantDTO_Profile {
    private Long tenantId;
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private String iban;
    private String paymentCardNo;
    private float rating;
}
