package lv.emendatus.Destiny_PropMan.domain.entity;

import jakarta.persistence.*;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.BookingStatus;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.ManagerType;

import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "booking")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "property_id")
    private Property property;

    @Column(name = "tenant_id")
    private Long tenantId;

    @Column(name = "start_date")
    private Timestamp startDate;

    @Column(name = "end_date")
    private Timestamp endDate;

    @Column(name = "is_paid")
    private boolean isPaid;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    public Booking() {
        //empty
    }

    public Booking(Long id, Property property, Long tenantId, Timestamp startDate, Timestamp endDate, boolean isPaid, BookingStatus status) {
        this.id = id;
        this.property = property;
        this.tenantId = tenantId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isPaid = isPaid;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
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

    public boolean isPaid() {
        return isPaid;
    }

    public void setPaid(boolean paid) {
        isPaid = paid;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Booking booking = (Booking) o;
        return isPaid == booking.isPaid && Objects.equals(id, booking.id) && Objects.equals(property, booking.property) && Objects.equals(tenantId, booking.tenantId) && Objects.equals(startDate, booking.startDate) && Objects.equals(endDate, booking.endDate) && status == booking.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, property, tenantId, startDate, endDate, isPaid, status);
    }

    @Override
    public String toString() {
        return "Booking{" +
                "id=" + id +
                ", property=" + property +
                ", tenantId=" + tenantId +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", isPaid=" + isPaid +
                ", status=" + status +
                '}';
    }
}