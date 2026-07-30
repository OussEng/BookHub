package fr.eni.bookhub.book.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import fr.eni.bookhub.book.entity.Book;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    @Query(value = "SELECT DISTINCT b FROM Book b " +
            "LEFT JOIN b.authors a " +
            "LEFT JOIN b.genres g " +
            "WHERE (:search IS NULL OR :search = '' OR " +
            "   LOWER(b.title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "   LOWER(b.isbn) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "   LOWER(a.firstname) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "   LOWER(a.lastname) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "   LOWER(a.penName) LIKE LOWER(CONCAT('%', :search, '%'))) " +
            "AND (:genreId IS NULL OR g.id = :genreId)",
            countQuery = "SELECT COUNT(DISTINCT b) FROM Book b " +
                    "LEFT JOIN b.authors a " +
                    "LEFT JOIN b.genres g " +
                    "WHERE (:search IS NULL OR :search = '' OR " +
                    "   LOWER(b.title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
                    "   LOWER(b.isbn) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
                    "   LOWER(a.firstname) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
                    "   LOWER(a.lastname) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
                    "   LOWER(a.penName) LIKE LOWER(CONCAT('%', :search, '%'))) " +
                    "AND (:genreId IS NULL OR g.id = :genreId)")
    Page<Book> searchBooks(
            @Param("search") String search,
            @Param("genreId") Long genreId,
            Pageable pageable
    );

}

