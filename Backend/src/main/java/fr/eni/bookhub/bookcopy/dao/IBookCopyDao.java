package fr.eni.bookhub.bookcopy.dao;


import fr.eni.bookhub.bookcopy.entity.BookCopy;
import fr.eni.bookhub.bookcopy.entity.BookStatus;
import fr.eni.bookhub.bookcopy.entity.Condition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface IBookCopyDao {

    Optional<BookCopy> findById(Long id);
    List<BookCopy> findAll();
    boolean existsByBook_IdAndBookStatus(Long bookId, BookStatus bookStatus);
    List<BookCopy> findByBookIdAndBookStatus(Long bookId, BookStatus bookStatus);
    List<BookCopy> findByBookId(Long bookId);
    Optional <BookCopy> findBySerialNumber(String serialNumber);
    BookCopy save(BookCopy bookCopy);

    Page<BookCopy> findCopiesByBookIdWithFilters(Long bookId, String serialNumber, Condition condition, Pageable pageable);
}
