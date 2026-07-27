package fr.eni.bookhub.reservation.dto.response;


import fr.eni.bookhub.reservation.entity.Reservation;
import fr.eni.bookhub.reservation.entity.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ReservationResponse {

    private Long id;

    // TODO ajouter le titre du livre quand l'entité Book sera disponible
    private Long bookId;

    private LocalDateTime reservesDate;

    private long rank;

    private ReservationStatus status;

    // Le rang n'est pas stocké : il est calculé par une requête à part
    public static ReservationResponse fromEntity(Reservation reservation, long rank) {
        return new ReservationResponse(
                reservation.getId(),
                reservation.getBookId(),
                reservation.getReservesDate(),
                rank,
                reservation.getStatus()

        );
    }

}
