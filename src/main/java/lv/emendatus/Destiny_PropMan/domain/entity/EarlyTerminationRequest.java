package lv.emendatus.Destiny_PropMan.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.BookingStatus;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.ETRequestStatus;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

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

    @Column(name = "booking_id")
    private Long bookingId;

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
}
