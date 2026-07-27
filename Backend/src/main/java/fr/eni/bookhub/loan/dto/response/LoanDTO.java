package fr.eni.bookhub.loan.dto.response;

import fr.eni.bookhub.loan.entity.Loan;
import fr.eni.bookhub.loan.entity.LoanStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class LoanDTO {

    private Long id;
    private Long bookCopyId;
    private Long userId;
    private LocalDate loanDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private LoanStatus status;

    public LoanDTO(Loan loan) {
        this.id = loan.getId();
        this.bookCopyId = loan.getBookCopyLoaned().getId();
        this.userId = loan.getLoaner().getId();
        this.loanDate = loan.getLoanDate();
        this.dueDate = loan.getDueDate();
        this.returnDate = loan.getReturnDate();
        this.status = loan.getStatus();
    }
}
