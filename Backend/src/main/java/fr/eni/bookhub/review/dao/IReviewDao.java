package fr.eni.bookhub.review.dao;

import fr.eni.bookhub.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface IReviewDao {

    Review save(Review review);

    boolean existsByUserIdAndBookId(Long userId, Long bookId);

    Optional<Review> findById(Long reviewId);

    Page<Review> findByBookId(Long bookId, Pageable pageable);

    Page<Review> findAll(Pageable pageable);

    void delete(Review review);

    Page<Review> searchByBookOrAuthor(String search, Pageable pageable);
}


