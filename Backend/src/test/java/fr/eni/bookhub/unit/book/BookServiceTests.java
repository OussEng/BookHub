package fr.eni.bookhub.unit.book;

import fr.eni.bookhub.author.dao.IAuthorDao;
import fr.eni.bookhub.author.entity.Author;
import fr.eni.bookhub.author.service.AuthorService;
import fr.eni.bookhub.book.dao.IBookDao;
import fr.eni.bookhub.book.dto.request.create.BookCreateRequest;
import fr.eni.bookhub.book.dto.response.BookResponse;
import fr.eni.bookhub.book.entity.Book;
import fr.eni.bookhub.book.service.BookService;
import fr.eni.bookhub.genre.dao.IGenreDao;
import fr.eni.bookhub.genre.entity.Genre;
import fr.eni.bookhub.genre.service.GenreService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class BookServiceTests {

    @Mock
    private IGenreDao genreDao;

    @Mock
    private IAuthorDao authorDao;

    @Mock
    private IBookDao bookRepository;

    @Mock
    private AuthorService authorService;

    @Mock
    private GenreService genreService;

    @InjectMocks
    private BookService bookService;

    @TempDir
    Path tempDir;

    private Book book;
    private Author author;
    private Genre genre;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(bookService, "uploadDir", tempDir.toString());
        ReflectionTestUtils.setField(bookService, "baseUrl", "http://localhost:8080");

        author = Author.builder()
                .id(1L)
                .firstname("Author")
                .lastname("Author")
                .penName("Author")
                .build();

        genre = Genre.builder()
                .id(1L)
                .label("genre")
                .build();

        book = new Book();
        book.setId(1L);
        book.setTitle("severance");
        book.setIsbn("123456789");
        book.setDescription("novel");
        book.setPublishDate(LocalDate.of(2026, 1, 1));
        book.setAuthors(List.of(author));
        book.setGenres(List.of(genre));
    }

    @Test
    void getAllBooksTest() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Book> bookPage = new PageImpl<>(List.of(book));

        when(bookRepository.searchBooks("severance", 1L, pageable)).thenReturn(bookPage);

        Page<BookResponse> result = bookService.getAllBooks("severance", 1L, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().getTitle()).isEqualTo("severance");
    }



    @Test
    void getBookByIdTest() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        BookResponse result = bookService.getBookById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("severance");
    }

    @Test
    void getBookById_throws_Exception_Test() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.getBookById(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Livre non trouvé");
    }


    @Test
    void createBook_Without_Image_Test() {
        BookCreateRequest dto = new BookCreateRequest();
        dto.setTitle("severance");
        dto.setIsbn("123456789");
        dto.setDescription("novel");
        dto.setPublishDate(LocalDate.of(2026, 1, 1));
        dto.setAuthorIds(List.of(1L));
        dto.setGenreIds(List.of(1L));

        when(authorDao.findAllById(List.of(1L))).thenReturn(List.of(author));
        when(genreDao.findAllById(List.of(1L))).thenReturn(List.of(genre));
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        BookResponse result = bookService.createBook(dto, null);

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("severance");
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    void createBook_With_Image_Test() {
        BookCreateRequest dto = new BookCreateRequest();
        dto.setTitle("severance");
        dto.setAuthorIds(List.of(1L));
        dto.setGenreIds(List.of(1L));

        MockMultipartFile image = new MockMultipartFile(
                "image", "img.png", "image/png", "gg".getBytes()
        );

        when(authorDao.findAllById(List.of(1L))).thenReturn(List.of(author));
        when(genreDao.findAllById(List.of(1L))).thenReturn(List.of(genre));
        when(bookRepository.save(any(Book.class))).thenAnswer(inv -> inv.getArgument(0));

        BookResponse result = bookService.createBook(dto, image);

        assertThat(result).isNotNull();
        assertThat(result.getImg()).startsWith("http://localhost:8080/uploads/books/");
        assertThat(result.getImg()).endsWith(".png");
    }

}
