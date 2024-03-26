package lv.emendatus.Destiny_PropMan.service.implementation;

import lv.emendatus.Destiny_PropMan.domain.dto.authentication.LoginDTO;
import lv.emendatus.Destiny_PropMan.domain.entity.Manager;
import lv.emendatus.Destiny_PropMan.domain.entity.Tenant;
import lv.emendatus.Destiny_PropMan.service.interfaces.LoginService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class JpaLoginService implements LoginService {
    private final JpaTenantService tenantService;
    private final JpaManagerService managerService;
    private final BCryptPasswordEncoder passwordEncoder;

    public JpaLoginService(JpaTenantService tenantService, JpaManagerService managerService, BCryptPasswordEncoder passwordEncoder) {
        this.tenantService = tenantService;
        this.managerService = managerService;
        this.passwordEncoder = passwordEncoder;
    }
    @Override
    public Tenant authenticateTenant(LoginDTO loginDTO) {
        Tenant tenant = tenantService.getTenantByLogin(loginDTO.getLogin());
        if (tenant != null && passwordEncoder.matches(loginDTO.getPassword(), tenant.getPassword())) {
            return tenant;
        }
        return null;
        // or throw an exception if authentication fails?
    }
    @Override
    public Manager authenticateManager(LoginDTO loginDTO) {
        Manager manager = managerService.getManagerByLogin(loginDTO.getLogin());
        if (manager != null && passwordEncoder.matches(loginDTO.getPassword(), manager.getPassword())) {
            return manager;
        }
        return null;
        // or throw an exception if authentication fails?
    }
}
