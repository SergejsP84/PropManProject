package lv.emendatus.Destiny_PropMan.repository.interfaces;

import lv.emendatus.Destiny_PropMan.domain.entity.LeasingHistory;
import lv.emendatus.Destiny_PropMan.domain.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface LeasingHistoryRepository extends JpaRepository<LeasingHistory, Long> {

    List<LeasingHistory> findByPropertyId(Long propertyId);

    List<LeasingHistory> findByTenant(Tenant tenant);

    @Query("SELECT lh FROM LeasingHistory lh WHERE lh.startDate >= :startTs AND lh.endDate <= :endTs")
    List<LeasingHistory> findByTimePeriod(@Param("startTs") Timestamp startTs, @Param("endTs") Timestamp endTs);
}
