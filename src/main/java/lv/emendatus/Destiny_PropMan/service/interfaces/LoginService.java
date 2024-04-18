package lv.emendatus.Destiny_PropMan.service.interfaces;

import jakarta.servlet.http.HttpServletRequest;
import lv.emendatus.Destiny_PropMan.domain.dto.authentication.LoginDTO;
import lv.emendatus.Destiny_PropMan.domain.entity.Admin;
import lv.emendatus.Destiny_PropMan.domain.entity.Manager;
import lv.emendatus.Destiny_PropMan.domain.entity.Tenant;

public interface LoginService {
    Tenant authenticateTenant(LoginDTO loginDTO, HttpServletRequest request);
    Manager authenticateManager(LoginDTO loginDTO, HttpServletRequest request);
    Admin authenticateAdmin(LoginDTO loginDTO, HttpServletRequest request);
}
