package fr.eni.bookhub.reservation.dao;

import fr.eni.bookhub.reservation.entity.Reservation;
import fr.eni.bookhub.reservation.entity.ReservationStatus;
import fr.eni.bookhub.user.entity.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface IReservationDao {

    // Enregistre la réservation et renvoie l'objet avec l'id généré par la base
    Reservation save(Reservation reservation);

    // RG-RESA-01 : nombre de réservations vivantes du lecteur, plafonné à 5
    long countByUserAndStatusIn(User user, Collection<ReservationStatus> statuts);

    // Doublon : le lecteur a-t-il déjà une réservation vivante sur ce livre ?
    boolean existsByUserAndBookIdAndStatusIn(User user, Long bookId, Collection<ReservationStatus> statuts);

    // Taille de la file du livre, égale au rang de la réservation qui vient d'être créée
    long countByBookIdAndStatusIn(Long bookId, Collection<ReservationStatus> statuts);

    Optional<Reservation> findFirstByBookIdAndStatusOrderByReservesDateAsc(
            Long bookId,
            ReservationStatus status);

    boolean existsByBookCopyIdAndStatus(Long bookCopyId, ReservationStatus reservationStatus);

    Optional<Reservation> findByUserAndBookIdAndStatus(User user, Long bookId, ReservationStatus status);

    Optional<Reservation> findById(Long reservationId);

    List<Reservation> findByStatusAndPickupDeadlineBefore(ReservationStatus status, LocalDateTime deadline);
}
