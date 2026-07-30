package fr.eni.bookhub.bookcopy.repository;

import fr.eni.bookhub.bookcopy.entity.BookCopy;
import fr.eni.bookhub.bookcopy.entity.BookStatus;
import fr.eni.bookhub.bookcopy.entity.Condition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookCopyRepository extends JpaRepository<BookCopy, Long> {
    List<BookCopy> findByBookId(Long bookId);
    boolean existsByBook_IdAndBookStatus(Long bookId, BookStatus bookStatus);
    List<BookCopy> findByBookIdAndBookStatus(Long bookId, BookStatus bookStatus);
    Optional<BookCopy> findBySerialNumber(String serialNumber);

    @Query(value = "SELECT c FROM BookCopy c " +
            "WHERE c.book.id = :bookId " +
            "AND (:serialNumber IS NULL OR :serialNumber = '' OR LOWER(c.serialNumber) LIKE LOWER(CONCAT('%', :serialNumber, '%'))) " +
            "AND (CAST(:condition AS string) IS NULL OR c.condition = :condition)",
            countQuery = "SELECT COUNT(c) FROM BookCopy c " +
                    "WHERE c.book.id = :bookId " +
                    "AND (:serialNumber IS NULL OR :serialNumber = '' OR LOWER(c.serialNumber) LIKE LOWER(CONCAT('%', :serialNumber, '%'))) " +
                    "AND (CAST(:condition AS string) IS NULL OR c.condition = :condition)")
    Page<BookCopy> findCopiesByBookIdWithFilters(
            @Param("bookId") Long bookId,
            @Param("serialNumber") String serialNumber,
            @Param("condition") Condition condition,
            Pageable pageable
    );
}
