package fr.eni.bookhub.bookcopy.controller;

import fr.eni.bookhub.bookcopy.dto.request.CreateBookCopyRequest;
import fr.eni.bookhub.bookcopy.dto.response.BookCopyResponse;
import fr.eni.bookhub.bookcopy.entity.Condition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * OpenAPI documentation interface for {@link BookCopyController}.
 *
 * @see BookCopyController
 */
@Tag(
        name = "Book Copies",
        description = "Physical copy management. A book can have several copies, each tracked independently by status and condition."
)
@SecurityRequirement(name = "bearerAuth")
public interface BookCopyControllerApi {

    @Operation(
            summary = "Create a book copy",
            description = """
                    Registers a new physical copy of a book, with a unique serial number and an initial condition. The copy starts with status `AVAILABLE`.
                    
                    Note: if `bookId` does not reference an existing book, the API currently returns a generic 500 error rather than 404 — this is a known gap on the service side.
                    """,
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Book copy successfully created",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                      "id": 1,
                                                      "serialNumber": "SN-000123",
                                                      "bookTitle": "1984",
                                                      "bookStatus": "AVAILABLE",
                                                      "condition": "NEW",
                                                      "canBeLoaned": true,
                                                      "canBeReserved": false
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Validation failed",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(value = "{\"error\": \"Le numéro de série doit contenir entre 6 et 20 caractères\"}")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "A book copy with this serial number already exists",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(value = "{\"error\": \"Cet exemplaire existe déjà.\"}")
                            )
                    )
            }
    )
    ResponseEntity<BookCopyResponse> createBookCopy(@Valid @RequestBody CreateBookCopyRequest request);

    @Operation(
            summary = "List copies of a book",
            description = """
                    Returns the paginated copies of a given book, optionally filtered by serial number and/or condition.
                    
                    Note: if `bookId` does not match any book, this returns an empty page rather than a 404 — no existence check is performed here.
                    """,
            parameters = {
                    @Parameter(name = "bookId", description = "Book identifier", required = true),
                    @Parameter(name = "serialNumber", description = "Optional filter, partial match on serial number"),
                    @Parameter(name = "condition", description = "Optional filter by condition (NEW, GOOD, WORN, DAMAGED)"),
                    @Parameter(name = "page", description = "Page number, zero-based"),
                    @Parameter(name = "size", description = "Page size")
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Copies successfully retrieved",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                      "content": [
                                                        {
                                                          "id": 1,
                                                          "serialNumber": "SN-000123",
                                                          "bookTitle": "1984",
                                                          "bookStatus": "AVAILABLE",
                                                          "condition": "NEW",
                                                          "canBeLoaned": true,
                                                          "canBeReserved": false
                                                        }
                                                      ],
                                                      "totalElements": 1,
                                                      "totalPages": 1,
                                                      "number": 0,
                                                      "size": 10
                                                    }
                                                    """
                                    )
                            )
                    )
            }
    )
    ResponseEntity<Page<BookCopyResponse>> getCopiesByBookId(
            @PathVariable Long bookId,
            @RequestParam(required = false) String serialNumber,
            @RequestParam(required = false) Condition condition,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size);
}