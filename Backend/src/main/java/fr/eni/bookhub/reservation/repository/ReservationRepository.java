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

    long countByUserAndStatusIn(User user, Collection<ReservationStatus> statuts);

    boolean existsByUserAndBookIdAndStatusIn(User user, Long bookId, Collection<ReservationStatus> statuses);

    long countByBookIdAndStatusIn(Long bookId, Collection<ReservationStatus> statuts);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Reservation> findFirstByBookIdAndStatusOrderByReservesDateAsc(
            Long bookId,
            ReservationStatus status);

    boolean existsByBookCopyIdAndStatus(Long bookCopyId, ReservationStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Reservation> findByUserAndBookIdAndStatus(User user, Long bookId, ReservationStatus status);

    List<Reservation> findByStatusAndPickupDeadlineBefore(ReservationStatus status, LocalDateTime deadline);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from Reservation r where r.id = :id")
    Optional<Reservation> findByIdForUpdate(@Param("id") Long id);

    // Réservations du lecteur, la plus récente en premier
    List<Reservation> findByUserOrderByReservesDateDesc(User user);

    // Nombre de réservations actives entrées dans la file avant celle-ci
    long countByBookIdAndStatusInAndReservesDateBefore(
            Long bookId,
            Collection<ReservationStatus> statuts,
            LocalDateTime reservesDate);

}