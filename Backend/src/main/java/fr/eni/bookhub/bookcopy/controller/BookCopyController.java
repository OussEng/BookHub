package fr.eni.bookhub.bookcopy.controller;

import fr.eni.bookhub.bookcopy.dto.request.CreateBookCopyRequest;
import fr.eni.bookhub.bookcopy.dto.response.BookCopyResponse;
import fr.eni.bookhub.bookcopy.service.BookCopyService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/book-copies")
public class BookCopyController {

    private final BookCopyService bookCopyService;

    public BookCopyController(BookCopyService bookCopyService) {
        this.bookCopyService = bookCopyService;
    }

    @PostMapping("/create")
    public ResponseEntity<BookCopyResponse> createBookCopy(@Valid @RequestBody CreateBookCopyRequest request) {
        BookCopyResponse bookCopy = bookCopyService.createBookCopy(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(bookCopy);
    }

    @GetMapping("/by-book/{bookId}")
    public ResponseEntity<List<BookCopyResponse>> getCopiesByBookId(@PathVariable Long bookId) {
        return ResponseEntity.ok(bookCopyService.getCopiesByBookId(bookId));
    }
}