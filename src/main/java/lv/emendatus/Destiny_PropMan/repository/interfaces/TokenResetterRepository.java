package lv.emendatus.Destiny_PropMan.repository.interfaces;

import lv.emendatus.Destiny_PropMan.domain.entity.Tenant;
import lv.emendatus.Destiny_PropMan.domain.entity.TokenResetter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenResetterRepository extends JpaRepository<TokenResetter, Long> {
}
