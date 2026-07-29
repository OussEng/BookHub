package fr.eni.bookhub.genre.service;

import fr.eni.bookhub.genre.dao.IGenreDao;
import fr.eni.bookhub.genre.dto.response.GenreResponse;
import fr.eni.bookhub.genre.entity.Genre;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class GenreService {

    private final IGenreDao genreDao;


    public List<GenreResponse> getAllGenres(){
        List<Genre> genres = this.genreDao.findAllGenres();
        return genres.stream().map(GenreResponse::fromEntity).toList();
    }



}
