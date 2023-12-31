package lv.emendatus.Destiny_PropMan.domain.entity;

import jakarta.persistence.*;

import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "leasing_history")
public class LeasingHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "tenant_id")
    private Tenant tenant;

    @Column(name = "property_id")
    private Long propertyId;

    @Column(name = "start_date")
    private Timestamp startDate;

    @Column(name = "end_date")
    private Timestamp endDate;

    public LeasingHistory() {
    }

    public LeasingHistory(Long id, Tenant tenant, Long propertyId, Timestamp startDate, Timestamp endDate) {
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

    public Tenant getTenant() {
        return tenant;
    }

    public void setTenant(Tenant tenant) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LeasingHistory that = (LeasingHistory) o;
        return Objects.equals(id, that.id) && Objects.equals(tenant, that.tenant) && Objects.equals(propertyId, that.propertyId) && Objects.equals(startDate, that.startDate) && Objects.equals(endDate, that.endDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, tenant, propertyId, startDate, endDate);
    }

    @Override
    public String toString() {
        return "LeasingHistory{" +
                "id=" + id +
                ", tenant=" + tenant +
                ", propertyId=" + propertyId +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
}