package fr.eni.bookhub.reservation.dto.response;


import fr.eni.bookhub.reservation.entity.Reservation;
import fr.eni.bookhub.reservation.entity.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ReservationResponse {

    private final Long id;
    private final Long bookId;
    private final String bookTitle;
    private final LocalDateTime reservesDate;
    private final long rank;
    private final ReservationStatus status;
    private final LocalDateTime pickupDeadline;
    private final boolean canBeCancelled;

    // Le rang n'est pas porté par l'entité : il vient d'une requête à part
    public static ReservationResponse fromEntity(Reservation reservation, long rank) {
        return new ReservationResponse(
                reservation.getId(),
                reservation.getBook().getId(),
                reservation.getBook().getTitle(),
                reservation.getReservesDate(),
                rank,
                reservation.getStatus(),
                reservation.getPickupDeadline(),
                ReservationStatus.ACTIFS.contains(reservation.getStatus())
        );
    }
}