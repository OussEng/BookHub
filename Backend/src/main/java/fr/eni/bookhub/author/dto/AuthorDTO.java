package fr.eni.bookhub.author.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class AuthorDTO {

    private String firstname;
    private String lastname;
    private String pen_name;

}
