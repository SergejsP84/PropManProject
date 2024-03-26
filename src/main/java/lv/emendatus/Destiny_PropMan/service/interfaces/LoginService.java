package lv.emendatus.Destiny_PropMan.service.interfaces;

import lv.emendatus.Destiny_PropMan.domain.dto.authentication.LoginDTO;
import lv.emendatus.Destiny_PropMan.domain.entity.Manager;
import lv.emendatus.Destiny_PropMan.domain.entity.Tenant;

public interface LoginService {
    Tenant authenticateTenant(LoginDTO loginDTO);
    Manager authenticateManager(LoginDTO loginDTO);
}
