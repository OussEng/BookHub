package fr.eni.bookhub.reservation.repository;

import fr.eni.bookhub.reservation.entity.Reservation;
import fr.eni.bookhub.reservation.entity.ReservationStatus;
import fr.eni.bookhub.user.entity.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    // ─── Lectures simples : aucun verrou ───

    long countByUserAndStatusIn(User user, Collection<ReservationStatus> statuses);

    boolean existsByUserAndBookIdAndStatusIn(User user, Long bookId, Collection<ReservationStatus> statuses);

    long countByBookIdAndStatusIn(Long bookId, Collection<ReservationStatus> statuses);

    boolean existsByBookCopyIdAndStatus(Long bookCopyId, ReservationStatus status);

    List<Reservation> findByStatusAndPickupDeadlineBefore(ReservationStatus status, LocalDateTime deadline);

    // Réservations du lecteur, la plus récente en premier
    List<Reservation> findByUserOrderByReservesDateDesc(User user);

    // Nombre de réservations actives entrées dans la file avant celle-ci
    long countByBookIdAndStatusInAndReservesDateBefore(
            Long bookId,
            Collection<ReservationStatus> statuses,
            LocalDateTime reservesDate);

    Optional<Reservation> findFirstByUserAndBookIdAndStatusIn(
            User user, Long bookId, Collection<ReservationStatus> statuses);


    // ─── Lectures verrouillantes : suffixe ForUpdate ───

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Reservation> findFirstByBookIdAndStatusOrderByReservesDateAsc(
            Long bookId,
            ReservationStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from Reservation r where r.id = :id")
    Optional<Reservation> findByIdForUpdate(@Param("id") Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Reservation> findByUserAndBookIdAndStatus(User user, Long bookId, ReservationStatus status);
}