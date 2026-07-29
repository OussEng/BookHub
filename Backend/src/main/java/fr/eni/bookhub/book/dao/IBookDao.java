package fr.eni.bookhub.book.dao;

import fr.eni.bookhub.book.entity.Book;

import java.util.List;
import java.util.Optional;

public interface IBookDao {
    Optional<Book> findById(Long id);
    List<Book> findAll();
    boolean existsById(Long id);
}
