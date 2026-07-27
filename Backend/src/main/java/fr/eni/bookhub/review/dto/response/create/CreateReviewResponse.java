package fr.eni.bookhub.review.dto.response.create;

import fr.eni.bookhub.auth.dto.response.RegisterResponse;
import fr.eni.bookhub.review.entity.Review;
import fr.eni.bookhub.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class CreateReviewResponse {

    private final Long id;

    private final String username;

    private final Long bookId;

    private final int rating;

    private final String comment;

    private final LocalDateTime createdAt;

    public static CreateReviewResponse fromUserEntity(Review review) {
        return new CreateReviewResponse(
                review.getId(),
                review.getUser().getUsername() != null ? review.getUser().getUsername() : review.getUser().getFirstname() + " " + review.getUser().getLastname(),
                review.getBook().getId(),
                review.getRating(),
                review.getComment(),
                review.getCreatedAt()
        );
    }
}
