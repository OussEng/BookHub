package fr.eni.bookhub.reservation.repository;

import fr.eni.bookhub.reservation.entity.Reservation;
import fr.eni.bookhub.reservation.entity.ReservationStatus;
import fr.eni.bookhub.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

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
}