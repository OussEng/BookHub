package fr.eni.bookhub.bookcopy.repository;

import fr.eni.bookhub.bookcopy.entity.BookCopy;
import fr.eni.bookhub.bookcopy.entity.BookStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookCopyRepository extends JpaRepository<BookCopy, Long> {
    List<BookCopy> findByBookId(Long bookId);
    boolean existsByBook_IdAndBookStatus(Long bookId, BookStatus bookStatus);
    List<BookCopy> findByBookIdAndBookStatus(Long bookId, BookStatus bookStatus);

    /**
     * Change le statut d'un exemplaire uniquement s'il vaut encore fromStatus.
     * Retourne le nombre de lignes touchées : 0 = l'état n'était pas celui attendu.
     */
    @Modifying(flushAutomatically = true)
    @Query("""
        update BookCopy c
        set c.bookStatus = :toStatus
        where c.id = :bookCopyId
          and c.book.id = :bookId
          and c.bookStatus = :fromStatus
        """)
    int changeStatus(@Param("bookCopyId") Long bookCopyId,
                     @Param("bookId") Long bookId,
                     @Param("fromStatus") BookStatus fromStatus,
                     @Param("toStatus") BookStatus toStatus);
}
