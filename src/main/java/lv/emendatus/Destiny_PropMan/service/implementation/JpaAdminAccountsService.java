package lv.emendatus.Destiny_PropMan.service.implementation;

import jakarta.annotation.PostConstruct;
import org.springframework.transaction.annotation.Transactional;
import lv.emendatus.Destiny_PropMan.domain.entity.Admin;
import lv.emendatus.Destiny_PropMan.repository.interfaces.AdminRepository;
import lv.emendatus.Destiny_PropMan.service.interfaces.AdminAccountsService;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class JpaAdminAccountsService implements AdminAccountsService {
    @Autowired
    private AdminRepository adminRepository;
    private final JpaTenantService tenantService;
    private final JpaManagerService managerService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final Logger LOGGER = LogManager.getLogger(JpaTenantRegistrationService.class);

    public JpaAdminAccountsService(JpaTenantService tenantService, JpaManagerService managerService, BCryptPasswordEncoder passwordEncoder) {
        this.tenantService = tenantService;
        this.managerService = managerService;
        this.passwordEncoder = passwordEncoder;
    }
    @Override
    public List<Admin> findAll() {
        return adminRepository.findAll();
    }
    @Override
    public void addAdmin(Admin admin) {
        adminRepository.save(admin);
    }
    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') and principal.username == 'DefaultAdmin'")
    @Transactional
    public void createAdmin(String name, String login, String password) {
        if (tenantService.getTenantByLogin(login) == null && managerService.getManagerByLogin(login) == null && findByLogin(login).isEmpty()) {
            Admin admin = new Admin();
            admin.setName(name);
            admin.setLogin(login);
            admin.setPassword(passwordEncoder.encode(password));
            List<String> knownIPs = new ArrayList<>();
            admin.setKnownIps(knownIPs);
            addAdmin(admin);
        } else {
            LOGGER.log(Level.INFO, "This login already exists, please select a different one.");
            System.out.println("This login already exists, please select a different one.");
        }

    }
    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') and principal.username == 'DefaultAdmin'")
    @Transactional
    public void deleteAdminByLogin(String login) {
        Optional<Admin> admin = findByLogin(login);
        if (admin.isPresent()) {
            if (!admin.get().getName().equals("DefaultAdmin")) {
                Long id = admin.get().getId();
                LOGGER.log(Level.INFO, "Admin " + admin.get().getName() + " deleted.");
                System.out.println("Admin " + admin.get().getName() + " deleted.");
                adminRepository.deleteById(id);
            } else {
                LOGGER.log(Level.INFO, "Cannot delete the default admin.");
                System.out.println("Cannot delete the default admin.");
            }
        } else {
            LOGGER.log(Level.INFO, "Cannot delete admin: the entered login could not be found.");
            System.out.println("No admin with this login could be found");
        }
    }
    @Override
    public Optional<Admin> findByLogin(String login) {
        List<Admin> allAdmins = adminRepository.findAll();
        for (Admin admin : allAdmins) {
            if (admin.getLogin().equals(login)) {
                return Optional.of(admin);
            }
        }
        return Optional.empty();
    }
    @Override
    @PostConstruct
    public void createDefaultAdmin() {
        if (!findByLogin("DefaultAdmin").isPresent()) {
            Admin defaultAdmin = new Admin();
            defaultAdmin.setName("DefaultAdmin");
            defaultAdmin.setLogin("DefaultAdmin");
            defaultAdmin.setPassword("DefaultPassword");
            List<String> knownIPs = new ArrayList<>();
            defaultAdmin.setKnownIps(knownIPs);
            addAdmin(defaultAdmin);
        }
    }

    // AUXILIARY METHOD
    public void addAnIpForAdmin(String login, String newIP) {
        if (findByLogin(login).isPresent()) {
            throw new RuntimeException("Error fetching the admin credentials");
        } else {
            Admin admin = findByLogin(login).get();
            List<String> knownIps = admin.getKnownIps();
            knownIps.add(newIP);
            admin.setKnownIps(knownIps);
            adminRepository.save(admin);
        }
    }
}
