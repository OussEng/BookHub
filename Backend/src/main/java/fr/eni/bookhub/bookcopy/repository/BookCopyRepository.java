package fr.eni.bookhub.bookcopy.repository;

import fr.eni.bookhub.bookcopy.entity.BookCopy;
import fr.eni.bookhub.bookcopy.entity.BookStatus;
import fr.eni.bookhub.bookcopy.entity.Condition;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookCopyRepository extends JpaRepository<BookCopy, Long> {

    List<BookCopy> findByBookId(Long bookId);

    boolean existsByBook_IdAndBookStatus(Long bookId, BookStatus bookStatus);

    List<BookCopy> findByBookIdAndBookStatus(Long bookId, BookStatus bookStatus);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from BookCopy c where c.id = :id")
    Optional<BookCopy> findByIdForUpdate(@Param("id") Long id);

    boolean existsByBook_IdAndBookStatusAndConditionIn(Long bookId, BookStatus bookStatus, Collection<Condition> conditions);
}
