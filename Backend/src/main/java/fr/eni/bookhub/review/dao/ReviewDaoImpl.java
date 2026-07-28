package fr.eni.bookhub.review.dao;

import fr.eni.bookhub.review.entity.Review;
import fr.eni.bookhub.review.repository.ReviewRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@AllArgsConstructor
public class ReviewDaoImpl implements IReviewDao {

    private final ReviewRepository reviewRepository;

    @Override
    public Review save(Review review) {
        return reviewRepository.save(review);
    }

    @Override
    public boolean existsByUserIdAndBookId(Long userId, Long bookId) {
        return reviewRepository.existsByUserIdAndBookId(userId, bookId);
    }

    @Override
    public Optional<Review> findById(Long reviewId) {
        return reviewRepository.findById(reviewId);
    }

    @Override
    public Page<Review> findByBookId(Long bookId, Pageable pageable) {
        return reviewRepository.findByBookId(bookId, pageable);
    }

    @Override
    public Page<Review> findAll(Pageable pageable) {
        return reviewRepository.findAll(pageable);
    }

    @Override
    public void delete(Review review) {
        reviewRepository.delete(review);
    }
}
