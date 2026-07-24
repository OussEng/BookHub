package fr.eni.bookhub.review.service;

import fr.eni.bookhub.review.dto.request.create.CreateReviewDTO;
import fr.eni.bookhub.review.entity.Review;
import fr.eni.bookhub.review.repository.ReviewRepository;
import fr.eni.bookhub.user.entity.User;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
public class ReviewService {

    @PreAuthorize("isAuthenticated()")
    public Review createReview(Long id, CreateReviewDTO createReviewDTO) {

    }
}
