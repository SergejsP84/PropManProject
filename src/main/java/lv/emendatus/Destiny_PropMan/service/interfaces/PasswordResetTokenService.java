package lv.emendatus.Destiny_PropMan.service.interfaces;

import lv.emendatus.Destiny_PropMan.domain.entity.PropertyAmenity;
import lv.emendatus.Destiny_PropMan.domain.tokens.PasswordResetToken;
import lv.emendatus.Destiny_PropMan.repository.interfaces.PasswordResetTokenRepository;

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
