package fr.eni.bookhub.bookcopy.dao;


import fr.eni.bookhub.bookcopy.entity.BookCopy;
import fr.eni.bookhub.bookcopy.entity.BookStatus;
import fr.eni.bookhub.bookcopy.entity.Condition;
import fr.eni.bookhub.bookcopy.repository.BookCopyRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Override
    public List<BookCopy> findByBookIdAndBookStatus(Long bookId, BookStatus bookStatus){
        return bookCopyRepository.findByBookIdAndBookStatus(bookId, bookStatus);
    }

    @Override
    public List<BookCopy> findByBookId(Long bookId) {
        return bookCopyRepository.findByBookId(bookId);
    }

    @Override
    public Optional<BookCopy> findBySerialNumber(String serialNumber) {
        return bookCopyRepository.findBySerialNumber(serialNumber);
    }

    @Override
    public BookCopy save(BookCopy bookCopy) {
        return bookCopyRepository.save(bookCopy);
    }

    @Override
    public Page<BookCopy> findCopiesByBookIdWithFilters(Long bookId, String serialNumber, Condition condition, Pageable pageable) {
        return bookCopyRepository.findCopiesByBookIdWithFilters(bookId,serialNumber,condition,pageable);
    }


}
