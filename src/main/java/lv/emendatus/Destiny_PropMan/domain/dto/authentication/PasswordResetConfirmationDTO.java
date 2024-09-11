package lv.emendatus.Destiny_PropMan.domain.dto.authentication;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PasswordResetConfirmationDTO {
        private String token;
//        private String newPassword;
//        private String reEnterNewPassword;
}
