package lv.emendatus.Destiny_PropMan.service.implementation;

import lv.emendatus.Destiny_PropMan.domain.entity.NumericalConfig;
import lv.emendatus.Destiny_PropMan.domain.tokens.PasswordResetToken;
import lv.emendatus.Destiny_PropMan.repository.interfaces.PasswordResetTokenRepository;
import lv.emendatus.Destiny_PropMan.service.interfaces.PasswordResetTokenService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class JpaPasswordResetTokenService implements PasswordResetTokenService {
    private static long expirationMinutes = 5; // Default
    private final PasswordResetTokenRepository repository;
    private final JpaNumericalConfigService configService;
    public JpaPasswordResetTokenService(PasswordResetTokenRepository repository, JpaNumericalConfigService configService) {
        this.repository = repository;
        this.configService = configService;
    }
    @Override
    public List<PasswordResetToken> getAllPasswordResetTokens() {
        return repository.findAll();
    }
    @Override
    public Optional<PasswordResetToken> getPasswordResetTokenById(Long id) {
        return repository.findById(id);
    }
    @Override
    public void addPasswordResetToken(PasswordResetToken passwordResetToken) {
        repository.save(passwordResetToken);
    }
    @Override
    public void deletePasswordResetTokenById(Long id) {
        repository.deleteById(id);
    }
    @Override
    public PasswordResetToken findByToken(String token) {
        List<PasswordResetToken> allTokens = getAllPasswordResetTokens();
        for (PasswordResetToken currentToken : allTokens) {
            if (currentToken.getToken().equals(token)) {
                return currentToken;
            }
        }
        return null;
    }

    @Override
    public boolean isExpired(PasswordResetToken passwordResetToken) {
        long timeWindow = expirationMinutes;
        for (NumericalConfig config : configService.getSystemSettings()) {
            if (config.getName().equals("PasswordTokenExpiresInMinutes")) timeWindow = config.getValue().intValue();
        }
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expirationTime = passwordResetToken.getCreationTime().plus(timeWindow, ChronoUnit.MINUTES);
        return now.isAfter(expirationTime);
    }
}
