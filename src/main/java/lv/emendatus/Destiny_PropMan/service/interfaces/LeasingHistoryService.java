package lv.emendatus.Destiny_PropMan.service.interfaces;

import lv.emendatus.Destiny_PropMan.domain.entity.LeasingHistory;
import lv.emendatus.Destiny_PropMan.domain.entity.Property;
import lv.emendatus.Destiny_PropMan.domain.entity.Tenant;;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface LeasingHistoryService {
    List<LeasingHistory> getAllLeasingHistories();
    Optional<LeasingHistory> getLeasingHistoryById(Long id);
    void addLeasingHistory(LeasingHistory leasingHistory);
    void deleteLeasingHistory(Long id);
    List<LeasingHistory> getLeasingHistoryByProperty(Property property);
    List<LeasingHistory> getLeasingHistoryByTimePeriod(LocalDateTime startDate, LocalDateTime endDate);
    List<LeasingHistory> getLeasingHistoryByTenant(Tenant tenant);
}
