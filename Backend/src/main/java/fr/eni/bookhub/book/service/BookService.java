package fr.eni.bookhub.book.service;


import fr.eni.bookhub.author.dao.IAuthorDao;
import fr.eni.bookhub.author.entity.Author;
import fr.eni.bookhub.book.dao.IBookDao;
import fr.eni.bookhub.book.dto.request.create.BookCreateRequest;
import fr.eni.bookhub.book.dto.response.BookResponse;
import fr.eni.bookhub.book.entity.Book;
import fr.eni.bookhub.exception.custom.FileStorageException;
import fr.eni.bookhub.exception.custom.InvalidFileException;
import fr.eni.bookhub.genre.dao.IGenreDao;
import fr.eni.bookhub.genre.entity.Genre;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Path;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;


@RequiredArgsConstructor
@Service
public class BookService {

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Value("${app.base-url}")
    private String baseUrl;


    private final IGenreDao genreDao;
    private final IAuthorDao authorDao;

    private final IBookDao bookRepository;

    public List<BookResponse> getAllBooks() {
        List<Book> books = bookRepository.findAll();
        return books.stream()
                .map(BookResponse::fromBookEntity)
                .toList();
    }

    public BookResponse getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Livre non trouvé"));
        return BookResponse.fromBookEntity(book);
    }


    @Transactional
    public BookResponse createBook(BookCreateRequest dto, MultipartFile image) {
        List<Author> authors = authorDao.findAllById(dto.getAuthorIds());
        List<Genre> genres = genreDao.findAllById(dto.getGenreIds());

        if (authors.size() != dto.getAuthorIds().size()) {
            throw new EntityNotFoundException("One or more authors not found");
        }
        if (genres.size() != dto.getGenreIds().size()) {
            throw new EntityNotFoundException("One or more genres not found");
        }

        Book book = new Book();
        book.setTitle(dto.getTitle());
        book.setIsbn(dto.getIsbn());
        book.setDescription(dto.getDescription());
        book.setPublishDate(dto.getPublishDate());
        book.setAuthors(authors);
        book.setGenres(genres);

        if (image != null && !image.isEmpty()) {
            String imageUrl = storeImage(image);
            book.setImg(imageUrl);
        }

        Book saved = bookRepository.save(book);
        return BookResponse.fromBookEntity(saved);
    }

    private String storeImage(MultipartFile file) {
        try {
            validateImage(file);

            String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
            String filename = UUID.randomUUID() + "." + extension;

            Path targetDir = Paths.get(uploadDir);
            Files.createDirectories(targetDir);

            Path targetPath = targetDir.resolve(filename);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            return baseUrl + "/uploads/books/" + filename;
        } catch (IOException e) {
            throw new FileStorageException("Failed to store image", e);
        }
    }

    private void validateImage(MultipartFile file) {
        List<String> allowed = List.of("image/jpeg", "image/png", "image/webp");
        if (!allowed.contains(file.getContentType())) {
            throw new InvalidFileException("Unsupported image type: " + file.getContentType());
        }
        long maxBytes = 5 * 1024 * 1024;
        if (file.getSize() > maxBytes) {
            throw new InvalidFileException("Image too large (max 5MB)");
        }
    }
}
