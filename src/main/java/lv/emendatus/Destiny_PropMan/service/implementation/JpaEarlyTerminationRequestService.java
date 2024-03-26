package lv.emendatus.Destiny_PropMan.service.implementation;

import lv.emendatus.Destiny_PropMan.domain.entity.EarlyTerminationRequest;
import lv.emendatus.Destiny_PropMan.repository.interfaces.EarlyTerminationRequestRepository;
import lv.emendatus.Destiny_PropMan.service.interfaces.EarlyTerminationRequestService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class JpaEarlyTerminationRequestService implements EarlyTerminationRequestService {
    private final EarlyTerminationRequestRepository repository;
    private final JpaBookingService bookingService;
    public JpaEarlyTerminationRequestService(EarlyTerminationRequestRepository repository, JpaBookingService bookingService) {
        this.repository = repository;
        this.bookingService = bookingService;
    }
    @Override
    public List<EarlyTerminationRequest> getAllETRequests() {
        return repository.findAll();
    }
    @Override
    public Optional<EarlyTerminationRequest> getETRequestById(Long id) {
        return repository.findById(id);
    }
    @Override
    public void addETRequest(EarlyTerminationRequest request) {
        repository.save(request);
    }
    @Override
    public void deleteETRequest(Long id) {
        repository.deleteById(id);
    }
    @Override
    public List<EarlyTerminationRequest> getETRequestsByManager(Long managerId) {
        List<EarlyTerminationRequest> result = new ArrayList<>();
        for (EarlyTerminationRequest request : getAllETRequests()) {
            if (bookingService.getBookingById(request.getBookingId()).isPresent()
            && bookingService.getBookingById(request.getBookingId()).get().getProperty().getManager().getId().equals(managerId))
                result.add(request);
        }
        return result;
    }
    @Override
    public List<EarlyTerminationRequest> getETRequestsByTenant(Long tenantId) {
        return getAllETRequests().stream().filter(request -> request.getTenantId().equals(tenantId)).toList();
    }
    @Override
    public List<EarlyTerminationRequest> getETRequestByBooking(Long bookingId) { // there is supposed to be just one request per booking, but left the List here as a failsafe measure against nasty null pointers
        return getAllETRequests().stream().filter(request -> request.getBookingId().equals(bookingId)).toList();
    }
}
