package fr.eni.bookhub.review.dto.request.update;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateReviewDTO {

    @NotNull
    private Long id;

    @NotNull
    private int rating;

    @Null
    private String comment;

}
