package fr.eni.bookhub.genre.dao;

import fr.eni.bookhub.genre.entity.Genre;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public interface IGenreDao {
    List<Genre> findAllGenres();

    List<Genre> findAllById(List<Long> genreIds);
}
