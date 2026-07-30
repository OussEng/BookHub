package fr.eni.bookhub.review.service;

import fr.eni.bookhub.book.dao.IBookDao;
import fr.eni.bookhub.book.entity.Book;
import fr.eni.bookhub.exception.custom.ConflictException;
import fr.eni.bookhub.exception.custom.ResourceNotFoundException;
import fr.eni.bookhub.loan.dao.ILoanDao;
import fr.eni.bookhub.loan.entity.LoanStatus;
import fr.eni.bookhub.review.dao.IReviewDao;
import fr.eni.bookhub.review.dto.request.ReviewRequest;
import fr.eni.bookhub.review.dto.response.AdminReviewResponse;
import fr.eni.bookhub.review.dto.response.ReviewResponse;
import fr.eni.bookhub.review.entity.Review;
import fr.eni.bookhub.review.entity.ReviewStatus;
import fr.eni.bookhub.security.AuthenticatedUserProvider;
import fr.eni.bookhub.user.entity.User;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class ReviewService {

    private AuthenticatedUserProvider authenticatedUserProvider;
    private IBookDao bookDao;
    private ILoanDao loanDao;
    private IReviewDao reviewDao;

    @PreAuthorize("isAuthenticated()")
    @Transactional
    public ReviewResponse createReview(Long bookId, ReviewRequest request) {
        User currentUser = authenticatedUserProvider.getCurrentUser();

        Book book = bookDao.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Ouvrage introuvable"));

        if (reviewDao.existsByUserIdAndBookId(currentUser.getId(), bookId)) {
            throw new ConflictException("Vous avez déjà laissé un avis pour ce livre, vous pouvez le modifier plutôt que d'en créer un nouveau");
        }

        if (!loanDao.existsByLoanerIdAndBookCopyLoanedBookIdAndStatus(currentUser.getId(), bookId, LoanStatus.RETURNED)) {
            throw new AccessDeniedException("Vous ne pouvez pas noter cette ouvrage");
        }

        Review review = Review.builder()
                .rating(request.getRating())
                .comment(request.getComment())
                .status(ReviewStatus.ACTIVE)
                .user(currentUser)
                .book(book)
                .build();

        return ReviewResponse.fromEntity(reviewDao.save(review));
    }

    @PreAuthorize("isAuthenticated()")
    @Transactional
    public ReviewResponse updateReview(Long bookId, Long reviewId, ReviewRequest request) {
        User currentUser = authenticatedUserProvider.getCurrentUser();

        if (!bookDao.existsById(bookId)) {
            throw new ResourceNotFoundException("Ouvrage introuvable");
        }

        Review review = reviewDao.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Avis introuvable"));

        if (!review.getBook().getId().equals(bookId)) {
            throw new ResourceNotFoundException("Avis introuvable");
        }

        if (!review.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Vous ne pouvez pas modifier l'avis d'un autre utilisateur");
        }

        if (review.getStatus() == ReviewStatus.MODERATED) {
            throw new ConflictException("Cet avis a été modéré et ne peut plus être modifié");
        }

        review.setRating(request.getRating());
        review.setComment(request.getComment());

        return ReviewResponse.fromEntity(reviewDao.save(review));
    }

    @PreAuthorize("isAuthenticated()")
    @Transactional
    public void deleteReview(Long bookId, Long reviewId) {
        User currentUser = authenticatedUserProvider.getCurrentUser();

        if (!bookDao.existsById(bookId)) {
            throw new ResourceNotFoundException("Ouvrage introuvable");
        }

        Review review = reviewDao.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Avis introuvable"));

        if (!review.getBook().getId().equals(bookId)) {
            throw new ResourceNotFoundException("Avis introuvable");
        }

        if (!review.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Vous ne pouvez pas supprimer l'avis d'un autre utilisateur");
        }

        if (review.getStatus() == ReviewStatus.MODERATED) {
            throw new ConflictException("Cet avis a été modéré et ne peut plus être supprimé");
        }

        reviewDao.delete(review);
    }

    @PreAuthorize("isAuthenticated()")
    @Transactional
    public Page<ReviewResponse> getReviewsByBook(Long bookId, Pageable pageable) {
        if (!bookDao.existsById(bookId)) {
            throw new ResourceNotFoundException("Ouvrage introuvable");
        }

        Page<Review> reviews = reviewDao.findByBookId(bookId, pageable);
        return reviews.map(ReviewResponse::fromEntity);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public Page<AdminReviewResponse> getAllReviews(Pageable pageable) {
        Page<Review> reviews = reviewDao.findAll(pageable);
        return reviews.map(AdminReviewResponse::fromEntity);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    @Transactional
    public void moderateReview(Long reviewId) {
        Review review = reviewDao.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Avis introuvable"));

        review.setStatus(ReviewStatus.MODERATED);
        review.setModeratedAt(LocalDateTime.now());

        reviewDao.save(review);
    }
}
