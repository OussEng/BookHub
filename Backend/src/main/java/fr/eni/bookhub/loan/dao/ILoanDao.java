package fr.eni.bookhub.loan.dao;

import fr.eni.bookhub.loan.entity.Loan;
import fr.eni.bookhub.loan.entity.LoanStatus;

import java.util.List;

public interface ILoanDao {

    List<Loan> findAll();

    Loan findById(long id);

    void save(Loan loan);

    List<Loan> findByLoanerId(long loanerId);

    boolean existsByLoanerIdAndBookCopyLoanedBookId(long loanerId, long bookCopyLoanedBookId);

    int countByLoanerIdAndStatus(long loanerId, LoanStatus status);

    boolean existsByLoanerIdAndBookCopyLoanedBookIdAndStatus(long loanerId, long bookCopyLoanedBookId, LoanStatus status);
}
