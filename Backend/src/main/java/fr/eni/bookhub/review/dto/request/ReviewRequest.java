package fr.eni.bookhub.review.dto.request;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ReviewRequest {

    @NotNull(message = "La note est requise")
    @Min(value = 0, message = "La note doit être comprise entre 0 et 5")
    @Max(value = 5, message = "La note doit être comprise entre 0 et 5")
    private Integer rating;

    @Nullable
    @Size(max = 1000, message = "Le commentaire ne doit pas dépasser 1000 caractères")
    private String comment;
}
