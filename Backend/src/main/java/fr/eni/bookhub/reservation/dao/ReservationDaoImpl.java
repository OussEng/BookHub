package fr.eni.bookhub.reservation.dao;

import fr.eni.bookhub.reservation.entity.Reservation;
import fr.eni.bookhub.reservation.entity.ReservationStatus;
import fr.eni.bookhub.reservation.repository.ReservationRepository;
import fr.eni.bookhub.user.entity.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

// Délégation pure vers Spring Data : aucune règle métier ici, elle vit dans le service
@Component
@AllArgsConstructor
public class ReservationDaoImpl implements IReservationDao {

    private final ReservationRepository reservationRepository;

    @Override
    public Reservation save(Reservation reservation) {
        return reservationRepository.save(reservation);
    }

    @Override
    public long countByUserAndStatusIn(User user, Collection<ReservationStatus> statuts) {
        return reservationRepository.countByUserAndStatusIn(user, statuts);
    }

    @Override
    public boolean existsByUserAndBookIdAndStatusIn(User user, Long bookId, Collection<ReservationStatus> statuts) {
        return reservationRepository.existsByUserAndBookIdAndStatusIn(user, bookId, statuts);
    }

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

    @Override
    public Optional<Reservation> findByUserAndBookIdAndStatus(User user, Long bookId, ReservationStatus status) {
        return reservationRepository.findByUserAndBookIdAndStatus(user, bookId, status);
    }

    @Override
    public Optional<Reservation> findById(Long reservationId) {
        return reservationRepository.findById(reservationId);
    }

    @Override
    public List<Reservation> findByStatusAndPickupDeadlineBefore(ReservationStatus status, LocalDateTime deadline) {
        return reservationRepository.findByStatusAndPickupDeadlineBefore(status, deadline);
    }
}
