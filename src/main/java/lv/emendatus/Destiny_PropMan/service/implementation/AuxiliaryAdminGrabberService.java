package lv.emendatus.Destiny_PropMan.service.implementation;

import lv.emendatus.Destiny_PropMan.domain.entity.Admin;
import lv.emendatus.Destiny_PropMan.repository.interfaces.AdminRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AuxiliaryAdminGrabberService implements lv.emendatus.Destiny_PropMan.service.interfaces.AuxiliaryAdminGrabberService {

    private AdminRepository adminRepository;

    public AuxiliaryAdminGrabberService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    @Override
    public Optional<Admin> findByLogin(String username) {
        List<Admin> allAdmins = adminRepository.findAll();
        for (Admin admin : allAdmins) {
            if (admin.getLogin().equals(username)) {
                return Optional.of(admin);
            }
        }
        return Optional.empty();
    }
}
