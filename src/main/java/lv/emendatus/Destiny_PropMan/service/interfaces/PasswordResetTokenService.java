package lv.emendatus.Destiny_PropMan.service.interfaces;

import lv.emendatus.Destiny_PropMan.domain.tokens.PasswordResetToken;
import java.util.List;
import java.util.Optional;

public interface PasswordResetTokenService {
    List<PasswordResetToken> getAllPasswordResetTokens();
    Optional<PasswordResetToken> getPasswordResetTokenById(Long id);
    void addPasswordResetToken(PasswordResetToken passwordResetToken);
    void deletePasswordResetTokenById(Long id);
    PasswordResetToken findByToken (String passwordResetToken);
    boolean isExpired(PasswordResetToken passwordResetToken);
}
