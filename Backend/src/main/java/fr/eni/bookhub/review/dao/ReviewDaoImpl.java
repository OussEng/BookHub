package fr.eni.bookhub.review.dao;

import fr.eni.bookhub.review.entity.Review;
import fr.eni.bookhub.review.repository.ReviewRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ReviewDaoImpl implements IReviewDao {

    private final ReviewRepository reviewRepository;

    @Override
    public Review save(Review review) {
        return reviewRepository.save(review);
    }
}
