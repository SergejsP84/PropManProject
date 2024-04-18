package lv.emendatus.Destiny_PropMan.service.interfaces;

import lv.emendatus.Destiny_PropMan.domain.entity.Tenant;
import lv.emendatus.Destiny_PropMan.domain.entity.TokenResetter;

import java.util.List;
import java.util.Optional;

public interface TokenResetService {
    List<TokenResetter> getAllResetters();
    Optional<TokenResetter> getResettersById(Long id);
    void addResetter(TokenResetter tokenResetter);
    void deleteResetter(Long id);
}
