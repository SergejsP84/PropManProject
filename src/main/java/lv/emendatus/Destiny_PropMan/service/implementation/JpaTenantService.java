package lv.emendatus.Destiny_PropMan.service.implementation;

import lv.emendatus.Destiny_PropMan.domain.entity.Tenant;
import lv.emendatus.Destiny_PropMan.repository.interfaces.TenantRepository;
import lv.emendatus.Destiny_PropMan.service.interfaces.TenantService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class JpaTenantService implements TenantService {
    private final TenantRepository tenantRepository;
    public JpaTenantService(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
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
}
