package fr.eni.bookhub.bookcopy.controller;

import fr.eni.bookhub.bookcopy.dto.request.CreateBookCopyRequest;
import fr.eni.bookhub.bookcopy.dto.response.BookCopyResponse;
import fr.eni.bookhub.bookcopy.service.BookCopyService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/book-copies")
public class BookCopyController {

    private final BookCopyService bookCopyService;

    public BookCopyController(BookCopyService bookCopyService) {
        this.bookCopyService = bookCopyService;
    }

    @PostMapping
    public BookCopyResponse createBookCopy(@RequestBody CreateBookCopyRequest request) {
        return bookCopyService.createBookCopy(request);
    }

    @GetMapping("/by-book/{bookId}")
    public List<BookCopyResponse> getCopiesByBookId(@PathVariable Long bookId) {
        return bookCopyService.getCopiesByBookId(bookId);
    }
}