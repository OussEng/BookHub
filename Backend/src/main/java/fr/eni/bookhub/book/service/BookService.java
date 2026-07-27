package fr.eni.bookhub.book.service;


import fr.eni.bookhub.book.dto.response.BookResponse;
import fr.eni.bookhub.book.entity.Book;
import fr.eni.bookhub.book.repository.BookRepository;
import fr.eni.bookhub.bookcopy.service.BookCopyService;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<BookResponse> getAllBooks() {
        List<Book> books = bookRepository.findAll();
        return books.stream()
                .map(book -> BookResponse.fromBookEntity(book))
                .toList();
    }

    public BookResponse getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Livre non trouvé"));
        return BookResponse.fromBookEntity(book);
    }


}
