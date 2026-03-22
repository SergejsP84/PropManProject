package lv.emendatus.Destiny_PropMan.service.implementation;

import lv.emendatus.Destiny_PropMan.domain.entity.TenantPayment;
import lv.emendatus.Destiny_PropMan.repository.interfaces.TenantPaymentRepository;
import lv.emendatus.Destiny_PropMan.service.interfaces.TenantPaymentService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class JpaTenantPaymentService implements TenantPaymentService {

    private final TenantPaymentRepository repository;
    private final Logger LOGGER = LogManager.getLogger(JpaTenantPaymentService.class);

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
        return repository.findByTenant_Id(tenantId);
    }

    @Override
    public List<TenantPayment> getPaymentsByManager(Long managerId) {
        return repository.findByManagerId(managerId);
    }

    @Override
    public List<TenantPayment> getPaymentsByProperty(Long propertyId) {
        return repository.findByAssociatedPropertyId(propertyId);
    }

    @Override
    public List<TenantPayment> getUnsettledPayments() {
        return repository.findUnsettledPayments();
    }

    @Override
    public void settlePayment(Long paymentId) {
        repository.findById(paymentId).ifPresent(payment -> {
            payment.setFeePaidToManager(true);
            payment.setReceivedFromTenant(true);
            repository.save(payment);
        });
    }

    @Override
    public List<TenantPayment> getPaymentsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return repository.findByReceiptDueBetween(Timestamp.valueOf(startDate), Timestamp.valueOf(endDate));
    }

    @Override
    public TenantPayment getPaymentByBooking(Long bookingId) {
        return repository.findByAssociatedBookingId(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Could not retrieve a payment for the associated booking"));
    }
}
