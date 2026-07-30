package fr.eni.bookhub.reservation.controller;

import fr.eni.bookhub.reservation.dto.response.ReservationResponse;
import fr.eni.bookhub.reservation.service.ReservationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@PreAuthorize("isAuthenticated()")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping("/{bookId}")
    public ResponseEntity<ReservationResponse> createReservation (
            @PathVariable Long bookId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reservationService.createReservation(bookId));
    }

    @DeleteMapping("/{reservationId}")
    public ResponseEntity<Void> cancelReservation (
            @PathVariable Long reservationId) {
        reservationService.cancelReservation(reservationId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my")
    public ResponseEntity<List<ReservationResponse>> getMyReservations () {
        return ResponseEntity.ok(reservationService.getMyReservations());
    }
}
