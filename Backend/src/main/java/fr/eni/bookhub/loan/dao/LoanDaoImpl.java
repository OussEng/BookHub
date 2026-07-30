package fr.eni.bookhub.loan.dao;

import fr.eni.bookhub.loan.entity.Loan;
import fr.eni.bookhub.loan.entity.LoanStatus;
import fr.eni.bookhub.loan.repository.LoanRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@AllArgsConstructor
public class LoanDaoImpl implements ILoanDao{

    private final LoanRepository loanRepository;

    public List<Loan> findAll() {
        return loanRepository.findAll();
    }

    public Optional<Loan> findById(long id) {
        return loanRepository.findById(id);
    }

    public void save(Loan loan) {
        loanRepository.save(loan);
    }

    public List<Loan> findByLoanerId(long loanerId) {
        return loanRepository.findByLoanerId(loanerId);
    }

    public boolean existsByLoanerIdAndBookCopyLoanedBookId(long loanerId, long bookCopyLoanedBookId) {
        return loanRepository.existsByLoanerIdAndBookCopyLoanedBookId(loanerId, bookCopyLoanedBookId);
    }

    public int countByLoanerIdAndStatus(long loanerId, LoanStatus status) {
        return loanRepository.countByLoanerIdAndStatus(loanerId, status);
    }

    public boolean existsByLoanerIdAndBookCopyLoanedBookIdAndStatus(long loanerId, long bookCopyLoanedBookId, LoanStatus loanStatus) {
        return loanRepository.existsByLoanerIdAndBookCopyLoanedBookIdAndStatus(loanerId, bookCopyLoanedBookId, loanStatus);
    }
}
