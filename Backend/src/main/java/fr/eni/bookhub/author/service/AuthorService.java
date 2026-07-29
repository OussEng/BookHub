package fr.eni.bookhub.author.service;

import fr.eni.bookhub.author.dao.IAuthorDao;
import fr.eni.bookhub.author.dto.response.AuthorResponse;
import fr.eni.bookhub.author.entity.Author;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class AuthorService {

    private final IAuthorDao authorDao;

    public List<AuthorResponse> getAllAuthors(){
        List<Author> authors = this.authorDao.findAllAuthors();
        return authors.stream().map(AuthorResponse::fromEntity).toList();
    }


}
