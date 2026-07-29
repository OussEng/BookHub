package fr.eni.bookhub.loan.service;

import fr.eni.bookhub.bookcopy.entity.BookCopy;
import fr.eni.bookhub.bookcopy.repository.BookCopyRepository;
import fr.eni.bookhub.bookcopy.service.BookCopyService;
import fr.eni.bookhub.exception.custom.ConflictException;
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
    private final BookCopyRepository bookCopyRepository;

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
            throw new ConflictException("Loan not found");
        }

        return new LoanDTO(loanRepository.findById(id));

    }

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
            throw new ConflictException("Loan not found");
        }

        return loanRepository.findById(id);

    }

    /*
    Method in charge to make a new loan if the book searched isn't loan.
    @id : id of the loan you want to find.
     */
    public void createLoan(Long id) {
        User currentUser = authenticatedUserProvider.getCurrentUser();
        BookCopy bookCopy = bookCopyRepository.findById(id).orElseThrow(() -> new ConflictException("Book Copy not found"));

        if (bookCopyService.canBeLoaned(bookCopy)) {
            throw new ConflictException("Book already loaned");
        }

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
    public LoanDTO returnLoan(Long id) {
        Loan loanFound = this.getLoanEntityById(id);

        if (loanFound == null) {
            throw new ConflictException("Loan not found");
        }

        if (loanFound.getStatus().equals(LoanStatus.RETURN) || loanFound.getReturnDate() != null) {
            throw new ConflictException("Loan already returned");
        }

        loanFound.setReturnDate(LocalDate.now());

        if (this.IsLoanReturnDelayed(loanFound) && loanFound.getDueDate() == null) {
            loanFound.setDueDate(LocalDate.now());
            return new LoanDTO(loanFound);
        } else {
            loanFound.setStatus(LoanStatus.RETURN);
            loanRepository.save(loanFound);
            return null;
        }


    }

    /*
    Method in charge to determinate if a loan is returned in time.
    @loan Object Loan you want to test.
     */
    public boolean IsLoanReturnDelayed(Loan loan) {
        LocalDate dateEmprunt = loan.getLoanDate();
        LocalDate dateReturn = dateEmprunt.plusDays(14);

        if (loan.getReturnDate().isAfter(dateReturn)) {
            return true;
        }
        return false;
    }
}
