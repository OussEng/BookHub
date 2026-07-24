package fr.eni.bookhub.bookcopy.entity;

import fr.eni.bookhub.book.entity.Book;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "bookCopies")
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



}
