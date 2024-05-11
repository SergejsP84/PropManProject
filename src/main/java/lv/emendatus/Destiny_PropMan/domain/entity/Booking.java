package lv.emendatus.Destiny_PropMan.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.BookingStatus;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.ManagerType;

import java.sql.Timestamp;
import java.util.Objects;
@Getter
@Setter
@Entity
@Table(name = "booking")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Property field is required")
    @ManyToOne
    @JoinColumn(name = "property_id")
    private Property property;

    @Column(name = "tenant_id")
    private Long tenantId;

    @NotBlank(message = "Start date is required")
    @Column(name = "start_date")
    private Timestamp startDate;

    @NotBlank(message = "End date is required")
    @Column(name = "end_date")
    private Timestamp endDate;

    @Column(name = "is_paid")
    private boolean isPaid;

    @NotBlank(message = "Booking status is required")
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