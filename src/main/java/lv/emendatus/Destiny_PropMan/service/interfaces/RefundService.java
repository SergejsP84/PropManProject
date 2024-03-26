package lv.emendatus.Destiny_PropMan.service.interfaces;

import lv.emendatus.Destiny_PropMan.domain.entity.Refund;
import lv.emendatus.Destiny_PropMan.domain.entity.Tenant;

import java.util.List;
import java.util.Optional;

public interface RefundService {
    List<Refund> getAllRefunds();
    Optional<Refund> getRefundById(Long id);
    void addRefund(Refund refund);
    void deleteRefund(Long id);
    List<Refund> getRefundsByTenant(Long tenantId);
    List<Refund> getRefundsByBooking(Long bookingId);
}
