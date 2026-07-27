package fr.eni.bookhub.review.service;

import fr.eni.bookhub.exception.custom.ResourceNotFoundException;
import fr.eni.bookhub.review.dto.request.create.CreateReviewDTO;
import fr.eni.bookhub.review.dto.response.create.CreateReviewResponse;
import fr.eni.bookhub.security.AuthenticatedUserProvider;
import fr.eni.bookhub.user.dao.IUserDao;
import fr.eni.bookhub.user.entity.User;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ReviewService {

    private AuthenticatedUserProvider authenticatedUserProvider;
    private IUserDao userDao;

    @PreAuthorize("isAuthenticated()")
    @Transactional
    public CreateReviewResponse createReview(CreateReviewDTO createReviewDTO) {
        User currentUser = authenticatedUserProvider.getCurrentUser();
        return null;
    }
}
