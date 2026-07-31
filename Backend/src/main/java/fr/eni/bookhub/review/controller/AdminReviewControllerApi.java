package fr.eni.bookhub.review.controller;

import fr.eni.bookhub.review.dto.response.AdminReviewResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * OpenAPI documentation interface for {@link AdminReviewController}.
 *
 * <p>All endpoints are restricted to ADMIN and LIBRARIAN roles.</p>
 *
 * @see AdminReviewController
 */
@Tag(
        name = "Review Moderation",
        description = "Administrative review moderation. Restricted to ADMIN and LIBRARIAN roles."
)
@SecurityRequirement(name = "bearerAuth")
public interface AdminReviewControllerApi {

    @Operation(
            summary = "List all reviews (admin)",
            description = "Returns every review in the system, including moderated ones, with reader and book details. Optional free-text search on book title or author.",
            parameters = {
                    @Parameter(name = "search", description = "Optional free-text search on book title or author")
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Reviews successfully retrieved",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                      "content": [
                                                        {
                                                          "id": 1,
                                                          "username": "mehdi",
                                                          "bookId": 1,
                                                          "bookTitle": "1984",
                                                          "bookAuthors": ["George Orwell"],
                                                          "rating": 4,
                                                          "comment": "Excellent livre",
                                                          "moderated": false,
                                                          "createdAt": "2026-07-20T10:00:00",
                                                          "updatedAt": "2026-07-20T10:00:00",
                                                          "moderatedAt": null
                                                        }
                                                      ],
                                                      "totalElements": 1,
                                                      "totalPages": 1
                                                    }
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
    ResponseEntity<Page<AdminReviewResponse>> getAllReviews(String search, Pageable pageable);

    @Operation(
            summary = "Moderate a review",
            description = "Marks a review as moderated, hiding its comment from public listings. Cannot be undone through this endpoint.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Review successfully moderated", content = @Content),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Access denied — current user is not ADMIN or LIBRARIAN",
                            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"error\": \"Access Denied\"}"))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Review not found",
                            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"error\": \"Avis introuvable\"}"))
                    )
            }
    )
    ResponseEntity<Void> moderateReview(@PathVariable Long reviewId);
}