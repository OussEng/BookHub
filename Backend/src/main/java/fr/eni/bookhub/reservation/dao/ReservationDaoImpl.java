package fr.eni.bookhub.reservation.dao;

import fr.eni.bookhub.reservation.entity.Reservation;
import fr.eni.bookhub.reservation.entity.ReservationStatus;
import fr.eni.bookhub.reservation.repository.ReservationRepository;
import fr.eni.bookhub.user.entity.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Optional;

// Délégation pure vers Spring Data : aucune règle métier ici, elle vit dans le service
@Component
@AllArgsConstructor
public class ReservationDaoImpl implements IReservationDao {

    // Seul point d'accès à la base : le service ne connaît que l'interface
    private final ReservationRepository reservationRepository;

    // INSERT puis relecture de l'id généré par IDENTITY
    @Override
    public Reservation save(Reservation reservation) {
        return reservationRepository.save(reservation);
    }

    // SELECT COUNT(*) ... WHERE users_id = ? AND reservation_status IN (?)
    @Override
    public long countByUserAndStatusIn(User user, Collection<ReservationStatus> statuts) {
        return reservationRepository.countByUserAndStatusIn(user, statuts);
    }

    // SELECT ... TOP 1 : s'arrête dès la première ligne trouvée, pas de comptage
    @Override
    public boolean existsByUserAndBookIdAndStatusIn(User user, Long bookId, Collection<ReservationStatus> statuts) {
        return reservationRepository.existsByUserAndBookIdAndStatusIn(user, bookId, statuts);
    }

    // SELECT COUNT(*) ... WHERE book_id = ? AND reservation_status IN (?)
    @Override
    public long countByBookIdAndStatusIn(Long bookId, Collection<ReservationStatus> statuts) {
        return reservationRepository.countByBookIdAndStatusIn(bookId, statuts);
    }

    @Override
    public Optional<Reservation> findFirstByBookIdAndStatusOrderByReservesDateAsc(Long bookId, ReservationStatus status) {
        return reservationRepository.findFirstByBookIdAndStatusOrderByReservesDateAsc(bookId, status);
    }

    @Override
    public boolean existsByBookCopyIdAndStatus(Long bookCopyId, ReservationStatus reservationStatus) {
        return reservationRepository.existsByBookCopyIdAndStatus(bookCopyId, reservationStatus);
    }
}
