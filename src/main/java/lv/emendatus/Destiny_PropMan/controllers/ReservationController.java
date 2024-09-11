package lv.emendatus.Destiny_PropMan.controllers;

import lv.emendatus.Destiny_PropMan.annotation.reservation_controler.CancelReservation;
import lv.emendatus.Destiny_PropMan.annotation.reservation_controler.MakeReservation;
import lv.emendatus.Destiny_PropMan.domain.dto.reservation.ConfirmationDTO;
import lv.emendatus.Destiny_PropMan.domain.dto.reservation.ReservationCancellationDTO;
import lv.emendatus.Destiny_PropMan.domain.dto.reservation.ReservationRequestDTO;
import lv.emendatus.Destiny_PropMan.service.implementation.JpaReservationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/reservation")
public class ReservationController {
    public final JpaReservationService service;

    public ReservationController(JpaReservationService service) {
        this.service = service;
    }
    @PostMapping("/book")
    @PreAuthorize("hasAuthority('TENANT')")
    @MakeReservation
    public ResponseEntity<ConfirmationDTO> makeReservation(@RequestBody ReservationRequestDTO request, Principal principal) {
        ConfirmationDTO outcome = service.makeReservation(request, principal);
        return ResponseEntity.ok(outcome);
    }
    @PostMapping("/cancel")
    @PreAuthorize("hasAuthority('TENANT')")
    @CancelReservation
    public ResponseEntity<String> cancelReservation(@RequestBody ReservationCancellationDTO cancellationRequest, Principal principal) {
        return service.cancelReservation(cancellationRequest, principal);
    }
}
