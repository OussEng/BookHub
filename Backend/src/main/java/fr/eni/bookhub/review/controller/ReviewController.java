package fr.eni.bookhub.review.controller;

import fr.eni.bookhub.review.dto.request.create.CreateReviewDTO;
import fr.eni.bookhub.review.dto.response.create.CreateReviewResponse;
import fr.eni.bookhub.review.service.ReviewService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/books/{bookId}/reviews", produces = "application/json")
@AllArgsConstructor
public class ReviewController {

    private ReviewService reviewService;

    @PostMapping
    public ResponseEntity<CreateReviewResponse> createReview(
            @PathVariable Long bookId,
            @Valid @RequestBody CreateReviewDTO request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(reviewService.createReview(request));
    }
}
