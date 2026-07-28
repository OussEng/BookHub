package fr.eni.bookhub.bookcopy.service;


import fr.eni.bookhub.book.entity.Book;
import fr.eni.bookhub.book.repository.BookRepository;
import fr.eni.bookhub.bookcopy.dto.request.CreateBookCopyRequest;
import fr.eni.bookhub.bookcopy.dto.response.BookCopyResponse;
import fr.eni.bookhub.bookcopy.entity.BookCopy;
import fr.eni.bookhub.bookcopy.entity.BookStatus;
import fr.eni.bookhub.bookcopy.entity.Condition;
import fr.eni.bookhub.bookcopy.repository.BookCopyRepository;
import org.springframework.stereotype.Service;


@Service
public class BookCopyService {

    private final BookCopyRepository bookCopyRepository;
    private final BookRepository bookRepository;

    public BookCopyService(BookCopyRepository bookCopyRepository, BookRepository bookRepository) {
        this.bookCopyRepository = bookCopyRepository;
        this.bookRepository = bookRepository;
    }

    private boolean goodCondition(BookCopy copy) {
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

        BookCopy copy = BookCopy.builder()
                .serialNumber(request.getSerialNumber())
                .book(book)
                .bookStatus(BookStatus.AVAILABLE)
                .condition(Condition.NEW)
                .build();

        BookCopy savedCopy = bookCopyRepository.save(copy);

        return BookCopyResponse.fromBookCopyEntity(savedCopy);
    }
}
