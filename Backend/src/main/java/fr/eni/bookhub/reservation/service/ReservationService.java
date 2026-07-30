package fr.eni.bookhub.reservation.service;

import fr.eni.bookhub.book.dao.IBookDao;
import fr.eni.bookhub.book.entity.Book;
import fr.eni.bookhub.bookcopy.dao.IBookCopyDao;
import fr.eni.bookhub.bookcopy.entity.BookCopy;
import fr.eni.bookhub.bookcopy.entity.BookStatus;
import fr.eni.bookhub.bookcopy.entity.Condition;
import fr.eni.bookhub.bookcopy.service.BookCopyService;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.EnumSet;
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
    private BookCopyService bookCopyService;

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
        if (bookCopyDao.existsByBook_IdAndBookStatusAndConditionIn(bookId, BookStatus.AVAILABLE, EnumSet.of(Condition.NEW, Condition.GOOD)))
            throw new ConflictException("Le livre est dipsonible, empruntez-le directement");

        long reservationListLength = reservationDao.countByBookIdAndStatusIn(bookId, ReservationStatus.ACTIFS);

        // Liste de réservation supérieur ou égale à 5.
        if (reservationListLength >= 5)
            throw new ConflictException("Limite de 5 réservations atteintes");

        Reservation reservation = reservationDao.save(new Reservation(currentUser, bookFound));

        long rank = reservationDao.countByBookIdAndStatusIn(bookId, ReservationStatus.ACTIFS);

        return ReservationResponse.fromEntity(reservation, rank);
    }

    /**
     * Fait avancer la file d'un cran pour un livre donné.
     * Appelée sur retour de prêt (fromStatus = AVAILABLE, une fois l'exemplaire libéré),
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

        // 1. La file d'abord, l'exemplaire ensuite : ordre de verrouillage absolu.
        Optional<Reservation> firstReservation = reservationDao
                .findFirstByBookIdAndStatusOrderByReservesDateAsc(bookId, ReservationStatus.PENDING);

        BookCopy bookCopy = bookCopyDao.findByIdForUpdate(bookCopyId)
                .orElseThrow(() -> new ResourceNotFoundException("Exemplaire introuvable"));

        if (bookCopy.getBookStatus() != fromStatus) {
            log.warn("Promotion ignorée : bookCopy={} n'était pas {}", bookCopyId, fromStatus);
            return false;
        }

        // 2. Personne n'attend : l'exemplaire redevient empruntable.
        if (firstReservation.isEmpty()) {
            bookCopy.setBookStatus(BookStatus.AVAILABLE);
            bookCopyDao.saveAndFlush(bookCopy);
            return false;
        }

        // 3. Exemplaire déjà mis de côté pour quelqu'un d'autre : on n'y touche pas.
        if (reservationDao.existsByBookCopyIdAndStatus(bookCopyId, ReservationStatus.READY_FOR_PICKUP)) {
            return false;
        }

        // 4. Mise de côté.
        bookCopy.setBookStatus(BookStatus.RESERVED);
        bookCopyDao.saveAndFlush(bookCopy);

        // 5. La réservation passe en tête. Deadline stockée, jamais recalculée.
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        Reservation reservation = firstReservation.get();
        reservation.setStatus(ReservationStatus.READY_FOR_PICKUP);
        reservation.setBookCopy(bookCopy);
        reservation.setNotifiedAt(now);
        reservation.setPickupDeadline(now.plusHours(PICKUP_DELAY_HOURS));

        return true;
    }

    /**
     * Vérifie si l'utilisateur a une réservation prête à retirer pour ce livre.
     * Si oui, la clôture (FULFILLED) et renvoie l'exemplaire mis de côté.
     * Si le délai de retrait est dépassé, lève une exception.
     *
     * @return l'exemplaire réservé, ou vide si aucune réservation prête n'existe
     */
    @Transactional
    public Optional<BookCopy> fulfillIfReady(User user, Long bookId) {
        Optional<Reservation> readyReservation = reservationDao
                .findByUserAndBookIdAndStatus(user, bookId, ReservationStatus.READY_FOR_PICKUP);

        if (readyReservation.isEmpty()) {
            return Optional.empty();
        }

        Reservation reservation = readyReservation.get();

        if (reservation.getPickupDeadline().isBefore(LocalDateTime.now(ZoneOffset.UTC))) {
            throw new ConflictException("Le délai de retrait de cette réservation est dépassé");
        }

        BookCopy bookCopy = reservation.getBookCopy();

        if (!bookCopyService.goodCondition(bookCopy)) {
            throw new ConflictException("L'exemplaire réservé n'est plus dans un état empruntable");
        }

        reservation.setStatus(ReservationStatus.FULFILLED);
        reservationDao.save(reservation);

        return Optional.of(bookCopy);
    }

    @Transactional
    public void cancelReservation(Long reservationId) {
        User currentUser = userProvider.getCurrentUser();

        Reservation reservation = reservationDao.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Réservation introuvable"));

        if (!reservation.getUser().getId().equals(currentUser.getId()))
            throw new AccessDeniedException("Vous n'êtes pas autorisé à annuler cette réservation");

        if (!ReservationStatus.ACTIFS.contains(reservation.getStatus())) {
            throw new ConflictException("Cette réservation n'est pas active et ne peut pas être annulée");
        }

        ReservationStatus previousStatus = reservation.getStatus();

        reservation.setStatus(ReservationStatus.CANCELLED);
        reservationDao.save(reservation);

        if (previousStatus == ReservationStatus.READY_FOR_PICKUP) {
            promote(reservation.getBook().getId(), reservation.getBookCopy().getId(), BookStatus.RESERVED);
        }
    }

    @Transactional
    public void expireOne(Long reservationId) {
        Reservation reservation = reservationDao.findById(reservationId)
                .orElseThrow();

        if (reservation.getStatus() != ReservationStatus.READY_FOR_PICKUP) {
            return; // déjà traitée entre-temps, ou changée d'état
        }

        Long bookId = reservation.getBook().getId();
        Long bookCopyId = reservation.getBookCopy().getId();

        reservation.setStatus(ReservationStatus.EXPIRED);
        reservationDao.save(reservation);

        promote(bookId, bookCopyId, fr.eni.bookhub.bookcopy.entity.BookStatus.RESERVED);
    }
}
