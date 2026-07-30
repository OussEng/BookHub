package fr.eni.bookhub.review.repository;

import fr.eni.bookhub.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    boolean existsByUserIdAndBookId(Long userId, Long bookId);

    Page<Review> findByBookId(Long bookId, Pageable pageable);

    @Query("""
    SELECT r FROM Review r
    JOIN r.book b
    LEFT JOIN b.authors a
    WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :search, '%'))
       OR LOWER(a.firstname) LIKE LOWER(CONCAT('%', :search, '%'))
       OR LOWER(a.lastname) LIKE LOWER(CONCAT('%', :search, '%'))
       OR LOWER(a.penName) LIKE LOWER(CONCAT('%', :search, '%'))
    """)
    Page<Review> searchByBookOrAuthor(@Param("search") String search, Pageable pageable);
}
