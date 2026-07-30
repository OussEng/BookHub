package fr.eni.bookhub.book.dao;

import aj.org.objectweb.asm.commons.Remapper;
import fr.eni.bookhub.book.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface IBookDao {
    Optional<Book> findById(Long id);
    Page<Book> findAll(Pageable pageable);
    Book save(Book book);
    Page<Book> searchBooks(String search, Long genreId, Pageable pageable);
    void deleteById(Long id);
    List<Book> findAll();
    boolean existsById(Long id);
}
