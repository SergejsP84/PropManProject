package lv.emendatus.Destiny_PropMan.domain.dto;


import lv.emendatus.Destiny_PropMan.domain.entity.Tenant;

import java.sql.Timestamp;

public class LeasingHistoryDTO {
    private Long id;
    private TenantDTO tenant;
    private Long propertyId;
    private Timestamp startDate;
    private Timestamp endDate;

    public LeasingHistoryDTO() {
    }

    public LeasingHistoryDTO(Long id, TenantDTO tenant, Long propertyId, Timestamp startDate, Timestamp endDate) {
        this.id = id;
        this.tenant = tenant;
        this.propertyId = propertyId;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TenantDTO getTenant() {
        return tenant;
    }

    public void setTenant(TenantDTO tenant) {
        this.tenant = tenant;
    }

    public Long getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(Long propertyId) {
        this.propertyId = propertyId;
    }

    public Timestamp getStartDate() {
        return startDate;
    }

    public void setStartDate(Timestamp startDate) {
        this.startDate = startDate;
    }

    public Timestamp getEndDate() {
        return endDate;
    }

    public void setEndDate(Timestamp endDate) {
        this.endDate = endDate;
    }
}
