package fr.eni.bookhub.loan.entity;

import fr.eni.bookhub.bookcopy.entity.BookCopy;
import fr.eni.bookhub.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "loans")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Date loanDate;

    private Date dueDate;

    private Date returnDate;

    @Enumerated
    @Column(nullable = false)
    private LoanStatus status;

    @ManyToOne
    private User loaner;

    @ManyToOne
    private BookCopy bookLoaned;

}
