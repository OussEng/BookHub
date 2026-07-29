package fr.eni.bookhub.loan.service;

import fr.eni.bookhub.bookcopy.dao.IBookCopyDao;
import fr.eni.bookhub.bookcopy.entity.BookCopy;
import fr.eni.bookhub.bookcopy.entity.BookStatus;
import fr.eni.bookhub.bookcopy.repository.BookCopyRepository;
import fr.eni.bookhub.bookcopy.service.BookCopyService;
import fr.eni.bookhub.exception.custom.LoanException;
import fr.eni.bookhub.loan.dao.ILoanDao;
import fr.eni.bookhub.loan.dto.response.LoanDTO;
import fr.eni.bookhub.loan.entity.Loan;
import fr.eni.bookhub.loan.entity.LoanStatus;
import fr.eni.bookhub.security.AuthenticatedUserProvider;
import fr.eni.bookhub.user.entity.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class LoanService {

    private final ILoanDao loanRepository;
    private final AuthenticatedUserProvider authenticatedUserProvider;
    private final BookCopyService bookCopyService;
    private final IBookCopyDao bookCopyRepository;


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

        if (loanRepository.findById(id) == null) {
            throw new LoanException("Loan not found");
        }

        return new LoanDTO(loanRepository.findById(id));

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

        if (loanRepository.findById(id) == null) {
            throw new LoanException("Loan not found");
        }

        return loanRepository.findById(id);

    }

    /*
    Method in charge to make a new loan if the book searched isn't loan.
    @id : id of the book copy you want to loan.
     */
    public void createLoan(Long bookId) {
        User currentUser = authenticatedUserProvider.getCurrentUser();
        List<BookCopy> bookCopyFoundList = bookCopyRepository.findByBookIdAndBookStatus(bookId, BookStatus.AVAILABLE);
        int listSize = bookCopyFoundList.size();
        System.out.println(bookCopyFoundList);

        if (listSize == 0) {
            throw new LoanException("Book copy not available");
        }

        BookCopy bookCopy = bookCopyFoundList.get(listSize - 1);

        if (!bookCopyService.canBeLoaned(bookCopy)) {
            throw new LoanException("Book already loaned");
        }

        if (loanRepository.countByLoanerIdAndStatus(currentUser.getId(), LoanStatus.ACTIVE) >= 5) {
            throw new LoanException("Maximum books limit reached");
        }

        bookCopy.setBookStatus(BookStatus.LOANED);
        bookCopyRepository.save(bookCopy);

        Loan newLoan = Loan.builder()
                .loaner(currentUser)
                .bookCopyLoaned(bookCopy)
                .loanDate(LocalDate.now())
                .status(LoanStatus.ACTIVE)
                .build();
        loanRepository.save(newLoan);
    }

    /*
    Method in charge to declare to return a loan when the book return to the library.
    @id : id of the loan you want to close.
     */
    public void returnLoan(Long loanId) {
        Loan loanFound = this.getLoanEntityById(loanId);

        if (loanFound == null) {
            throw new LoanException("Loan not found");
        }

        if (loanFound.getStatus().equals(LoanStatus.RETURN) || loanFound.getReturnDate() != null) {
            throw new LoanException("Loan already returned");
        }

        if (this.IsLoanReturnDelayed(loanFound)) {
            loanFound.setDueDate(LocalDate.now());
        }

        loanFound.setReturnDate(LocalDate.now());
        loanFound.setStatus(LoanStatus.RETURN);
        loanRepository.save(loanFound);

    }

// --- Utils ---

    /*
    Method in charge to determinate if a loan is returned in time.
    @loan Object Loan you want to test.
     */
    public boolean IsLoanReturnDelayed(Loan loan) {
        LocalDate dateEmprunt = loan.getLoanDate();
        LocalDate dateReturn = dateEmprunt.plusDays(14);

        return loan.getReturnDate().isAfter(dateReturn);
    }
}
