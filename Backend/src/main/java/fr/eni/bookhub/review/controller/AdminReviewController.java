package fr.eni.bookhub.review.controller;

import fr.eni.bookhub.review.dto.response.AdminReviewResponse;
import fr.eni.bookhub.review.service.ReviewService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/admin/reviews", produces = "application/json")
@AllArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
public class AdminReviewController {

    private final ReviewService reviewService;

    @GetMapping
    public ResponseEntity<Page<AdminReviewResponse>> getAllReviews(
            Pageable pageable) {
        return ResponseEntity.ok(reviewService.getAllReviews(pageable));
    }

    @PatchMapping("/{reviewId}/moderate")
    public ResponseEntity<Void> moderateReview(@PathVariable Long reviewId) {
        reviewService.moderateReview(reviewId);
        return ResponseEntity.noContent().build();
    }


}
