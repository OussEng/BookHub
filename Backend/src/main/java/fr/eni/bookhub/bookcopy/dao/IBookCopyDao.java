package fr.eni.bookhub.bookcopy.dao;


import fr.eni.bookhub.bookcopy.entity.BookCopy;
import fr.eni.bookhub.bookcopy.entity.BookStatus;
import fr.eni.bookhub.bookcopy.entity.Condition;
import fr.eni.bookhub.reservation.entity.ReservationStatus;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface IBookCopyDao {

    Optional<BookCopy> findById(Long id);
    List<BookCopy> findAll();
    boolean existsByBook_IdAndBookStatusAndConditionIn(Long bookId, BookStatus bookStatus, Collection<Condition> conditions);
    List<BookCopy> findByBookIdAndBookStatus(Long bookId, BookStatus bookStatus);
    BookCopy save(BookCopy bookCopy);
    Optional<BookCopy> findByIdForUpdate(Long id);
    BookCopy saveAndFlush(BookCopy bookCopy);

}
