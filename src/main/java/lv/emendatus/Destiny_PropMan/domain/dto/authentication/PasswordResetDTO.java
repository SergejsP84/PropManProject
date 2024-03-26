package lv.emendatus.Destiny_PropMan.domain.dto.authentication;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.UserType;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PasswordResetDTO {
    private String email;
    private UserType userType;
    private String newPassword;
    private String reEnterNewPassword;
}
