package fr.eni.bookhub.reservation.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;

@Getter
public class ReservationRequest {

    @NotNull(message = "Book id is required")
    @Positive(message = "Book id must be positive")
    private Long bookId;

}
