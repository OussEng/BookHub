package fr.eni.bookhub.review.dao;

import fr.eni.bookhub.review.entity.Review;

public interface IReviewDao {

    Review save(Review review);
}
