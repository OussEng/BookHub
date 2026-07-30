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
import fr.eni.bookhub.reservation.dto.response.BookActionResponse;
import fr.eni.bookhub.reservation.dto.response.ReservationResponse;
import fr.eni.bookhub.reservation.entity.BookAction;
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
import java.util.List;
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

        if (reservationDao.countByUserAndStatusIn(currentUser, ReservationStatus.ACTIFS) >= 5)
            throw new ConflictException("Limite de 5 réservations en cours atteinte");

        // Livre disponible à l'emprunt
        if (bookCopyDao.existsByBook_IdAndBookStatusAndConditionIn(bookId, BookStatus.AVAILABLE, EnumSet.of(Condition.NEW, Condition.GOOD)))
            throw new ConflictException("Le livre est disponible, empruntez-le directement");

        Reservation reservation = reservationDao.save(new Reservation(currentUser, bookFound));

        return ReservationResponse.fromEntity(reservation, rank(reservation));
    }

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

        // 4. Vérifie L'état
        if (!bookCopyService.goodCondition(bookCopy)) {
            bookCopy.setBookStatus(BookStatus.AVAILABLE);
            bookCopyDao.saveAndFlush(bookCopy);
            return false;
        }

        // 5. Mise de côté.
        bookCopy.setBookStatus(BookStatus.RESERVED);
        bookCopyDao.saveAndFlush(bookCopy);

        // 6. La réservation passe en tête. Deadline stockée, jamais recalculée.
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        Reservation reservation = firstReservation.get();
        reservation.setStatus(ReservationStatus.READY_FOR_PICKUP);
        reservation.setBookCopy(bookCopy);
        reservation.setNotifiedAt(now);
        reservation.setPickupDeadline(now.plusHours(PICKUP_DELAY_HOURS));

        return true;
    }

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

        Reservation reservation = reservationDao.findByIdForUpdate(reservationId)
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
        Reservation reservation = reservationDao.findByIdForUpdate(reservationId)
                .orElseThrow();

        if (reservation.getStatus() != ReservationStatus.READY_FOR_PICKUP) {
            return; // déjà traitée entre-temps, ou changée d'état.
        }

        Long bookId = reservation.getBook().getId();
        Long bookCopyId = reservation.getBookCopy().getId();

        reservation.setStatus(ReservationStatus.EXPIRED);
        reservationDao.save(reservation);

        promote(bookId, bookCopyId, BookStatus.RESERVED);
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> getMyReservations() {
        User currentUser = userProvider.getCurrentUser();

        return reservationDao.findByUserOrderByReservesDateDesc(currentUser)
                .stream()
                .map(reservation -> ReservationResponse.fromEntity(reservation, rank(reservation)))
                .toList();
    }

    /**
     * Position dans la file : une de plus que le nombre de réservations
     * actives entrées avant celle-ci. 0 pour une réservation close.
     */
    private long rank(Reservation reservation) {
        if (!ReservationStatus.ACTIFS.contains(reservation.getStatus())) {
            return 0;
        }
        return reservationDao.countByBookIdAndStatusInAndReservesDateBefore(
                reservation.getBook().getId(),
                ReservationStatus.ACTIFS,
                reservation.getReservesDate()) + 1;
    }

    @Transactional(readOnly = true)
    public BookActionResponse getAvailableAction(Long bookId) {
        User user = userProvider.getCurrentUser();

        // 1. Réservation active du lecteur sur ce livre : prioritaire sur tout le reste
        Optional<Reservation> mine = reservationDao
                .findFirstByUserAndBookIdAndStatusIn(user, bookId, ReservationStatus.ACTIFS);

        if (mine.isPresent()) {
            Reservation r = mine.get();
            if (r.getStatus() == ReservationStatus.READY_FOR_PICKUP
                    && r.getPickupDeadline().isAfter(LocalDateTime.now(ZoneOffset.UTC))) {
                return new BookActionResponse(
                        BookAction.PICKUP, null, r.getId(), 0, r.getPickupDeadline());
            }
            return new BookActionResponse(
                    BookAction.CANCEL, null, r.getId(), rank(r), null);
        }

        // 2. Prêt en cours du lecteur sur ce livre
        if (loanDao.existsByLoanerIdAndBookCopyLoanedBookIdAndStatus(
                user.getId(), bookId, LoanStatus.ACTIVE)) {
            return new BookActionResponse(
                    BookAction.NONE, "Vous empruntez déjà ce livre", null, 0, null);
        }

        // 3. Un exemplaire est empruntable : bouton « Emprunter »
        if (bookCopyService.hasLoanableCopy(bookId)) {
            if (loanDao.countByLoanerIdAndStatus(user.getId(), LoanStatus.ACTIVE) >= 3) {
                return new BookActionResponse(
                        BookAction.NONE, "Limite de 3 emprunts atteinte", null, 0, null);
            }
            return new BookActionResponse(BookAction.LOAN, null, null, 0, null);
        }

        // 4. Rien de disponible : bouton « Réserver »
        if (reservationDao.countByUserAndStatusIn(user, ReservationStatus.ACTIFS) >= 5) {
            return new BookActionResponse(
                    BookAction.NONE, "Limite de 5 réservations atteinte", null, 0, null);
        }
        return new BookActionResponse(BookAction.RESERVE, null, null, 0, null);
    }
}
