package fr.eni.bookhub.loan.repository;

import fr.eni.bookhub.bookcopy.entity.BookCopy;
import fr.eni.bookhub.loan.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findByLoanerId(long loanerId);
    boolean existsByLoanerIdAndBookCopyLoanedBookId(long loanerId, long bookCopyLoanedBookId);
}
