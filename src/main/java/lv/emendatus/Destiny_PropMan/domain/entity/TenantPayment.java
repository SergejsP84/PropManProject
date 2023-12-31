package lv.emendatus.Destiny_PropMan.domain.entity;

import jakarta.persistence.*;

import java.sql.Timestamp;
import java.util.Objects;
import java.util.Set;

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

    @Column(name = "received_from_tenant")
    private boolean receivedFromTenant;

    @Column(name = "fee_paid_to_manager")
    private boolean feePaidToManager;

    @Column(name = "receipt_due")
    private Timestamp receiptDue;

    public TenantPayment() {
    }

    public TenantPayment(Long id, Double amount, Tenant tenant, Long managerId, Long associatedPropertyId, boolean receivedFromTenant, boolean feePaidToManager, Timestamp receiptDue) {
        this.id = id;
        this.amount = amount;
        this.tenant = tenant;
        this.managerId = managerId;
        this.associatedPropertyId = associatedPropertyId;
        this.receivedFromTenant = receivedFromTenant;
        this.feePaidToManager = feePaidToManager;
        this.receiptDue = receiptDue;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Tenant getTenant() {
        return tenant;
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }

    public Long getManagerId() {
        return managerId;
    }

    public void setManagerId(Long managerId) {
        this.managerId = managerId;
    }

    public Long getAssociatedPropertyId() {
        return associatedPropertyId;
    }

    public void setAssociatedPropertyId(Long associatedPropertyId) {
        this.associatedPropertyId = associatedPropertyId;
    }

    public boolean isReceivedFromTenant() {
        return receivedFromTenant;
    }

    public void setReceivedFromTenant(boolean receivedFromTenant) {
        this.receivedFromTenant = receivedFromTenant;
    }

    public boolean isFeePaidToManager() {
        return feePaidToManager;
    }

    public void setFeePaidToManager(boolean feePaidToManager) {
        this.feePaidToManager = feePaidToManager;
    }

    public Timestamp getReceiptDue() {
        return receiptDue;
    }

    public void setReceiptDue(Timestamp receiptDue) {
        this.receiptDue = receiptDue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TenantPayment that = (TenantPayment) o;
        return receivedFromTenant == that.receivedFromTenant && feePaidToManager == that.feePaidToManager && Objects.equals(id, that.id) && Objects.equals(amount, that.amount) && Objects.equals(tenant, that.tenant) && Objects.equals(managerId, that.managerId) && Objects.equals(associatedPropertyId, that.associatedPropertyId) && Objects.equals(receiptDue, that.receiptDue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, amount, tenant, managerId, associatedPropertyId, receivedFromTenant, feePaidToManager, receiptDue);
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
                ", feePaidToManager=" + feePaidToManager +
                ", receiptDue=" + receiptDue +
                '}';
    }
}