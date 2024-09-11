package lv.emendatus.Destiny_PropMan.service.interfaces;

import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.UserType;

import java.security.Principal;

public interface PasswordService {
    public void changePassword(String login, UserType userType, String newPassword, String reEnterNewPassword, Principal principal);
    public void resetPassword(String email, UserType userType, String newPassword, String reEnterNewPassword);
    public void completePasswordReset(String token);
}
