package lv.emendatus.Destiny_PropMan.service.implementation;

import lv.emendatus.Destiny_PropMan.domain.dto.profile.BookingHistoryDTO;
import lv.emendatus.Destiny_PropMan.domain.dto.profile.LeasingHistoryDTO_Profile;
import lv.emendatus.Destiny_PropMan.domain.dto.profile.TenantDTO_Profile;
import lv.emendatus.Destiny_PropMan.domain.entity.LeasingHistory;
import lv.emendatus.Destiny_PropMan.domain.entity.Tenant;
import lv.emendatus.Destiny_PropMan.mapper.LeasingHistoryMapper;
import lv.emendatus.Destiny_PropMan.mapper.TenantMapper;
import lv.emendatus.Destiny_PropMan.repository.interfaces.TenantRepository;
import lv.emendatus.Destiny_PropMan.service.interfaces.TenantService;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class JpaTenantService implements TenantService {
    private final TenantRepository tenantRepository;
    private final Logger LOGGER = LogManager.getLogger(JpaPropertyService.class);
    private final TenantMapper tenantMapper;
    private final LeasingHistoryMapper leasingHistoryMapper;
    private final JpaLeasingHistoryService leasingHistoryService;

    public JpaTenantService(TenantRepository tenantRepository, TenantMapper tenantMapper, LeasingHistoryMapper leasingHistoryMapper, JpaLeasingHistoryService leasingHistoryService) {
        this.tenantRepository = tenantRepository;
        this.tenantMapper = tenantMapper;
        this.leasingHistoryMapper = leasingHistoryMapper;
        this.leasingHistoryService = leasingHistoryService;
    }
    @Override
    public List<Tenant> getAllTenants() {
        return tenantRepository.findAll();
    }
    @Override
    public Optional<Tenant> getTenantById(Long id) {
        return tenantRepository.findById(id);
    }
    @Override
    public void addTenant(Tenant tenant) {
        tenantRepository.save(tenant);
    }
    @Override
    public void deleteTenant(Long id) {
        tenantRepository.deleteById(id);
    }
    @Override
    public List<Tenant> getTenantsByFirstNameOrLastName(String name) {
        List<Tenant> allTenants = getAllTenants();
        String lowercaseName = name.toLowerCase();
        return allTenants.stream()
                .filter(tenant -> tenant.getFirstName().toLowerCase().contains(lowercaseName) ||
                        tenant.getLastName().toLowerCase().contains(lowercaseName))
                .toList();
    }

    @Override
    public Tenant getTenantByLogin(String login) {
        for (Tenant tenant : getAllTenants()) {
            if (tenant.getLogin().equals(login)) return tenant;
        }
        return null;
    }
    @Override
    public Tenant getTenantByEmail(String email) {
        for (Tenant tenant : getAllTenants()) {
            if (tenant.getEmail().equals(email)) return tenant;
        }
        return null;
    }

    @Override
    public Tenant getTenantByConfirmationToken(String confirmationToken) {
        for (Tenant tenant : getAllTenants()) {
            if (tenant.getConfirmationToken().equals(confirmationToken)) return tenant;
        }
        return null;
    }

    @Override
    public boolean isTokenValid(Tenant tenant, String token) {
        return tenant != null && token.equals(tenant.getConfirmationToken());
    }

}
