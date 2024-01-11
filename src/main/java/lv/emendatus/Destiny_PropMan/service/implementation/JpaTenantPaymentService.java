package lv.emendatus.Destiny_PropMan.service.implementation;

import lv.emendatus.Destiny_PropMan.domain.entity.Bill;
import lv.emendatus.Destiny_PropMan.domain.entity.TenantPayment;
import lv.emendatus.Destiny_PropMan.repository.interfaces.TenantPaymentRepository;
import lv.emendatus.Destiny_PropMan.service.interfaces.TenantPaymentService;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class JpaTenantPaymentService implements TenantPaymentService {

    private final TenantPaymentRepository repository;

    private final Logger LOGGER = LogManager.getLogger(JpaPropertyService.class);

    public JpaTenantPaymentService(TenantPaymentRepository repository) {
        this.repository = repository;
    }
    @Override
    public List<TenantPayment> getAllTenantPayments() {
        return repository.findAll();
    }
    @Override
    public Optional<TenantPayment> getTenantPaymentById(Long id) {
        return repository.findById(id);
    }
    @Override
    public void addTenantPayment(TenantPayment tenantPayment) {
        repository.save(tenantPayment);
    }
    @Override
    public void deleteTenantPayment(Long id) {
        repository.deleteById(id);
    }
    @Override
    public List<TenantPayment> getPaymentsByTenant(Long tenantId) {
        return getAllTenantPayments().stream()
                .filter(tenantPayment -> tenantPayment.getTenant().getId().equals(tenantId)).toList();
    }
    @Override
    public List<TenantPayment> getPaymentsByManager(Long managerId) {
        return getAllTenantPayments().stream()
                .filter(tenantPayment -> tenantPayment.getManagerId().equals(managerId)).toList();
    }
    @Override
    public List<TenantPayment> getPaymentsByProperty(Long propertyId) {
        return getAllTenantPayments().stream()
                .filter(tenantPayment -> tenantPayment.getAssociatedPropertyId().equals(propertyId)).toList();
    }
    @Override
    public List<TenantPayment> getUnsettledPayments() {
        return getAllTenantPayments().stream()
                .filter(tenantPayment -> !(tenantPayment.isFeePaidToManager() && tenantPayment.isReceivedFromTenant())).toList();
    }
    @Override
    public void settlePayment(Long paymentId) {
        Optional<TenantPayment> soughtPayment = repository.findById(paymentId);
        soughtPayment.ifPresent(payment -> {
            payment.setFeePaidToManager(true);
            payment.setReceivedFromTenant(true);
            repository.save(payment);
        });
    }
    @Override
    public List<TenantPayment> getPaymentsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return getAllTenantPayments().stream()
                .filter(payment -> {
                    Timestamp receiptDue = payment.getReceiptDue();
                    return ((receiptDue.after(Timestamp.valueOf(startDate)) || receiptDue.equals(Timestamp.valueOf(startDate)))
                            && (receiptDue.before(Timestamp.valueOf(endDate)) || receiptDue.equals(Timestamp.valueOf(endDate))));
                })
                .collect(Collectors.toList());
    }
}
