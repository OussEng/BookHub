package fr.eni.bookhub.genre.entity;

import fr.eni.bookhub.book.entity.Book;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "genres")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Genre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 80, nullable = false)
    private String label;

    @ManyToMany(mappedBy = "genres")
    private Set<Book> books = new HashSet<>();
}