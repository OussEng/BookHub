package fr.eni.bookhub.bookcopy.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateBookCopyRequest {

    @NotNull
    @Size(max = 80)
    private String serialNumber;

    @NotNull
    private Long bookId;
}
