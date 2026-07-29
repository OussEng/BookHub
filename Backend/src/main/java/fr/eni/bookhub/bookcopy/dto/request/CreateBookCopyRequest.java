package fr.eni.bookhub.bookcopy.dto.request;

import fr.eni.bookhub.bookcopy.entity.Condition;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateBookCopyRequest {

    @NotNull(message = "Le numéro de série est obligatoire")
    @Size(min = 6, max = 20, message = "Le numéro de série doit contenir entre 6 et 20 caractères")
    private String serialNumber;

    @NotNull(message = "L'état du livre est obligatoire")
    private Condition condition;

    @NotNull(message = "L'ID du livre est obligatoire")
    private Long bookId;
}
