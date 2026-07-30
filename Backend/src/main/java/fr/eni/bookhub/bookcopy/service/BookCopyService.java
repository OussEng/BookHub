package fr.eni.bookhub.bookcopy.service;


import fr.eni.bookhub.book.dao.IBookDao;
import fr.eni.bookhub.book.dto.response.BookResponse;
import fr.eni.bookhub.book.entity.Book;
import fr.eni.bookhub.book.repository.BookRepository;
import fr.eni.bookhub.bookcopy.dao.IBookCopyDao;
import fr.eni.bookhub.bookcopy.dto.request.CreateBookCopyRequest;
import fr.eni.bookhub.bookcopy.dto.response.BookCopyResponse;
import fr.eni.bookhub.bookcopy.entity.BookCopy;
import fr.eni.bookhub.bookcopy.entity.BookStatus;
import fr.eni.bookhub.bookcopy.entity.Condition;
import fr.eni.bookhub.bookcopy.repository.BookCopyRepository;
import fr.eni.bookhub.exception.custom.ConflictException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import org.springframework.stereotype.Service;



@Service
public class BookCopyService {

    private final IBookCopyDao bookCopyRepository;
    private final IBookDao bookRepository;

    public BookCopyService(IBookCopyDao bookCopyRepository, IBookDao bookRepository) {
        this.bookCopyRepository = bookCopyRepository;
        this.bookRepository = bookRepository;
    }

    public boolean goodCondition(BookCopy copy) {
        return copy.getCondition() == Condition.NEW
                || copy.getCondition() == Condition.GOOD;
    }

    public boolean canBeLoaned(BookCopy copy) {
        return copy.getBookStatus() == BookStatus.AVAILABLE && goodCondition(copy);
    }

    public boolean canBeReserved(BookCopy copy) {
        return copy.getBookStatus() == BookStatus.LOANED && goodCondition(copy);
    }


    public BookCopyResponse createBookCopy(CreateBookCopyRequest request) {
        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new RuntimeException("Livre non trouvé"));



        if (bookCopyRepository.findBySerialNumber(request.getSerialNumber()).isPresent()) {
            throw new ConflictException("Cet exemplaire existe déjà.");
        }

        BookCopy copy = BookCopy.builder()
                .serialNumber(request.getSerialNumber())
                .book(book)
                .bookStatus(BookStatus.AVAILABLE)
                .condition(request.getCondition())
                .build();

        BookCopy savedCopy = bookCopyRepository.save(copy);

        return BookCopyResponse.fromBookCopyEntity(savedCopy);
    }

    public Page<BookCopyResponse> getCopiesByBookId(
            Long bookId,
            String serialNumber,
            Condition condition,
            Pageable pageable
    ) {
        return bookCopyRepository
                .findCopiesByBookIdWithFilters(bookId, serialNumber, condition, pageable)
                .map(BookCopyResponse::fromBookCopyEntity);
    }
}
