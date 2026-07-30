package fr.eni.bookhub.genre.controller;

import fr.eni.bookhub.genre.dto.response.GenreResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * OpenAPI documentation interface for {@link GenreController}.
 *
 * <p>Declares all Swagger/OpenAPI annotations for genre endpoints,
 * keeping {@link GenreController} clean and focused on business logic.</p>
 *
 * <p>All endpoints require a valid JWT Bearer token.</p>
 *
 * @see GenreController
 */
@Tag(
        name = "Genres",
        description = "Genre lookup endpoints. Used to populate genre filters and book creation/update forms."
)
@SecurityRequirement(name = "bearerAuth")
public interface GenreControllerApi {

    @Operation(
            summary = "List all genres",
            description = "Returns the full list of genres available in the catalog, ordered as stored. Used to populate genre selectors on the client side.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Genres successfully retrieved",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = """
                                                    [
                                                      {
                                                        "id": 1,
                                                        "label": "Science-Fiction"
                                                      },
                                                      {
                                                        "id": 2,
                                                        "label": "Fantasy"
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
    ResponseEntity<List<GenreResponse>> getGenres();
}