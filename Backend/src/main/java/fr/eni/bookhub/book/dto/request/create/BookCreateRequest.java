package fr.eni.bookhub.book.dto.request.create;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder


public class BookCreateRequest {


    @NotBlank(message = "Le titre est obligatoire")
    @Size(max = 255, message = "Le titre ne doit pas dépasser 255 caractères")
    private String title;

    @NotEmpty(message = "Le livre doit avoir au moins un auteur")
    private List<Long> authorIds;

    @NotEmpty(message = "Le livre doit avoir au moins un genre")
    private List<Long> genreIds;

    @NotBlank(message = "L'ISBN est obligatoire")
    private String isbn;

    @NotBlank(message = "La description est obligatoire")
    @Size(max = 2000, message = "La description ne doit pas dépasser 2000 caractères")
    private String description;

    @NotNull(message = "La date de publication est obligatoire")
    @PastOrPresent(message = "La date de publication ne peut pas être dans le futur")
    private LocalDate publishDate;

}
