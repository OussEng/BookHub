package fr.eni.bookhub.reservation.service;

import fr.eni.bookhub.book.dao.IBookDao;
import fr.eni.bookhub.book.entity.Book;
import fr.eni.bookhub.bookcopy.dao.IBookCopyDao;
import fr.eni.bookhub.bookcopy.entity.BookCopy;
import fr.eni.bookhub.bookcopy.entity.BookStatus;
import fr.eni.bookhub.exception.custom.ConflictException;
import fr.eni.bookhub.exception.custom.ResourceNotFoundException;
import fr.eni.bookhub.loan.dao.ILoanDao;
import fr.eni.bookhub.loan.entity.LoanStatus;
import fr.eni.bookhub.reservation.dao.IReservationDao;
import fr.eni.bookhub.reservation.dto.response.ReservationResponse;
import fr.eni.bookhub.reservation.entity.Reservation;
import fr.eni.bookhub.reservation.entity.ReservationStatus;
import fr.eni.bookhub.security.AuthenticatedUserProvider;
import fr.eni.bookhub.user.entity.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class ReservationService {

    private IReservationDao reservationDao;
    private AuthenticatedUserProvider userProvider;
    private IBookDao bookDao;
    private IBookCopyDao bookCopyDao;
    private ILoanDao loanDao;

    private static final int PICKUP_DELAY_HOURS = 72;

    @Transactional
    public ReservationResponse createReservation(Long bookId) {
        User currentUser = userProvider.getCurrentUser();

        // Livre qui n'existe pas en base
        Book bookFound = bookDao.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Ouvrage introuvable"));

        // Livre déjà en cours d'emprunt par l'utilisateur connecté
        if (loanDao.existsByLoanerIdAndBookCopyLoanedBookIdAndStatus(currentUser.getId(), bookId, LoanStatus.ACTIVE))
            throw new ConflictException("Vous empruntez déjà ce livre");

        // Livre déjà en cours de réservation par l'utilisateur connecté
        if (reservationDao.existsByUserAndBookIdAndStatusIn(currentUser, bookId, ReservationStatus.ACTIFS))
            throw new ConflictException("Vous réservez déjà ce livre");

        // Livre disponible à l'emprunt
        if (bookCopyDao.existsByBook_IdAndBookStatus(bookId, BookStatus.AVAILABLE))
            throw new ConflictException("Le livre est dipsonible, empruntez-le directement");

        long reservationListLength = reservationDao.countByBookIdAndStatusIn(bookId, ReservationStatus.ACTIFS);

        // Liste de réservation supérieur ou égale à 5
        if (reservationListLength >= 5)
            throw new ConflictException("Limite de 5 réservations atteintes");

        Reservation reservation = reservationDao.save(new Reservation(currentUser, bookFound));

        long rank = reservationDao.countByBookIdAndStatusIn(bookId, ReservationStatus.ACTIFS);

        return ReservationResponse.fromEntity(reservation, rank);
    }

    /**
     * Fait avancer la file d'un cran pour un livre donné.
     * Appelée sur retour de prêt (fromStatus = LOANED),
     * sur annulation et sur expiration (fromStatus = RESERVED).
     * L'appelant tient la transaction.
     *
     * @return true si une réservation a été promue
     */
    @Transactional(propagation = Propagation.MANDATORY)
    public boolean promote(Long bookId, Long bookCopyId, BookStatus fromStatus) {

        if (bookId == null || bookCopyId == null) {
            return false;
        }

        BookCopy bookCopy = bookCopyDao.findById(bookCopyId)
                .orElseThrow(() -> new ResourceNotFoundException("Exemplaire introuvable"));

        // 1. La file d'abord, l'exemplaire ensuite : ordre de verrouillage absolu.
        Optional<Reservation> firstReservation = reservationDao
                .findFirstByBookIdAndStatusOrderByReservesDateAsc(bookId, ReservationStatus.PENDING);

        // 2. Personne n'attend : l'exemplaire redevient empruntable.
        if (firstReservation.isEmpty()) {
            int updated = bookCopyDao.changeStatus(
                    bookCopyId, bookId, fromStatus, BookStatus.AVAILABLE);
            if (updated == 0) {
                log.warn("Libération ignorée : bookCopy={} n'était pas {}", bookCopyId, fromStatus);
            }
            return false;
        }

        // 3. Exemplaire déjà mis de côté pour quelqu'un d'autre : on n'y touche pas.
        if (reservationDao.existsByBookCopyIdAndStatus(bookCopyId, ReservationStatus.AVAILABLE)) {
            return false;
        }

        // 4. Mise de côté. Zéro ligne = état inattendu, on sort sans promouvoir.
        int updated = bookCopyDao.changeStatus(
                bookCopyId, bookId, fromStatus, BookStatus.RESERVED);
        if (updated == 0) {
            log.warn("Promotion abandonnée : bookCopy={} n'était pas {}", bookCopyId, fromStatus);
            return false;
        }

        // 5. La réservation passe en tête. Deadline stockée, jamais recalculée.
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        Reservation reservation = firstReservation.get();
        reservation.setStatus(ReservationStatus.AVAILABLE);
        reservation.setBookCopy(bookCopy);
        reservation.setNotifiedAt(now);
        reservation.setPickupDeadline(now.plusHours(PICKUP_DELAY_HOURS));

        return true;
    }


}
