package lv.emendatus.Destiny_PropMan.repository.interfaces;

import lv.emendatus.Destiny_PropMan.domain.entity.TenantPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public interface TenantPaymentRepository extends JpaRepository<TenantPayment, Long> {

    List<TenantPayment> findByTenant_Id(Long tenantId);

    List<TenantPayment> findByManagerId(Long managerId);

    List<TenantPayment> findByAssociatedPropertyId(Long propertyId);

    Optional<TenantPayment> findByAssociatedBookingId(Long bookingId);

    @Query("SELECT tp FROM TenantPayment tp WHERE tp.feePaidToManager = false OR tp.receivedFromTenant = false")
    List<TenantPayment> findUnsettledPayments();

    @Query("SELECT tp FROM TenantPayment tp WHERE tp.receiptDue >= :start AND tp.receiptDue <= :end")
    List<TenantPayment> findByReceiptDueBetween(@Param("start") Timestamp start, @Param("end") Timestamp end);
}
