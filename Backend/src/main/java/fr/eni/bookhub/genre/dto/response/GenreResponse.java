package fr.eni.bookhub.genre.dto.response;

import fr.eni.bookhub.genre.entity.Genre;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class GenreResponse {

    private Long id;
    private  String label;


    public static GenreResponse fromEntity(Genre genre){
        return new GenreResponse(
                genre.getId(),
                genre.getLabel()
        );
    }

}
