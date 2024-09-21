package lv.emendatus.Destiny_PropMan.service.implementation;

import lv.emendatus.Destiny_PropMan.domain.entity.Refund;
import lv.emendatus.Destiny_PropMan.repository.interfaces.RefundRepository;
import lv.emendatus.Destiny_PropMan.service.interfaces.RefundService;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class JpaRefundService implements RefundService {
    private final RefundRepository repository;
    public JpaRefundService(RefundRepository repository) {
        this.repository = repository;
    }
    @Override
    public List<Refund> getAllRefunds() {
        return repository.findAll();
    }
    @Override
    public Optional<Refund> getRefundById(Long id) {
        return repository.findById(id);
    }
    @Override
    public void addRefund(Refund refund) {
        repository.save(refund);
    }
    @Override
    public void deleteRefund(Long id) {
        repository.deleteById(id);
    }
    @Override
    public List<Refund> getRefundsByTenant(Long tenantId) {
        return getAllRefunds().stream().filter(refund -> refund.getTenantId().equals(tenantId)).toList();
    }
    @Override
    public List<Refund> getRefundsByBooking(Long bookingId) {
        return getAllRefunds().stream().filter(refund -> refund.getBookingId().equals(bookingId)).toList();
    }
}
