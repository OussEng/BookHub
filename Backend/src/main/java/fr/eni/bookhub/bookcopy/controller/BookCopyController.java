package fr.eni.bookhub.bookcopy.controller;

import fr.eni.bookhub.bookcopy.dto.request.CreateBookCopyRequest;
import fr.eni.bookhub.bookcopy.dto.response.BookCopyResponse;
import fr.eni.bookhub.bookcopy.entity.Condition;
import fr.eni.bookhub.bookcopy.service.BookCopyService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@PreAuthorize("hasRole('LIBRARIAN')")
@RestController
@RequestMapping("/api/book-copies")
public class BookCopyController implements BookCopyControllerApi {

    private final BookCopyService bookCopyService;

    public BookCopyController(BookCopyService bookCopyService) {
        this.bookCopyService = bookCopyService;
    }

    @PostMapping("/create")
    public ResponseEntity<BookCopyResponse> createBookCopy(@Valid @RequestBody CreateBookCopyRequest request) {
        BookCopyResponse bookCopy = bookCopyService.createBookCopy(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(bookCopy);
    }

    @GetMapping("/book/{bookId}")
    public ResponseEntity<Page<BookCopyResponse>> getCopiesByBookId(
            @PathVariable Long bookId,
            @RequestParam(required = false) String serialNumber,
            @RequestParam(required = false) Condition condition,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        return ResponseEntity.ok(bookCopyService.getCopiesByBookId(bookId, serialNumber, condition, pageable));
    }
}