package fr.eni.bookhub.book.dto.response;

import fr.eni.bookhub.auth.dto.response.RegisterResponse;
import fr.eni.bookhub.book.entity.Book;
import fr.eni.bookhub.bookcopy.entity.BookStatus;
import fr.eni.bookhub.genre.entity.Genre;
import fr.eni.bookhub.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
public class BookResponse {

    private final Long id;
    private final String title;
    private final String img;
    private final String description;
    private final String isbn;
    private final List<String> genres;
    private final LocalDate publishDate;
    private final List<String> author;
    private final boolean available;

    public static BookResponse fromBookEntity(Book book) {
        return new BookResponse(
                book.getId(),
                book.getTitle(),
                book.getImg(),
                book.getDescription(),
                book.getIsbn(),
                book.getGenres() == null ? null : book.getGenres().stream()
                        .map(Genre::getLabel)
                        .toList(),
                book.getPublishDate(),
                book
                        .getAuthors()
                        .stream()
                        .map(author ->
                                author.getPenName() != null ?
                                        author.getPenName() :
                                        author.getFirstname() + " " + author.getLastname())
                        .toList(),
                book.getCopies().stream()
                        .anyMatch(copy -> copy.getBookStatus() == BookStatus.AVAILABLE)
        );
    }
}
