package lv.emendatus.Destiny_PropMan.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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

    @NotBlank(message = "Amount is required")
    @Column(name = "amount", precision = 10)
    private Double amount;
    @ManyToOne
    @JoinColumn(name = "currency_id")
    private Currency currency;
    @ManyToOne
    @JoinColumn(name = "tenant_id")
    private Tenant tenant;

    @NotBlank(message = "Manager ID is required")
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
        TenantPayment payment = (TenantPayment) o;
        return receivedFromTenant == payment.receivedFromTenant && feePaidToManager == payment.feePaidToManager && Objects.equals(id, payment.id) && Objects.equals(amount, payment.amount) && Objects.equals(currency, payment.currency) && Objects.equals(tenant, payment.tenant) && Objects.equals(managerId, payment.managerId) && Objects.equals(associatedPropertyId, payment.associatedPropertyId) && Objects.equals(associatedBookingId, payment.associatedBookingId) && Objects.equals(managerPayment, payment.managerPayment) && Objects.equals(receiptDue, payment.receiptDue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, amount, currency, tenant, managerId, associatedPropertyId, associatedBookingId, receivedFromTenant, managerPayment, feePaidToManager, receiptDue);
    }

    @Override
    public String toString() {
        return "TenantPayment{" +
                "id=" + id +
                ", amount=" + amount +
                ", currency=" + currency +
                ", tenant=" + tenant +
                ", managerId=" + managerId +
                ", associatedPropertyId=" + associatedPropertyId +
                ", associatedBookingId=" + associatedBookingId +
                ", receivedFromTenant=" + receivedFromTenant +
                ", managerPayment=" + managerPayment +
                ", feePaidToManager=" + feePaidToManager +
                ", receiptDue=" + receiptDue +
                '}';
    }
}