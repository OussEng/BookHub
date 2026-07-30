package fr.eni.bookhub.book.controller;

import fr.eni.bookhub.book.dto.request.create.BookCreateRequest;
import fr.eni.bookhub.book.dto.request.update.BookUpdateRequest;
import fr.eni.bookhub.book.dto.response.BookResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

/**
 * OpenAPI documentation interface for {@link BookController}.
 *
 * <p><strong>Known gap:</strong> several not-found scenarios in the underlying
 * service ({@code getBookById}, {@code createBook}, {@code updateBook},
 * {@code deleteBook}) throw exceptions that {@code GlobalExceptionHandler}
 * does not intercept, resulting in a generic 500 rather than a 404. This is
 * documented as-is below and should be fixed at the service/handler level.</p>
 *
 * @see BookController
 */
@Tag(
        name = "Books",
        description = "Book catalog management: search, details, creation, update and deletion, including cover image upload."
)
@SecurityRequirement(name = "bearerAuth")
public interface BookControllerApi {

    @Operation(
            summary = "Search and list books",
            description = """
                    Returns a paginated, filterable list of books. Filtering by `search` matches on title (implementation-specific), `genreId` filters by genre.
                    
                    **Known gap:** an invalid `sortBy` property name is not validated and results in a 500 Internal Server Error rather than a 400.
                    """,
            parameters = {
                    @Parameter(name = "search", description = "Optional free-text search on book title"),
                    @Parameter(name = "genreId", description = "Optional genre filter"),
                    @Parameter(name = "sortBy", description = "Property to sort by. Default: id"),
                    @Parameter(name = "sortDir", description = "Sort direction, `asc` or `desc`. Default: desc"),
                    @Parameter(name = "page", description = "Page number, zero-based. Default: 0"),
                    @Parameter(name = "size", description = "Page size. Default: 20")
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Books successfully retrieved",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                      "content": [
                                                        {
                                                          "id": 1,
                                                          "title": "1984",
                                                          "img": "https://api.example.com/uploads/books/abc.jpg",
                                                          "description": "A dystopian novel...",
                                                          "isbn": "9780451524935",
                                                          "genres": ["Science-Fiction"],
                                                          "publishDate": "1949-06-08",
                                                          "author": ["George Orwell"],
                                                          "available": true
                                                        }
                                                      ],
                                                      "totalElements": 1,
                                                      "totalPages": 1,
                                                      "number": 0,
                                                      "size": 20
                                                    }
                                                    """
                                    )
                            )
                    )
            }
    )
    ResponseEntity<Page<BookResponse>> getAllBooks(
            String search, Long genreId, String sortBy, String sortDir, int page, int size);

    @Operation(
            summary = "Get a book by ID",
            description = "Retrieves detailed information about a specific book.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Book successfully retrieved",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = BookResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Book not found. Known gap: the service throws an unhandled RuntimeException(\"Livre non trouvé\") for this case, so a missing book currently returns a generic 500 rather than a 404.",
                            content = @Content
                    )
            }
    )
    ResponseEntity<BookResponse> getBookById(@PathVariable Long id);

    @Operation(
            summary = "Create a book",
            description = """
                    Creates a new book with at least one author and one genre, and an optional cover image (JPEG/PNG/WebP, 5MB max).
                    
                    **Known gap:** if one or more `authorIds`/`genreIds` do not exist, or if the image is invalid, the service throws unhandled exceptions resulting in a generic 500 rather than a clean 404/400.
                    """,
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Book successfully created",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Validation failed on the request body",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(value = "{\"error\": \"Le titre est obligatoire\"}")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Unknown author/genre ID, or invalid/oversized image. Known gap: not translated into a 4xx response.",
                            content = @Content
                    )
            }
    )
    ResponseEntity<BookResponse> createBook(@Valid BookCreateRequest bookDto, MultipartFile image);

    @Operation(
            summary = "Update a book",
            description = """
                    Updates an existing book's title, ISBN, description, publish date, authors, genres and optionally its cover image.
                    
                    **Known gap 1:** if `id` does not match an existing book, the service throws an unhandled EntityNotFoundException, resulting in a generic 500 rather than a 404.
                    
                    **Known gap 2:** unlike creation, unknown `authorIds`/`genreIds` are silently ignored here (no existence check), rather than raising any error.
                    """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Book successfully updated",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Validation failed on the request body",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(value = "{\"error\": \"Le titre est obligatoire\"}")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Book not found, or invalid/oversized image. Known gap: not translated into a 4xx response.",
                            content = @Content
                    )
            }
    )
    ResponseEntity<BookResponse> updateBook(@PathVariable Long id, @Valid BookUpdateRequest request, MultipartFile image);

    @Operation(
            summary = "Delete a book",
            description = "Permanently deletes a book. Known gap: if `id` does not match an existing book, the service throws an unhandled EntityNotFoundException, resulting in a generic 500 rather than a 404.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Book successfully deleted", content = @Content),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Book not found. Known gap: not translated into a 404.",
                            content = @Content
                    )
            }
    )
    ResponseEntity<Void> deleteBook(@PathVariable Long id);
}