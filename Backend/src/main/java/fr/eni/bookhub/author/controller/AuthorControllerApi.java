package fr.eni.bookhub.author.controller;

import fr.eni.bookhub.author.dto.response.AuthorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * OpenAPI documentation interface for {@link AuthorController}.
 *
 * <p>Declares all Swagger/OpenAPI annotations for author endpoints,
 * keeping {@link AuthorController} clean and focused on business logic.</p>
 *
 * <p>All endpoints require a valid JWT Bearer token.</p>
 *
 * @see AuthorController
 */
@Tag(
        name = "Authors",
        description = "Author lookup endpoints. Used to populate author filters and book creation/update forms."
)
@SecurityRequirement(name = "bearerAuth")
public interface AuthorControllerApi {

    @Operation(
            summary = "List all authors",
            description = "Returns the full list of authors registered in the catalog. Used to populate author selectors on the client side.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Authors successfully retrieved",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = """
                                                    [
                                                      {
                                                        "id": 1,
                                                        "firstname": "George",
                                                        "lastname": "Orwell",
                                                        "penName": null
                                                      },
                                                      {
                                                        "id": 2,
                                                        "firstname": "Samuel",
                                                        "lastname": "Clemens",
                                                        "penName": "Mark Twain"
                                                      }
                                                    ]
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Missing or invalid JWT token",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                      "error": "Invalid email or password"
                                                    }
                                                    """
                                    )
                            )
                    )
            }
    )
    ResponseEntity<List<AuthorResponse>> getAllAuthors();
}