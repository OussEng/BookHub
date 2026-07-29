package fr.eni.bookhub.author.dao;

import fr.eni.bookhub.author.entity.Author;
import fr.eni.bookhub.author.repository.AuthorRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
@AllArgsConstructor
public class AuthorDaoImpl implements IAuthorDao{

    private final AuthorRepository authorRepository;

    @Override
    public List<Author> findAllAuthors() {
        return authorRepository.findAll();
    }

    @Override
    public List<Author> findAllById(List<Long> authorIds) {
        return authorRepository.findAllById(authorIds);
    }
}
