package fr.eni.bookhub.reservation.entity;

import fr.eni.bookhub.book.entity.Book;
import fr.eni.bookhub.bookcopy.entity.BookCopy;
import fr.eni.bookhub.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // requis par JPA
@Table(name = "reservation")
public class Reservation {

    @Id
    @Setter(AccessLevel.NONE)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    private Long id;

    @Column(name = "reserves_date", nullable = false)
    private LocalDateTime reservesDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "reservation_status", nullable = false)
    private ReservationStatus status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "users_id", nullable = false)
    private User user;

     @ManyToOne(fetch = FetchType.LAZY, optional = false)
     @JoinColumn(name = "book_id", nullable = false)
     private Book book;

     @ManyToOne(fetch = FetchType.LAZY)
     @JoinColumn(name = "book_copy_id")
     private BookCopy bookCopy;

    @Column(name = "notified_at")
    private LocalDateTime notifiedAt;

    @Column(name = "pickup_deadline")
    private LocalDateTime pickupDeadline;

    // Seule façon légitime de créer une réservation : elle entre en file d'attente
    public Reservation(User user, Book book) {
        this.user = user;
        this.book = book;
        this.status = ReservationStatus.PENDING;
        // UTC partout : promote() écrit notifiedAt et pickupDeadline en UTC.
        // Deux origines d'heure = ordre de file faussé et fenêtre de retrait décalée.
        this.reservesDate = LocalDateTime.now(ZoneOffset.UTC);
    }

}
