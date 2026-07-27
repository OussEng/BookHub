package fr.eni.bookhub.loan.controller;

import fr.eni.bookhub.loan.dto.response.LoanDTO;
import fr.eni.bookhub.loan.service.LoanService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
@AllArgsConstructor
public class LoanController {

    private final LoanService loanService;

    @GetMapping("")
    public ResponseEntity<List<LoanDTO>> findAll() {
        return ResponseEntity.ok().body(loanService.findAll());
    }

    @GetMapping("/my")
    public ResponseEntity<List<LoanDTO>> findAllMy() {
        return ResponseEntity.ok().body(loanService.findLoansByAuthenticatedUser());
    }

    @PutMapping("/{id}/return") // En tant que LIBRARIAN
    public ResponseEntity<LoanDTO> returnLoans(@PathVariable Long id) {

        LoanDTO returnLoan = loanService.returnLoan(id);

        if (returnLoan != null) {
            return ResponseEntity.ok().body(returnLoan);
        }
        return ResponseEntity.ok().build();
    }
}
