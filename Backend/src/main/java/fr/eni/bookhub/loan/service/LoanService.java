package fr.eni.bookhub.loan.service;

import fr.eni.bookhub.exception.custom.LoanException;
import fr.eni.bookhub.loan.dao.ILoanDao;
import fr.eni.bookhub.loan.entity.Loan;
import fr.eni.bookhub.loan.entity.LoanStatus;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class LoanService {

    private final ILoanDao loanRepository;

    public List<Loan> findAll() {
        return loanRepository.findAll();
    }

    public Loan findLoanById(Long id) {

        if (loanRepository.findById(id) != null) {
            return loanRepository.findById(id);
        } else {
            throw new LoanException("Loan not found");
        }

    }

    public void returnLoan(Long id) {
        Loan loanfound = this.findLoanById(id);


        if (loanfound != null) {
            if (loanfound.getStatus().equals(LoanStatus.RETURN) || loanfound.getReturnDate() != null) {
                throw new LoanException("Loan already returned");
            }


        } else {
            throw new LoanException("Loan not found");
        }

    }
}
