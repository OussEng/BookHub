package fr.eni.bookhub.unit.bookCopy;

import fr.eni.bookhub.book.dao.IBookDao;
import fr.eni.bookhub.book.entity.Book;
import fr.eni.bookhub.bookcopy.dao.IBookCopyDao;
import fr.eni.bookhub.bookcopy.dto.request.CreateBookCopyRequest;
import fr.eni.bookhub.bookcopy.dto.response.BookCopyResponse;
import fr.eni.bookhub.bookcopy.entity.BookCopy;
import fr.eni.bookhub.bookcopy.entity.BookStatus;
import fr.eni.bookhub.bookcopy.entity.Condition;
import fr.eni.bookhub.bookcopy.service.BookCopyService;
import fr.eni.bookhub.exception.custom.ConflictException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookCopyServiceTest {

    @Mock
    private IBookCopyDao bookCopyRepository;

    @Mock
    private IBookDao bookRepository;

    @InjectMocks
    private BookCopyService bookCopyService;

    private Book sampleBook;
    private BookCopy sampleCopy;

    @BeforeEach
    void setUp() {
        sampleBook = new Book();
        sampleBook.setId(1L);
        sampleBook.setTitle("Severance");

        sampleCopy = BookCopy.builder()
                .id(1L)
                .serialNumber("SN-12345")
                .book(sampleBook)
                .bookStatus(BookStatus.AVAILABLE)
                .condition(Condition.NEW)
                .build();
    }




    @Test
    void createBookCopy() {
        CreateBookCopyRequest request = new CreateBookCopyRequest();
        request.setBookId(1L);
        request.setSerialNumber("SN-12345");
        request.setCondition(Condition.NEW);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(sampleBook));
        when(bookCopyRepository.findBySerialNumber("SN-12345")).thenReturn(Optional.empty());
        when(bookCopyRepository.save(any(BookCopy.class))).thenReturn(sampleCopy);

        BookCopyResponse result = bookCopyService.createBookCopy(request);

        assertThat(result).isNotNull();
        assertThat(result.getSerialNumber()).isEqualTo("SN-12345");
        verify(bookCopyRepository, times(1)).save(any(BookCopy.class));
    }

    @Test
    void createBookCopy_Book_Not_Found() {
        CreateBookCopyRequest request = new CreateBookCopyRequest();
        request.setBookId(99L);

        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookCopyService.createBookCopy(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Livre non trouvé");
    }

    @Test
    void createBookCopy_Duplicate() {
        CreateBookCopyRequest request = new CreateBookCopyRequest();
        request.setBookId(1L);
        request.setSerialNumber("SN-12345");

        when(bookRepository.findById(1L)).thenReturn(Optional.of(sampleBook));
        when(bookCopyRepository.findBySerialNumber("SN-12345")).thenReturn(Optional.of(sampleCopy));

        assertThatThrownBy(() -> bookCopyService.createBookCopy(request))
                .isInstanceOf(ConflictException.class)
                .hasMessage("Cet exemplaire existe déjà.");
    }



    @Test
    void getCopiesByBookId() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<BookCopy> page = new PageImpl<>(List.of(sampleCopy));

        when(bookCopyRepository.findCopiesByBookIdWithFilters(1L, "SN-12345", Condition.NEW, pageable))
                .thenReturn(page);

        Page<BookCopyResponse> result = bookCopyService.getCopiesByBookId(1L, "SN-12345", Condition.NEW, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().getSerialNumber()).isEqualTo("SN-12345");
    }
}