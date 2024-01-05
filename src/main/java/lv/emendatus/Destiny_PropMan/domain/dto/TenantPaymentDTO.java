package lv.emendatus.Destiny_PropMan.domain.dto;

import lv.emendatus.Destiny_PropMan.domain.entity.Tenant;

import java.sql.Timestamp;

public class TenantPaymentDTO {
    private Long id;
    private Double amount;
    private Tenant tenant;
    private Long managerId;
    private Long associatedPropertyId;
    private boolean receivedFromTenant;
    private boolean feePaidToManager;
    private Timestamp receiptDue;

    public TenantPaymentDTO() {
    }

    public TenantPaymentDTO(Long id, Double amount, Tenant tenant, Long managerId, Long associatedPropertyId, boolean receivedFromTenant, boolean feePaidToManager, Timestamp receiptDue) {
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
}
