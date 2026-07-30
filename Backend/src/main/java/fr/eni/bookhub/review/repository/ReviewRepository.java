package fr.eni.bookhub.review.repository;

import fr.eni.bookhub.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    boolean existsByUserIdAndBookId(Long userId, Long bookId);

    Page<Review> findByBookId(Long bookId, Pageable pageable);
}
