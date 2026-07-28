package fr.eni.bookhub.bookcopy.dao;


import fr.eni.bookhub.bookcopy.entity.BookCopy;
import fr.eni.bookhub.bookcopy.entity.BookStatus;
import fr.eni.bookhub.bookcopy.repository.BookCopyRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@AllArgsConstructor
public class BookCopyDaoImpl implements IBookCopyDao {

    private final BookCopyRepository bookCopyRepository;

    @Override
    public Optional<BookCopy> findById(Long id) {
        return bookCopyRepository.findById(id);
    }

    @Override
    public List<BookCopy> findAll() {
        return bookCopyRepository.findAll();
    }

    @Override public boolean existsByBook_IdAndBookStatus(Long bookId, BookStatus bookStatus) {
        return bookCopyRepository.existsByBook_IdAndBookStatus(bookId, bookStatus);
    }
}
