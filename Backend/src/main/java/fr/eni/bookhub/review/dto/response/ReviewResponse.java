package fr.eni.bookhub.review.dto.response;

import fr.eni.bookhub.review.entity.Review;
import fr.eni.bookhub.review.entity.ReviewStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ReviewResponse {

    private final Long id;
    private final String username;
    private final Long bookId;
    private final Integer rating;
    private final String comment;
    private final boolean moderated;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public static ReviewResponse fromEntity(Review review) {
        boolean isModerated = review.getStatus() == ReviewStatus.MODERATED;
        String username = review.getUser().getDisplayUsername();
        String firstname = review.getUser().getFirstname();
        String lastname = review.getUser().getLastname();
        String initial = lastname != null && !lastname.isBlank()
                ? lastname.substring(0, 1).toUpperCase() + "."
                : "";

        return ReviewResponse.builder()
                .id(review.getId())
                .username(username != null ? username : (firstname + " " + initial).trim())
                .bookId(review.getBook().getId())
                .rating(review.getRating())
                .comment(isModerated ? null : review.getComment())
                .moderated(isModerated)
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }
}
