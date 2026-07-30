package fr.eni.bookhub.book.controller;

import fr.eni.bookhub.book.dto.request.create.BookCreateRequest;
import fr.eni.bookhub.book.dto.request.update.BookUpdateRequest;
import fr.eni.bookhub.book.dto.response.BookResponse;
import fr.eni.bookhub.book.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/all")
    public ResponseEntity<Page<BookResponse>> getAllBooks(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long genreId,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(bookService.getAllBooks(search, genreId, pageable));
    }


    @Operation(
            summary = "Get a book by its ID",
            description = "Retrieves detailed information about a specific book using its unique database identifier."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Book successfully retrieved",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BookResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Missing or invalid JWT token",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Book not found with the given ID",
                    content = @Content
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<BookResponse> getBookById(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.getBookById(id));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BookResponse> createBook(
            @RequestPart("book") @Valid BookCreateRequest bookDto,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        BookResponse created = bookService.createBook(bookDto, image);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }


    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BookResponse> updateBook(
            @PathVariable Long id,
            @RequestPart("book") @Valid BookUpdateRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        BookResponse updatedBook = bookService.updateBook(id, request, image);
        return ResponseEntity.ok(updatedBook);
    }


}
