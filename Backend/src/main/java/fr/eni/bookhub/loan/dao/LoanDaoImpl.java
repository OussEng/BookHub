package fr.eni.bookhub.loan.dao;

import fr.eni.bookhub.loan.repository.LoanRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class LoanDaoImpl implements ILoanDao{

    private final LoanRepository loanRepository;
}
