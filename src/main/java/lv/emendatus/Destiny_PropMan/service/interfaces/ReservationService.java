package lv.emendatus.Destiny_PropMan.service.interfaces;

import lv.emendatus.Destiny_PropMan.domain.dto.reservation.ConfirmationDTO;
import lv.emendatus.Destiny_PropMan.domain.dto.reservation.ErrorDTO;
import lv.emendatus.Destiny_PropMan.domain.dto.reservation.ReservationCancellationDTO;
import lv.emendatus.Destiny_PropMan.domain.dto.reservation.ReservationRequestDTO;
import org.springframework.http.ResponseEntity;

public interface ReservationService {
    ConfirmationDTO makeReservation(ReservationRequestDTO reservationRequest);

    ResponseEntity<String> cancelReservation(ReservationCancellationDTO cancellationRequest);
}
