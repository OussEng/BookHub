package fr.eni.bookhub.review.dto.response;

import fr.eni.bookhub.review.entity.Review;
import fr.eni.bookhub.review.entity.ReviewStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AdminReviewResponse {

    private final Long id;
    private final String username;
    private final Long bookId;
    private final Integer rating;
    private final String comment;
    private final boolean moderated;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final LocalDateTime moderatedAt;

    public static AdminReviewResponse fromEntity(Review review) {
        String username = review.getUser().getDisplayUsername();
        String firstname = review.getUser().getFirstname();
        String lastname = review.getUser().getLastname();

        return AdminReviewResponse.builder()
                .id(review.getId())
                .username(username != null ? username : (firstname + " " + lastname).trim())
                .bookId(review.getBook().getId())
                .rating(review.getRating())
                .comment(review.getComment())
                .moderated(review.getStatus() == ReviewStatus.MODERATED)
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .moderatedAt(review.getModeratedAt())
                .build();
    }
}
