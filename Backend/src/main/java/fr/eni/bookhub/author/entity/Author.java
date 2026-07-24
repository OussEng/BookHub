package fr.eni.bookhub.author.entity;

import fr.eni.bookhub.book.entity.Book;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Check;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "authors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Check(constraints = "pen_name IS NOT NULL OR (lastname IS NOT NULL AND firstname IS NOT NULL)")

public class Author {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(length=20, nullable=true, name="firstname")
    private String firstname;

    @Column(length=20, nullable=true, name="lastname")
    private String lastname;

    @Column(length=20, nullable=true, name="pen_name")
    private String penName;

    @ManyToMany(mappedBy = "authors")
    private Set<Book> books = new HashSet<>();

}
