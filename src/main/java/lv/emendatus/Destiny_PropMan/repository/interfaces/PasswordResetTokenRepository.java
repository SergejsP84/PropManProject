package lv.emendatus.Destiny_PropMan.repository.interfaces;

import lv.emendatus.Destiny_PropMan.domain.tokens.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
}
