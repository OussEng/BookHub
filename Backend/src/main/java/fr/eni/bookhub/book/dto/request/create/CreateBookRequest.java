package fr.eni.bookhub.book.dto.request.create;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder


public class CreateBookRequest {

    @NotNull
    private String title;
    private String img;
    private List<Long> authorsId;
    private List<Long> genresId;

}
