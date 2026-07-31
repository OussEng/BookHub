package fr.eni.bookhub.review.controller;

import fr.eni.bookhub.review.dto.request.ReviewRequest;
import fr.eni.bookhub.review.dto.response.ReviewResponse;
import fr.eni.bookhub.review.service.ReviewService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/books/{bookId}/reviews", produces = "application/json")
@AllArgsConstructor
public class ReviewController implements ReviewControllerApi {

    private ReviewService reviewService;

    @GetMapping
    public ResponseEntity<Page<ReviewResponse>> getReviewsByBook(
            @PathVariable Long bookId,
            Pageable pageable) {
        return ResponseEntity.ok(reviewService.getReviewsByBook(bookId, pageable));
    }

    @PostMapping
    public ResponseEntity<ReviewResponse> createReview(
            @PathVariable Long bookId,
            @Valid @RequestBody ReviewRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(reviewService.createReview(bookId, request));
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<ReviewResponse> updateReview(
            @PathVariable Long bookId,
            @PathVariable Long reviewId,
            @Valid @RequestBody ReviewRequest request) {
        return ResponseEntity
                .ok(reviewService.updateReview(bookId, reviewId, request));
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Long bookId,
            @PathVariable Long reviewId) {
        reviewService.deleteReview(bookId, reviewId);
        return ResponseEntity.noContent().build();
    }
}
