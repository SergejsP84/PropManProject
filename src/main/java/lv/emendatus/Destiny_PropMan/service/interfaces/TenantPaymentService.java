package lv.emendatus.Destiny_PropMan.service.interfaces;

import lv.emendatus.Destiny_PropMan.domain.entity.TenantPayment;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TenantPaymentService {
    List<TenantPayment> getAllTenantPayments();
    Optional<TenantPayment> getTenantPaymentById(Long id);
    void addTenantPayment(TenantPayment tenantPayment);
    void deleteTenantPayment(Long id);
    List<TenantPayment> getPaymentsByTenant(Long tenantId);
    List<TenantPayment> getPaymentsByManager(Long managerId);
    List<TenantPayment> getPaymentsByProperty(Long propertyId);
    List<TenantPayment> getUnsettledPayments();
    void settlePayment(Long paymentId);
    List<TenantPayment> getPaymentsByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    TenantPayment getPaymentByBooking(Long bookingId);
}
