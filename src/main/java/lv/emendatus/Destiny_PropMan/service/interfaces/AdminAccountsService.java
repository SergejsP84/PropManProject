package lv.emendatus.Destiny_PropMan.service.interfaces;

import lv.emendatus.Destiny_PropMan.domain.dto.registration.AdminRegistrationDTO;
import lv.emendatus.Destiny_PropMan.domain.entity.Admin;

import java.util.List;
import java.util.Optional;

public interface AdminAccountsService {
    List<Admin> findAll();
    Optional<Admin> findByLogin(String login);
    void createDefaultAdmin();
    void addAdmin(Admin admin);
    void createAdmin(AdminRegistrationDTO dto);
    void deleteAdminByLogin(String login);
}
