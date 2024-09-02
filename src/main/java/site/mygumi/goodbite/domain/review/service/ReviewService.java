package site.mygumi.goodbite.domain.review.service;

import site.mygumi.goodbite.common.UserCredentials;
import site.mygumi.goodbite.domain.review.dto.ReviewResponseDto;
import java.util.List;

public interface ReviewService<T, U> {

    void createReview(T dto, UserCredentials user);

    ReviewResponseDto getReview(Long reviewId);

    List<ReviewResponseDto> getAllReviews();

    List<ReviewResponseDto> getAllReviewsByRestaurantId(Long restaurantId);

    List<ReviewResponseDto> getMyReviews(UserCredentials user);

    void updateReview(Long reviewId, U dto, UserCredentials user);

    void deleteReview(Long reviewId, UserCredentials user);
}