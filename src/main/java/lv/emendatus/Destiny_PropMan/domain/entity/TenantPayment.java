package lv.emendatus.Destiny_PropMan.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tenant_payments")
public class TenantPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "amount", precision = 10)
    private Double amount;

    @ManyToOne
    @JoinColumn(name = "tenant_id")
    private Tenant tenant;

    @Column(name = "manager_id")
    private Long managerId;

    @Column(name = "associated_property_id")
    private Long associatedPropertyId;

    @Column(name = "associated_booking_id")
    private Long associatedBookingId;

    @Column(name = "received_from_tenant")
    private boolean receivedFromTenant;

    @Column(name = "managerPayment", precision = 10)
    private Double managerPayment;

    @Column(name = "fee_paid_to_manager")
    private boolean feePaidToManager;

    @Column(name = "receipt_due")
    private Timestamp receiptDue;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TenantPayment that = (TenantPayment) o;
        return receivedFromTenant == that.receivedFromTenant && feePaidToManager == that.feePaidToManager && Objects.equals(id, that.id) && Objects.equals(amount, that.amount) && Objects.equals(tenant, that.tenant) && Objects.equals(managerId, that.managerId) && Objects.equals(associatedPropertyId, that.associatedPropertyId) && Objects.equals(managerPayment, that.managerPayment) && Objects.equals(receiptDue, that.receiptDue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, amount, tenant, managerId, associatedPropertyId, receivedFromTenant, managerPayment, feePaidToManager, receiptDue);
    }

    @Override
    public String toString() {
        return "TenantPayment{" +
                "id=" + id +
                ", amount=" + amount +
                ", tenant=" + tenant +
                ", managerId=" + managerId +
                ", associatedPropertyId=" + associatedPropertyId +
                ", receivedFromTenant=" + receivedFromTenant +
                ", managerPayment=" + managerPayment +
                ", feePaidToManager=" + feePaidToManager +
                ", receiptDue=" + receiptDue +
                '}';
    }
}