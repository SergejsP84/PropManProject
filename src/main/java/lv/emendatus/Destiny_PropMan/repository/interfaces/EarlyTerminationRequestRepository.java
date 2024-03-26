package lv.emendatus.Destiny_PropMan.repository.interfaces;

import lv.emendatus.Destiny_PropMan.domain.entity.EarlyTerminationRequest;
import lv.emendatus.Destiny_PropMan.domain.entity.LeasingHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EarlyTerminationRequestRepository extends JpaRepository<EarlyTerminationRequest, Long> {
}
