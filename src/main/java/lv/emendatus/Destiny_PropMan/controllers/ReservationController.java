package lv.emendatus.Destiny_PropMan.controllers;

import lv.emendatus.Destiny_PropMan.annotation.reservation_controler.CancelReservation;
import lv.emendatus.Destiny_PropMan.annotation.reservation_controler.MakeReservation;
import lv.emendatus.Destiny_PropMan.domain.dto.reservation.ConfirmationDTO;
import lv.emendatus.Destiny_PropMan.domain.dto.reservation.ReservationCancellationDTO;
import lv.emendatus.Destiny_PropMan.domain.dto.reservation.ReservationRequestDTO;
import lv.emendatus.Destiny_PropMan.service.implementation.JpaReservationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reservation")
public class ReservationController {
    public final JpaReservationService service;

    public ReservationController(JpaReservationService service) {
        this.service = service;
    }
    @PostMapping("/book")
    @MakeReservation
    public ResponseEntity<ConfirmationDTO> makeReservation(@RequestBody ReservationRequestDTO request) {
        ConfirmationDTO outcome = service.makeReservation(request);
        return ResponseEntity.ok(outcome);
    }
    @PostMapping("/cancel")
    @CancelReservation
    public ResponseEntity<String> cancelReservation(@RequestBody ReservationCancellationDTO cancellationRequest) {
        return service.cancelReservation(cancellationRequest);
    }
}
