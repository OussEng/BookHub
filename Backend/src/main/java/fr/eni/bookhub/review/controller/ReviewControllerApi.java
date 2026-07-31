package fr.eni.bookhub.review.controller;

import fr.eni.bookhub.review.dto.request.ReviewRequest;
import fr.eni.bookhub.review.dto.response.ReviewResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * OpenAPI documentation interface for {@link ReviewController}.
 *
 * @see ReviewController
 */
@Tag(
        name = "Reviews",
        description = "Reader reviews on books. A review requires having actually returned a loan for that book, and each user may leave one review per book."
)
@SecurityRequirement(name = "bearerAuth")
public interface ReviewControllerApi {

    @Operation(
            summary = "List reviews for a book",
            description = "Returns the paginated, non-moderated reviews of a book. A moderated review's `comment` is returned as null.",
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
                                                          "userId": 2,
                                                          "username": "mehdi",
                                                          "bookId": 1,
                                                          "rating": 4,
                                                          "comment": "Excellent livre",
                                                          "moderated": false,
                                                          "createdAt": "2026-07-20T10:00:00",
                                                          "updatedAt": "2026-07-20T10:00:00"
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
                            responseCode = "404",
                            description = "Book not found",
                            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"error\": \"Ouvrage introuvable\"}"))
                    )
            }
    )
    ResponseEntity<Page<ReviewResponse>> getReviewsByBook(@PathVariable Long bookId, Pageable pageable);

    @Operation(
            summary = "Create a review",
            description = "Leaves a review on a book. Requires having returned at least one loan for that book, and only one review per user per book is allowed.",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Review successfully created",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                      "id": 1,
                                                      "userId": 2,
                                                      "username": "mehdi",
                                                      "bookId": 1,
                                                      "rating": 4,
                                                      "comment": "Excellent livre",
                                                      "moderated": false,
                                                      "createdAt": "2026-07-30T10:00:00",
                                                      "updatedAt": "2026-07-30T10:00:00"
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Validation failed",
                            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"error\": \"La note doit être comprise entre 0 et 5\"}"))
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "The user has not returned a loan for this book",
                            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"error\": \"Vous ne pouvez pas noter cette ouvrage car vous ne l'avez pas encore emprunté\"}"))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Book not found",
                            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"error\": \"Ouvrage introuvable\"}"))
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "The user already reviewed this book",
                            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"error\": \"Vous avez déjà laissé un avis pour ce livre, vous pouvez le modifier plutôt que d'en créer un nouveau\"}"))
                    )
            }
    )
    ResponseEntity<ReviewResponse> createReview(@PathVariable Long bookId, @Valid ReviewRequest request);

    @Operation(
            summary = "Update a review",
            description = "Updates the authenticated user's own review. A moderated review can no longer be edited.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Review successfully updated",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                      "id": 1,
                                                      "userId": 2,
                                                      "username": "mehdi",
                                                      "bookId": 1,
                                                      "rating": 5,
                                                      "comment": "Relecture, encore meilleur",
                                                      "moderated": false,
                                                      "createdAt": "2026-07-20T10:00:00",
                                                      "updatedAt": "2026-07-30T10:00:00"
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Validation failed",
                            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"error\": \"La note doit être comprise entre 0 et 5\"}"))
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "The review belongs to another user",
                            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"error\": \"Vous ne pouvez pas modifier l'avis d'un autre utilisateur\"}"))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Book not found, review not found, or review does not belong to this book",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(name = "Book not found", value = "{\"error\": \"Ouvrage introuvable\"}"),
                                            @ExampleObject(name = "Review not found", value = "{\"error\": \"Avis introuvable\"}")
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Review has been moderated and can no longer be edited",
                            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"error\": \"Cet avis a été modéré et ne peut plus être modifié\"}"))
                    )
            }
    )
    ResponseEntity<ReviewResponse> updateReview(@PathVariable Long bookId, @PathVariable Long reviewId, @Valid ReviewRequest request);

    @Operation(
            summary = "Delete a review",
            description = "Deletes the authenticated user's own review. A moderated review can no longer be deleted by its author.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Review successfully deleted", content = @Content),
                    @ApiResponse(
                            responseCode = "403",
                            description = "The review belongs to another user",
                            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"error\": \"Vous ne pouvez pas supprimer l'avis d'un autre utilisateur\"}"))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Book not found, review not found, or review does not belong to this book",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(name = "Book not found", value = "{\"error\": \"Ouvrage introuvable\"}"),
                                            @ExampleObject(name = "Review not found", value = "{\"error\": \"Avis introuvable\"}")
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Review has been moderated and can no longer be deleted",
                            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"error\": \"Cet avis a été modéré et ne peut plus être supprimé\"}"))
                    )
            }
    )
    ResponseEntity<Void> deleteReview(@PathVariable Long bookId, @PathVariable Long reviewId);
}