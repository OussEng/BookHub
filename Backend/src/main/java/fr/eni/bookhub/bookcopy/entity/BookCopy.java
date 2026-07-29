package fr.eni.bookhub.bookcopy.entity;

import fr.eni.bookhub.book.entity.Book;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "book_copies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class BookCopy {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(length=80, nullable=false)
    private String serialNumber;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookStatus bookStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "book_condition", nullable = false)
    private Condition condition;



}
