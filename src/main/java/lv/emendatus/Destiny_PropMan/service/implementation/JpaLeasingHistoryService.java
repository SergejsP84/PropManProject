package lv.emendatus.Destiny_PropMan.service.implementation;

import lv.emendatus.Destiny_PropMan.domain.entity.LeasingHistory;
import lv.emendatus.Destiny_PropMan.domain.entity.Property;
import lv.emendatus.Destiny_PropMan.domain.entity.Tenant;
import lv.emendatus.Destiny_PropMan.repository.interfaces.LeasingHistoryRepository;
import lv.emendatus.Destiny_PropMan.repository.interfaces.PropertyRepository;
import lv.emendatus.Destiny_PropMan.repository.interfaces.TenantRepository;
import lv.emendatus.Destiny_PropMan.service.interfaces.LeasingHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
public class JpaLeasingHistoryService implements LeasingHistoryService {
    private final LeasingHistoryRepository leasingHistoryRepository;
    private final PropertyRepository propertyRepository;
    private final TenantRepository tenantRepository;
    @Autowired
    public JpaLeasingHistoryService(LeasingHistoryRepository leasingHistoryRepository, PropertyRepository propertyRepository, TenantRepository tenantRepository) {
        this.leasingHistoryRepository = leasingHistoryRepository;
        this.propertyRepository = propertyRepository;
        this.tenantRepository = tenantRepository;
    }
    @Override
    public List<LeasingHistory> getAllLeasingHistories() {
        return leasingHistoryRepository.findAll();
    }
    @Override
    public Optional<LeasingHistory> getLeasingHistoryById(Long id) {
        return leasingHistoryRepository.findById(id);
    }
    @Override
    public void addLeasingHistory(LeasingHistory leasingHistory) {
        leasingHistoryRepository.save(leasingHistory);
    }
    @Override
    public void deleteLeasingHistory(Long id) {
        leasingHistoryRepository.deleteById(id);
    }
    @Override
    public List<LeasingHistory> getLeasingHistoryByProperty(Property property) {
        return getAllLeasingHistories().stream()
                .filter(leasingHistory -> {
                    Optional<Property> optionalProperty = propertyRepository.findById(leasingHistory.getPropertyId());
                    return optionalProperty.isPresent() && optionalProperty.get().equals(property);
                })
                .toList();
    }
    @Override
    public List<LeasingHistory> getLeasingHistoryByTimePeriod(Timestamp startDate, Timestamp endDate) {
        return getAllLeasingHistories().stream()
                .filter(leasingHistory ->
                        (leasingHistory.getEndDate().before(endDate) || leasingHistory.getEndDate().equals(endDate))
                                && (leasingHistory.getStartDate().after(startDate) || leasingHistory.getStartDate().equals(startDate))
                )
                .toList();
    }
    @Override
    public List<LeasingHistory> getLeasingHistoryByTenant(Tenant tenant) {
        return getAllLeasingHistories().stream()
                .filter(leasingHistory -> {
                    Optional<Tenant> optionalTenant = tenantRepository.findById(leasingHistory.getTenant().getId());
                    return optionalTenant.isPresent() && optionalTenant.get().equals(tenant);
                })
                .toList();
    }
}
