package lv.emendatus.Destiny_PropMan.service.interfaces;

import jakarta.servlet.http.HttpServletRequest;
import lv.emendatus.Destiny_PropMan.domain.dto.authentication.LoginDTO;
import lv.emendatus.Destiny_PropMan.domain.entity.Admin;
import lv.emendatus.Destiny_PropMan.domain.entity.Manager;
import lv.emendatus.Destiny_PropMan.domain.entity.Tenant;
import org.springframework.security.core.userdetails.UserDetails;

public interface LoginService {
    UserDetails authenticateTenant(LoginDTO loginDTO, HttpServletRequest request);
    UserDetails authenticateManager(LoginDTO loginDTO, HttpServletRequest request);
    UserDetails authenticateAdmin(LoginDTO loginDTO, HttpServletRequest request);
}
