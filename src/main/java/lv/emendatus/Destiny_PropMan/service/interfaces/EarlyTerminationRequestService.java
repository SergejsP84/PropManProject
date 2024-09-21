package lv.emendatus.Destiny_PropMan.service.interfaces;

import lv.emendatus.Destiny_PropMan.domain.entity.EarlyTerminationRequest;
import java.util.List;
import java.util.Optional;

public interface EarlyTerminationRequestService {
    List<EarlyTerminationRequest> getAllETRequests();
    Optional<EarlyTerminationRequest> getETRequestById(Long id);
    void addETRequest(EarlyTerminationRequest request);
    void deleteETRequest(Long id);
    List<EarlyTerminationRequest> getETRequestsByManager(Long managerId);
    List<EarlyTerminationRequest> getETRequestsByTenant(Long tenantId);
    List<EarlyTerminationRequest> getETRequestByBooking(Long bookingId);

}
