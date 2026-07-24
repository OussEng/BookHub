package fr.eni.bookhub.loan.controller;

import fr.eni.bookhub.loan.entity.Loan;
import fr.eni.bookhub.loan.services.LoanService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loan")
@AllArgsConstructor
public class LoanController {

    private final LoanService loanService;

    @GetMapping("")
    public List<Loan> findAll() {
        return loanService.findAll();
    }

    @GetMapping("/my")

    @PutMapping("/{id}/return") // En tant que LIBRARIAN
    public void returnLoans(@PathVariable Long id) {
        loanService.returnLoan(id);
    }
}
