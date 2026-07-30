package fr.eni.bookhub.bookcopy.dao;


import fr.eni.bookhub.bookcopy.entity.BookCopy;
import fr.eni.bookhub.bookcopy.entity.BookStatus;
import fr.eni.bookhub.bookcopy.entity.Condition;
import fr.eni.bookhub.bookcopy.repository.BookCopyRepository;
import fr.eni.bookhub.reservation.entity.ReservationStatus;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;
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

    @Override
    public boolean existsByBook_IdAndBookStatusAndConditionIn(Long bookId, BookStatus bookStatus, Collection<Condition> conditions) {
        return bookCopyRepository.existsByBook_IdAndBookStatusAndConditionIn(bookId, bookStatus, conditions);
    }

    @Override
    public List<BookCopy> findByBookIdAndBookStatus(Long bookId, BookStatus bookStatus) {
        return bookCopyRepository.findByBookIdAndBookStatus(bookId, bookStatus);
    }

    @Override
    public BookCopy save(BookCopy bookCopy) {
        return bookCopyRepository.save(bookCopy);
    }

    @Override
    public Optional<BookCopy> findByIdForUpdate(Long id) {
        return bookCopyRepository.findByIdForUpdate(id);
    }

    @Override
    public BookCopy saveAndFlush(BookCopy bookCopy) {
        return bookCopyRepository.saveAndFlush(bookCopy);
    }
}
