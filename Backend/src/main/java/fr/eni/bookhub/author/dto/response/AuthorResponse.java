package fr.eni.bookhub.author.dto.response;

import fr.eni.bookhub.author.entity.Author;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthorResponse {

    private final Long id;
    private final String firstname;
    private final String lastname;
    private final String penName;


    public static AuthorResponse fromEntity(Author author){
        return new AuthorResponse(
                author.getId(),
                author.getFirstname(),
                author.getLastname(),
                author.getPenName()
        );
    }

}
