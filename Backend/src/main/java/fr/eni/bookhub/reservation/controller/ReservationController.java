package fr.eni.bookhub.reservation.controller;

import fr.eni.bookhub.reservation.dto.request.ReservationRequest;
import fr.eni.bookhub.reservation.dto.response.ReservationResponse;
import fr.eni.bookhub.reservation.service.ReservationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
        return ResponseEntity.ok(reservationService.createReservation(bookId));
    }
}
