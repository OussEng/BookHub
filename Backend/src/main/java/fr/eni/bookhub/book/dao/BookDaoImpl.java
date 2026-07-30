package fr.eni.bookhub.book.dao;

import aj.org.objectweb.asm.commons.Remapper;
import fr.eni.bookhub.book.entity.Book;
import fr.eni.bookhub.book.repository.BookRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public Page<Book> findAll(Pageable pageable) {
        return bookRepository.findAll(pageable);
    }

    @Override
    public Book save(Book book) {
        return bookRepository.save(book);
    }

    @Override
    public Page<Book> searchBooks(String search, Long genreId, Pageable pageable) {
        return bookRepository.searchBooks(search, genreId,pageable);
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
