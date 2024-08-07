package com.sparta.goodbite.domain.review.service;

import com.sparta.goodbite.common.UserCredentials;
import com.sparta.goodbite.domain.customer.entity.Customer;
import com.sparta.goodbite.domain.restaurant.entity.Restaurant;
import com.sparta.goodbite.domain.restaurant.repository.RestaurantRepository;
import com.sparta.goodbite.domain.review.dto.CreateReviewRequestDto;
import com.sparta.goodbite.domain.review.dto.ReviewResponseDto;
import com.sparta.goodbite.domain.review.dto.UpdateReviewRequestDto;
import com.sparta.goodbite.domain.review.entity.Review;
import com.sparta.goodbite.domain.review.repository.ReviewRepository;
import com.sparta.goodbite.exception.auth.AuthErrorCode;
import com.sparta.goodbite.exception.auth.AuthException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ReviewService {

    private final RestaurantRepository restaurantRepository;
    private final ReviewRepository reviewRepository;

    @Transactional
    public void createReview(CreateReviewRequestDto createReviewRequestDto, UserCredentials user) {
        Restaurant restaurant = restaurantRepository.findByIdOrThrow(
            createReviewRequestDto.getRestaurantId());
        reviewRepository.save(createReviewRequestDto.toEntity(restaurant, (Customer) user));
    }

    @Transactional(readOnly = true)
    public ReviewResponseDto getReview(Long reviewId) {
        return ReviewResponseDto.from(reviewRepository.findByIdOrThrow(reviewId));
    }

    @Transactional(readOnly = true)
    public List<ReviewResponseDto> getAllReviews() {
        return reviewRepository.findAll().stream().map(ReviewResponseDto::from).toList();
    }

    @Transactional(readOnly = true)
    public List<ReviewResponseDto> getAllReviewsByRestaurantId(Long restaurantId) {
        return reviewRepository.findAllByRestaurantId(restaurantId).stream()
            .map(ReviewResponseDto::from).toList();
    }

    @Transactional
    public void updateReview(Long reviewId, UpdateReviewRequestDto updateReviewRequestDto,
        UserCredentials user) {

        Review review = getReviewByIdAndValidateCustomer(reviewId, user);
        review.update(updateReviewRequestDto);
    }

    @Transactional
    public void deleteReview(Long reviewId, UserCredentials user) {
        Review review = getReviewByIdAndValidateCustomer(reviewId, user);
        reviewRepository.delete(review);
    }

    private Review getReviewByIdAndValidateCustomer(Long reviewId, UserCredentials user) {
        Review review = reviewRepository.findByIdOrThrow(reviewId);

        if (!review.getCustomer().getId().equals(user.getId())) {
            throw new AuthException(AuthErrorCode.UNAUTHORIZED);
        }

        return review;
    }
}