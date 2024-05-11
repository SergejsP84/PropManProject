package lv.emendatus.Destiny_PropMan.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.BookingStatus;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.ETRequestStatus;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "early_termination_requests")
public class EarlyTerminationRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id")
    private Long tenantId;

    @NotBlank(message = "Booking ID is required")
    @Column(name = "booking_id")
    private Long bookingId;

    @NotBlank(message = "Termination date is required")
    @Column(name = "termination_date")
    private LocalDateTime terminationDate;

    @Column(name = "comment")
    private String comment;

    @Column(name = "manager_response")
    private String managersResponse;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private ETRequestStatus status;

    @Column(name = "processed_on")
    private LocalDate processedOn;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EarlyTerminationRequest request = (EarlyTerminationRequest) o;
        return Objects.equals(id, request.id) && Objects.equals(tenantId, request.tenantId) && Objects.equals(bookingId, request.bookingId) && Objects.equals(terminationDate, request.terminationDate) && Objects.equals(comment, request.comment) && Objects.equals(managersResponse, request.managersResponse) && status == request.status && Objects.equals(processedOn, request.processedOn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, tenantId, bookingId, terminationDate, comment, managersResponse, status, processedOn);
    }

    @Override
    public String toString() {
        return "EarlyTerminationRequest{" +
                "id=" + id +
                ", tenantId=" + tenantId +
                ", bookingId=" + bookingId +
                ", terminationDate=" + terminationDate +
                ", comment='" + comment + '\'' +
                ", managersResponse='" + managersResponse + '\'' +
                ", status=" + status +
                ", processedOn=" + processedOn +
                '}';
    }
}
