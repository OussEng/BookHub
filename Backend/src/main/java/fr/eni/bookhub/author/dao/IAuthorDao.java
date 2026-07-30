package fr.eni.bookhub.author.dao;

import fr.eni.bookhub.author.entity.Author;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public interface IAuthorDao {
    List<Author> findAllAuthors();
    List<Author> findAllById(List<Long> authorIds);
}
