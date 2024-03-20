package lv.emendatus.Destiny_PropMan.domain.dto.Admin;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdminTenantDTO {
    private Long tenantId;
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private String iban;
    private String paymentCardNo;
    private float rating;

}
