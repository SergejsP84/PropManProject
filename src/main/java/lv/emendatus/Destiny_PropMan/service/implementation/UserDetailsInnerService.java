package lv.emendatus.Destiny_PropMan.service.implementation;

import lv.emendatus.Destiny_PropMan.domain.entity.Manager;
import lv.emendatus.Destiny_PropMan.domain.entity.Tenant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserDetailsInnerService implements UserDetailsService {

    private final AuxiliaryAdminGrabberService adminGrabberService;
    private final JpaTenantService tenantService;
    private final JpaManagerService managerService;

    @Autowired
    public UserDetailsInnerService(AuxiliaryAdminGrabberService adminGrabberService, JpaTenantService tenantService, JpaManagerService managerService) {
        this.adminGrabberService = adminGrabberService;
        this.tenantService = tenantService;
        this.managerService = managerService;
    }
    public boolean tenantLoginFound(List<Tenant> allTenants, String login) {
        for (Tenant tenant : allTenants) {
            if (tenant.getLogin().equals(login)) return true;
        }
        return false;
    }
    public boolean managerLoginFound(List<Manager> allManagers, String login) {
        for (Manager manager : allManagers) {
            if (manager.getLogin().equals(login)) return true;
        }
        return false;
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {


        if (tenantLoginFound(tenantService.getAllTenants(), username)) {
            return User.builder()
                    .username(username)
                    .password("{bcrypt}" + tenantService.getTenantByLogin(username).getPassword())
//                    .password(tenantService.getTenantByLogin(username).getPassword())
                    .authorities(tenantService.getTenantByLogin(username).getAuthorities())
                    .build();
        } else if (managerLoginFound(managerService.getAllManagers(), username)) {
            return User.builder()
                    .username(username)
                    .password("{bcrypt}" + managerService.getManagerByLogin(username).getPassword())
//                    .password(managerService.getManagerByLogin(username).getPassword())
                    .authorities(managerService.getManagerByLogin(username).getAuthorities())
                    .build();
        } else if (adminGrabberService.findByLogin(username).isPresent()) {
//            System.out.println();
//            System.out.println("Setting the following Admin password to User: {bcrypt}" + adminGrabberService.findByLogin(username).get().getPassword());
//            System.out.println();
//            System.out.println("The actual encrypted password from the database is " + adminGrabberService.findByLogin(username).get().getPassword());
//            System.out.println();
            return User.builder()
                    .username(username)
                    .password("{bcrypt}" + adminGrabberService.findByLogin(username).get().getPassword())
//                    .password(adminGrabberService.findByLogin(username).get().getPassword())
                    .authorities(adminGrabberService.findByLogin(username).get().getAuthorities())
                    .build();
        } else {
            throw new UsernameNotFoundException("Could not find a user with username: " + username);
        }
    }
}
