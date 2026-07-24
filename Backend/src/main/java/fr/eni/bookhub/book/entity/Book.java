package fr.eni.bookhub.book.entity;

import fr.eni.bookhub.bookcopy.entity.BookCopy;
import fr.eni.bookhub.genre.entity.Genre;
import fr.eni.bookhub.author.entity.Author;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "books")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder


public class Book {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(length=80, nullable=false)
    private String title;

    @Column(length=20, nullable=false)
    private String isbn;

    @Column(length=500, nullable=false)
    private String img;

    @Column(nullable=false)
    private LocalDate publishDate;

    @Column(length=2000, nullable=false)
    private String description;

    @ManyToMany
    @JoinTable(
            name = "book_genre",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private Set<Genre> genres = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "book_author",
            joinColumns = @JoinColumn(name = "book_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "author_id")
    )
    private Set<Author> authors = new HashSet<>();

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<BookCopy> copies = new HashSet<>();


}