package fr.eni.bookhub.bookcopy.repository;

import fr.eni.bookhub.bookcopy.entity.BookCopy;
import fr.eni.bookhub.bookcopy.entity.BookStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookCopyRepository extends JpaRepository<BookCopy, Long> {
    List<BookCopy> findByBookId(Long bookId);
    boolean existsByBook_IdAndBookStatus(Long bookId, BookStatus bookStatus);
    List<BookCopy> findByBookIdAndBookStatus(Long bookId, BookStatus bookStatus);
    Optional<BookCopy> findBySerialNumber(String serialNumber);
}
