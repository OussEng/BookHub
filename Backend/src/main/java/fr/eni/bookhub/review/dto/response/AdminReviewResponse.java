package fr.eni.bookhub.review.dto.response;

import fr.eni.bookhub.author.entity.Author;
import fr.eni.bookhub.review.entity.Review;
import fr.eni.bookhub.review.entity.ReviewStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class AdminReviewResponse {

    private final Long id;
    private final String username;
    private final Long bookId;
    private final String bookTitle;
    private final List<String> bookAuthors;
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

        List<String> authorNames = review.getBook().getAuthors().stream()
                .map(AdminReviewResponse::formatAuthorName)
                .collect(Collectors.toList());

        return AdminReviewResponse.builder()
                .id(review.getId())
                .username(username != null ? username : (firstname + " " + lastname).trim())
                .bookId(review.getBook().getId())
                .bookTitle(review.getBook().getTitle())
                .bookAuthors(authorNames)
                .rating(review.getRating())
                .comment(review.getComment())
                .moderated(review.getStatus() == ReviewStatus.MODERATED)
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .moderatedAt(review.getModeratedAt())
                .build();
    }

    private static String formatAuthorName(Author author) {
        if (author.getPenName() != null && !author.getPenName().isBlank()) {
            return author.getPenName();
        }
        return (author.getFirstname() + " " + author.getLastname()).trim();
    }
}