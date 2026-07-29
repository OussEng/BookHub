package fr.eni.bookhub.reservation.repository;

import fr.eni.bookhub.book.entity.Book;
import fr.eni.bookhub.bookcopy.entity.BookStatus;
import fr.eni.bookhub.reservation.entity.Reservation;
import fr.eni.bookhub.reservation.entity.ReservationStatus;
import fr.eni.bookhub.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    // RG-RESA-01 : plafond de 5 réservations actives
    long countByUserAndStatusIn(User user, Collection<ReservationStatus> statuts);

    // TODO renommer en Book_Id quand l'entité Book remplacera le champ bookId
    // Doublon : le lecteur a déjà une réservation vivante sur ce livre
    boolean existsByUserAndBookIdAndStatusIn(User user, Long bookId, Collection<ReservationStatus> statuts);

    // TODO renommer en Book_Id quand l'entité Book remplacera le champ bookId
    // Taille de la file. À appeler après le save : la réservation qui vient
    // d'être créée entre toujours en dernier, la taille de la file vaut donc son rang.
    long countByBookIdAndStatusIn(Long bookId, Collection<ReservationStatus> statuts);

    Optional<Reservation> findFirstByBookIdAndStatusOrderByReservesDateAsc(
            Long bookId,
            ReservationStatus status);

    @Modifying(flushAutomatically = true)
    @Query("""
        update BookCopy c set c.bookStatus = :target
        where c.id = :copyId and c.book.id = :bookId
          and c.bookStatus = :expected
        """)
    int compareAndSetStatus(@Param("copyId") Long copyId,
                            @Param("bookId") Long bookId,
                            @Param("target") BookStatus target,
                            @Param("expected") BookStatus expected);


    boolean existsByBookCopyIdAndStatus(Long bookCopyId, ReservationStatus status);
}