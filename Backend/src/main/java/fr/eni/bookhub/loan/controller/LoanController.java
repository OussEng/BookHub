package fr.eni.bookhub.loan.controller;

import fr.eni.bookhub.loan.dto.response.LoanDTO;
import fr.eni.bookhub.loan.service.LoanService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
@AllArgsConstructor
public class LoanController {

    private final LoanService loanService;

    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    @GetMapping("")
    public ResponseEntity<List<LoanDTO>> findAll() {
        return ResponseEntity.ok().body(loanService.findAll());
    }

    @GetMapping("/my")
    public ResponseEntity<List<LoanDTO>> findAllMy() {
        return ResponseEntity.ok().body(loanService.findLoansByAuthenticatedUser());
    }

    @PostMapping("/{id}/borrow")
    public ResponseEntity<Void> createLoan(@PathVariable Long id) {
        loanService.createLoan(id);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    @PutMapping("/{id}/return")
    public ResponseEntity<LoanDTO> returnLoans(@PathVariable Long id) {
        loanService.returnLoan(id);
        return ResponseEntity.noContent().build();
    }
}
