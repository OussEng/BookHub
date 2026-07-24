package fr.eni.bookhub.loan.services;

import fr.eni.bookhub.loan.dao.ILoanDao;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class LoanService {

    private final ILoanDao loanRepository;
}
