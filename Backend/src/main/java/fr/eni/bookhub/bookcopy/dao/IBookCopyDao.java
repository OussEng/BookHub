package fr.eni.bookhub.bookcopy.dao;


import fr.eni.bookhub.bookcopy.entity.BookCopy;

import java.util.List;
import java.util.Optional;

public interface IBookCopyDao {

    Optional<BookCopy> findById(Long id);
    List<BookCopy> findAll();

}
