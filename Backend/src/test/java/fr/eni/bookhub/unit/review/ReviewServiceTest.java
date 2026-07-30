package fr.eni.bookhub.unit.review;

import fr.eni.bookhub.book.dao.IBookDao;
import fr.eni.bookhub.book.entity.Book;
import fr.eni.bookhub.exception.custom.ConflictException;
import fr.eni.bookhub.exception.custom.ResourceNotFoundException;
import fr.eni.bookhub.loan.dao.ILoanDao;
import fr.eni.bookhub.loan.entity.LoanStatus;
import fr.eni.bookhub.review.dao.IReviewDao;
import fr.eni.bookhub.review.dto.request.ReviewRequest;
import fr.eni.bookhub.review.dto.response.ReviewResponse;
import fr.eni.bookhub.review.entity.Review;
import fr.eni.bookhub.review.entity.ReviewStatus;
import fr.eni.bookhub.review.service.ReviewService;
import fr.eni.bookhub.security.AuthenticatedUserProvider;
import fr.eni.bookhub.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceTest {

    @Mock
    private AuthenticatedUserProvider authenticatedUserProvider;

    @Mock
    private IBookDao bookDao;

    @Mock
    private ILoanDao loanDao;

    @Mock
    private IReviewDao reviewDao;

    @InjectMocks
    private ReviewService reviewService;

    private User user;
    private Book book;
    private Review review;
    private ReviewRequest request;
    private ReviewResponse response;

    @BeforeEach
    void init() {
        user = User.builder()
                .id(1L)
                .firstname("John")
                .lastname("Doe")
                .build();
        book = Book.builder()
                .id(1L)
                .build();
        request = new ReviewRequest(4, "New comment");
        review = Review.builder()
                .id(1L)
                .rating(4)
                .comment("New comment")
                .status(ReviewStatus.ACTIVE)
                .user(user)
                .book(book)
                .createdAt(LocalDateTime.now())
                .updatedAt(null)
                .moderatedAt(null)
                .build();
        response = ReviewResponse.fromEntity(review);
    }

    @Test
    void createReview_shouldThrowResourceNotFound_whenBookNotFound() {
        // GIVEN
        when(authenticatedUserProvider.getCurrentUser()).thenReturn(user);
        when(bookDao.findById(999L)).thenReturn(Optional.empty());

        // WHEN
        ResourceNotFoundException ex =  assertThrows(ResourceNotFoundException.class,
                () -> reviewService.createReview(999L, request));

        // THEN
        assertEquals("Ouvrage introuvable", ex.getMessage());
        verify(authenticatedUserProvider).getCurrentUser();
        verify(bookDao).findById(999L);
        verify(reviewDao, never()).existsByUserIdAndBookId(eq(1L), eq(999L));
        verify(loanDao, never()).existsByLoanerIdAndBookCopyLoanedBookIdAndStatus(eq(1L), eq(999L), any());
        verify(reviewDao, never()).save(any());
    }

    @Test
    void createReview_shouldThrowConflict_whenReviewAlreadyExists() {
        // GIVEN
        when(authenticatedUserProvider.getCurrentUser()).thenReturn(user);
        when(bookDao.findById(1L)).thenReturn(Optional.of(book));
        when(reviewDao.existsByUserIdAndBookId(1L, 1L)).thenReturn(true);

        // WHEN
        ConflictException ex = assertThrows(ConflictException.class,
                () -> reviewService.createReview(1L, request));

        // THEN
        assertEquals("Vous avez déjà laissé un avis pour ce livre, vous pouvez le modifier plutôt que d'en créer un nouveau", ex.getMessage());
        verify(authenticatedUserProvider).getCurrentUser();
        verify(bookDao).findById(1L);
        verify(reviewDao).existsByUserIdAndBookId(1L, 1L);
        verify(loanDao, never()).existsByLoanerIdAndBookCopyLoanedBookIdAndStatus(eq(1L), eq(999L), any());
        verify(reviewDao, never()).save(any());
    }

    @Test
    void createReview_shouldThrowAccessDenied_whenNotLoaner() {
        // GIVEN
        when(authenticatedUserProvider.getCurrentUser()).thenReturn(user);
        when(bookDao.findById(1L)).thenReturn(Optional.of(book));
        when(reviewDao.existsByUserIdAndBookId(1L, 1L)).thenReturn(false);
        when(loanDao.existsByLoanerIdAndBookCopyLoanedBookIdAndStatus(1L, 1L, LoanStatus.RETURNED))
                .thenReturn(false);

        // WHEN
        AccessDeniedException ex =  assertThrows(AccessDeniedException.class,
                () -> reviewService.createReview(1L, request));

        // THEN
        assertEquals("Vous ne pouvez pas noter cette ouvrage", ex.getMessage());
        verify(authenticatedUserProvider).getCurrentUser();
        verify(bookDao).findById(1L);
        verify(reviewDao).existsByUserIdAndBookId(1L, 1L);
        verify(loanDao).existsByLoanerIdAndBookCopyLoanedBookIdAndStatus(1L, 1L, LoanStatus.RETURNED);
        verify(reviewDao, never()).save(any());
    }

    @Test
    void createReview_shouldCreateAndReturnReview_whenLoaner() {
        // GIVEN
        when(authenticatedUserProvider.getCurrentUser()).thenReturn(user);
        when(bookDao.findById(1L)).thenReturn(Optional.of(book));
        when(reviewDao.existsByUserIdAndBookId(1L, 1L)).thenReturn(false);
        when(loanDao.existsByLoanerIdAndBookCopyLoanedBookIdAndStatus(1L, 1L, LoanStatus.RETURNED))
                .thenReturn(true);
        when(reviewDao.save(any(Review.class))).thenAnswer(
                invocation -> invocation.getArgument(0));

        // WHEN
        ReviewResponse result =  reviewService.createReview(1L, request);

        // THEN
        assertEquals(4, result.getRating());
        assertEquals("New comment", result.getComment());
        assertEquals(book.getId(), result.getBookId());
        assertEquals("John D.", result.getUsername());
        verify(authenticatedUserProvider).getCurrentUser();
        verify(bookDao).findById(1L);
        verify(reviewDao).existsByUserIdAndBookId(1L, 1L);
        verify(loanDao).existsByLoanerIdAndBookCopyLoanedBookIdAndStatus(1L, 1L, LoanStatus.RETURNED);
        verify(reviewDao).save(argThat(r ->
                r.getId() == null
                        && r.getRating() == 4
                        && r.getComment().equals("New comment")
                        && r.getStatus().equals(ReviewStatus.ACTIVE)
                        && r.getUser().equals(user)
                        && r.getBook().equals(book)
        ));
    }
}
