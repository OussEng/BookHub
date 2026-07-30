package fr.eni.bookhub.reservation.dto.response;

import fr.eni.bookhub.reservation.entity.BookAction;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class BookActionResponse {
    private final BookAction action;
    private final String reason;                 // null sauf NONE
    private final Long reservationId;            // renseigné pour CANCEL et PICKUP
    private final long rank;                     // renseigné pour CANCEL
    private final LocalDateTime pickupDeadline;  // renseigné pour PICKUP
}