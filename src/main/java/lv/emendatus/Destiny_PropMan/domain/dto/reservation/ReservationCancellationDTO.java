package lv.emendatus.Destiny_PropMan.domain.dto.reservation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReservationCancellationDTO {
    private Long reservationId; // ID of the reservation to be canceled
    private Long tenantId; // ID of the tenant requesting the cancellation
    private LocalDateTime cancellationDateTime; // Timestamp when the cancellation request is made
    private String reason; // Optional: reason for cancellation provided by the tenant
}
