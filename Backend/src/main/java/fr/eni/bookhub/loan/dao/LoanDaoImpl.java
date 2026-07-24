package fr.eni.bookhub.loan.dao;

import fr.eni.bookhub.loan.entity.Loan;
import fr.eni.bookhub.loan.repository.LoanRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class LoanDaoImpl implements ILoanDao{

    private final LoanRepository loanRepository;

    public List<Loan> findAll() {
        return loanRepository.findAll();
    }

    public Loan findById(long id) {
        return loanRepository.findById(id).get();
    }
}
