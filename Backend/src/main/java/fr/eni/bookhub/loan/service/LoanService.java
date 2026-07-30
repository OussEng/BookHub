package fr.eni.bookhub.loan.service;

import fr.eni.bookhub.bookcopy.dao.IBookCopyDao;
import fr.eni.bookhub.bookcopy.entity.BookCopy;
import fr.eni.bookhub.bookcopy.entity.BookStatus;
import fr.eni.bookhub.bookcopy.service.BookCopyService;
import fr.eni.bookhub.exception.custom.ConflictException;
import fr.eni.bookhub.exception.custom.LoanException;
import fr.eni.bookhub.exception.custom.ResourceNotFoundException;
import fr.eni.bookhub.loan.dao.ILoanDao;
import fr.eni.bookhub.loan.dto.response.LoanDTO;
import fr.eni.bookhub.loan.entity.Loan;
import fr.eni.bookhub.loan.entity.LoanStatus;
import fr.eni.bookhub.reservation.dao.IReservationDao;
import fr.eni.bookhub.reservation.entity.Reservation;
import fr.eni.bookhub.reservation.entity.ReservationStatus;
import fr.eni.bookhub.reservation.service.ReservationService;
import fr.eni.bookhub.security.AuthenticatedUserProvider;
import fr.eni.bookhub.user.entity.User;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class LoanService {

    private final ILoanDao loanRepository;
    private final AuthenticatedUserProvider authenticatedUserProvider;
    private final BookCopyService bookCopyService;
    private final IBookCopyDao bookCopyRepository;
    private final ReservationService reservationService;

// --- CRUD ---

    /*
    Method in charge to find all loans on database.
     */
    public List<LoanDTO> findAll() {
        return loanRepository.findAll()
                .stream()
                .map(LoanDTO::new)
                .toList();
    }

    /*
    Method in charge to find one loan with its id and return a LoanDTO.
    @id : id of the loan you want to find.
     */
    public LoanDTO findLoanById(Long id) {
        return new LoanDTO(loanRepository.findById(id)
                .orElseThrow(() -> new LoanException("Loan not found")));
    }


    /*
    Method in charge to find loans of the authenticated user.
    */
    public List<LoanDTO> findLoansByAuthenticatedUser() {
        return loanRepository.findByLoanerId(authenticatedUserProvider.getCurrentUser().getId())
                .stream()
                .map(LoanDTO::new)
                .toList();
    }

    /*
    Method in charge to find one loan with its id.
    @id : id of the loan you want to find.
     */
    public Loan getLoanEntityById(Long id) {
        return loanRepository.findById(id)
                .orElseThrow(() -> new LoanException("Loan not found"));

    }

    public void createLoan(Long bookId) {
        User currentUser = authenticatedUserProvider.getCurrentUser();

        if (loanRepository.countByLoanerIdAndStatus(currentUser.getId(), LoanStatus.ACTIVE) >= 5) {
            throw new LoanException("Limite maximum de 5 livres atteinte");
        }

        BookCopy bookCopy = reservationService.fulfillIfReady(currentUser, bookId)
                .orElseGet(() -> findAvailableCopy(bookId));

        bookCopy.setBookStatus(BookStatus.LOANED);
        bookCopyRepository.save(bookCopy);

        Loan newLoan = Loan.builder()
                .loaner(currentUser)
                .bookCopyLoaned(bookCopy)
                .loanDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(14))
                .status(LoanStatus.ACTIVE)
                .build();
        loanRepository.save(newLoan);
    }

    private BookCopy findAvailableCopy(Long bookId) {
        List<BookCopy> bookCopyFoundList = bookCopyRepository.findByBookIdAndBookStatus(bookId, BookStatus.AVAILABLE);

        if (bookCopyFoundList.isEmpty()) {
            throw new LoanException("Aucun exemplaire disponible pour ce livre");
        }

        return bookCopyFoundList.stream()
                .filter(bookCopyService::canBeLoaned)
                .findFirst()
                .orElseThrow(() -> new LoanException("Aucun exemplaire disponible dans un état correct"));
    }

    @Transactional
    public void returnLoan(Long loanId) {
        Loan loanFound = loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Emprunt introuvable"));

        if (loanFound.getStatus() == LoanStatus.RETURNED) {
            throw new ConflictException("Retour déjà enregistré");
        }

        if (this.IsLoanReturnDelayed(loanFound)) {
            loanFound.setDueDate(LocalDate.now());
        }

        BookCopy bookCopy = loanFound.getBookCopyLoaned();
        bookCopy.setBookStatus(BookStatus.AVAILABLE);
        bookCopyRepository.saveAndFlush(bookCopy);

        loanFound.setReturnDate(LocalDate.now());
        loanFound.setStatus(LoanStatus.RETURNED);
        loanRepository.save(loanFound);

        reservationService.promote(
                bookCopy.getBook().getId(),
                bookCopy.getId(),
                BookStatus.AVAILABLE
        );
    }

// --- Utils ---

    /*
    Method in charge to determinate if a loan is returned in time.
    @loan Object Loan you want to test.
     */
    public boolean IsLoanReturnDelayed(Loan loan) {
        LocalDate loanDate = loan.getLoanDate();
        LocalDate dueDate = loanDate.plusDays(14);
        LocalDate now = LocalDate.now();

        return now.isAfter(dueDate);
    }
}
