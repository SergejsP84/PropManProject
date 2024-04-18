package lv.emendatus.Destiny_PropMan.service.interfaces;

import java.sql.Timestamp;

public interface TokenService {
    public String generateToken();

    public void resetTokensOlderThan(Timestamp timestamp);
}
