package fr.eni.bookhub.genre.dao;

import fr.eni.bookhub.genre.entity.Genre;
import fr.eni.bookhub.genre.repository.GenreRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@AllArgsConstructor
@Component
public class GenreDaoImpl implements IGenreDao {

    private final GenreRepository genreRepository;

    @Override
    public List<Genre> findAllGenres() {
        return genreRepository.findAll();
    }

    @Override
    public List<Genre> findAllById(List<Long> genreIds) {
        return genreRepository.findAllById(genreIds);
    }
}
