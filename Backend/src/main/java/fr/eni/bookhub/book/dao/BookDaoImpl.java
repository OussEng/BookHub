package fr.eni.bookhub.book.dao;

import fr.eni.bookhub.book.entity.Book;
import fr.eni.bookhub.book.repository.BookRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@AllArgsConstructor
public class BookDaoImpl implements IBookDao {

    private final BookRepository bookRepository;

    @Override
    public Optional<Book> findById(Long id) {
        return bookRepository.findById(id);
    }

    @Override
    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    @Override
    public boolean existsById(Long id) {
        return bookRepository.existsById(id);
    }
}
