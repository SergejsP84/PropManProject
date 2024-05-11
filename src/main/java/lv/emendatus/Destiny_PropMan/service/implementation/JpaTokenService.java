package lv.emendatus.Destiny_PropMan.service.implementation;

import lv.emendatus.Destiny_PropMan.service.interfaces.TokenService;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.UUID;

@Service
public class JpaTokenService implements TokenService {
    @Override
    public String generateToken() {
        return UUID.randomUUID().toString();
    }

    @Override
    public void resetTokensOlderThan(Timestamp timestamp) {
        // добавить или нет? Вроде уже реализовано
    }
}
