package fr.eni.bookhub.loan.controller;

import fr.eni.bookhub.loan.dto.response.LoanDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * OpenAPI documentation interface for {@link LoanController}.
 *
 * @see LoanController
 */
@Tag(
        name = "Loans",
        description = "Loan management: borrowing a book copy, returning it, and consulting loan history."
)
@SecurityRequirement(name = "bearerAuth")
public interface LoanControllerApi {

    @Operation(
            summary = "List all loans",
            description = "Returns every loan in the system, active and past. Restricted to ADMIN and LIBRARIAN roles.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Loans successfully retrieved",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = """
                                                    [
                                                      {
                                                        "id": 1,
                                                        "bookCopyId": 3,
                                                        "userId": 2,
                                                        "loanDate": "2026-07-01",
                                                        "dueDate": "2026-07-15",
                                                        "returnDate": null,
                                                        "status": "ACTIVE",
                                                        "bookTitle": "1984",
                                                        "loanerFirstName": "Mehdi",
                                                        "loanerLastName": "Rochereau"
                                                      }
                                                    ]
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Access denied — current user is not ADMIN or LIBRARIAN",
                            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"error\": \"Access Denied\"}"))
                    )
            }
    )
    ResponseEntity<List<LoanDTO>> findAll();

    @Operation(
            summary = "List my loans",
            description = "Returns the loan history of the currently authenticated user, active and past.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Loans successfully retrieved",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = """
                                                    [
                                                      {
                                                        "id": 1,
                                                        "bookCopyId": 3,
                                                        "userId": 2,
                                                        "loanDate": "2026-07-01",
                                                        "dueDate": "2026-07-15",
                                                        "returnDate": null,
                                                        "status": "ACTIVE",
                                                        "bookTitle": "1984",
                                                        "loanerFirstName": "Mehdi",
                                                        "loanerLastName": "Rochereau"
                                                      }
                                                    ]
                                                    """
                                    )
                            )
                    )
            }
    )
    ResponseEntity<List<LoanDTO>> findAllMy();

    @Operation(
            summary = "Borrow a book",
            description = """
                    Borrows a copy of the given book for the authenticated user. `id` is the **book** identifier, not a copy identifier — the API picks an available copy in good condition automatically, prioritizing one already set aside via a ready reservation.
                    
                    A user cannot have more than 3 active loans at once.
                    
                    Note: if `id` does not match any existing book, this returns 409 "Aucun exemplaire disponible pour ce livre" rather than a 404, since the lookup for available copies simply returns empty.
                    """,
            responses = {
                    @ApiResponse(responseCode = "201", description = "Loan successfully created", content = @Content),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Loan not possible",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(name = "Loan limit reached", value = "{\"error\": \"Limite maximum de 3 livres atteinte\"}"),
                                            @ExampleObject(name = "No copy available", value = "{\"error\": \"Aucun exemplaire disponible pour ce livre\"}"),
                                            @ExampleObject(name = "No copy in good condition", value = "{\"error\": \"Aucun exemplaire disponible dans un état correct\"}"),
                                            @ExampleObject(name = "Reservation pickup deadline passed", value = "{\"error\": \"Le délai de retrait de cette réservation est dépassé\"}"),
                                            @ExampleObject(name = "Reserved copy no longer loanable", value = "{\"error\": \"L'exemplaire réservé n'est plus dans un état empruntable\"}")
                                    }
                            )
                    )
            }
    )
    ResponseEntity<Void> createLoan(@PathVariable Long id);

    @Operation(
            summary = "Return a loan",
            description = "Marks a loan as returned and promotes the next pending reservation on that book, if any. Restricted to ADMIN and LIBRARIAN roles. Despite the Java method signature declaring a LoanDTO body, the actual response is 204 with no content.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Loan successfully returned, no response body", content = @Content),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Access denied — current user is not ADMIN or LIBRARIAN",
                            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"error\": \"Access Denied\"}"))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Loan not found",
                            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"error\": \"Emprunt introuvable\"}"))
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Loan already returned",
                            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"error\": \"Retour déjà enregistré\"}"))
                    )
            }
    )
    ResponseEntity<LoanDTO> returnLoans(@PathVariable Long id);
}