package com.sparta.goodbite.domain.review.service;

import com.sparta.goodbite.common.UserCredentials;
import com.sparta.goodbite.domain.customer.entity.Customer;
import com.sparta.goodbite.domain.restaurant.entity.Restaurant;
import com.sparta.goodbite.domain.restaurant.repository.RestaurantRepository;
import com.sparta.goodbite.domain.review.dto.CreateWaitingReviewRequestDto;
import com.sparta.goodbite.domain.review.dto.ReviewResponseDto;
import com.sparta.goodbite.domain.review.dto.UpdateReviewRequestDto;
import com.sparta.goodbite.domain.review.entity.Review;
import com.sparta.goodbite.domain.review.entity.WaitingReview;
import com.sparta.goodbite.domain.review.repository.WaitingReviewRepository;
import com.sparta.goodbite.domain.waiting.entity.Waiting;
import com.sparta.goodbite.domain.waiting.repository.WaitingRepository;
import com.sparta.goodbite.exception.auth.AuthErrorCode;
import com.sparta.goodbite.exception.auth.AuthException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class WaitingReviewServiceImpl implements
    ReviewService<CreateWaitingReviewRequestDto, UpdateReviewRequestDto> {

    private final RestaurantRepository restaurantRepository;
    private final WaitingRepository waitingRepository;
    private final WaitingReviewRepository waitingReviewRepository;

    @Override
    @Transactional
    public void createReview(CreateWaitingReviewRequestDto createWaitingReviewRequestDto,
        UserCredentials user) {
        Restaurant restaurant = restaurantRepository.findByIdOrThrow(
            createWaitingReviewRequestDto.getRestaurantId());
        Waiting waiting = waitingRepository.findByIdOrThrow(
            createWaitingReviewRequestDto.getWaitingId());
        waitingReviewRepository.save(
            createWaitingReviewRequestDto.toEntity(restaurant, (Customer) user, waiting));
    }

    @Override
    @Transactional(readOnly = true)
    public ReviewResponseDto getReview(Long reviewId) {
        return ReviewResponseDto.from(waitingReviewRepository.findByIdOrThrow(reviewId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponseDto> getAllReviews() {
        return waitingReviewRepository.findAll().stream().map(ReviewResponseDto::from).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponseDto> getAllReviewsByRestaurantId(Long restaurantId) {
        return waitingReviewRepository.findAllByRestaurantId(restaurantId).stream()
            .map(ReviewResponseDto::from).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponseDto> getMyReviews(UserCredentials user) {
        return waitingReviewRepository.findAllByCustomerId(user.getId()).stream()
            .map(ReviewResponseDto::from).toList();
    }

    @Override
    @Transactional
    public void updateReview(Long reviewId, UpdateReviewRequestDto updateReviewRequestDto,
        UserCredentials user) {

        Review review = getReviewByIdAndValidateCustomer(reviewId, user.getId());
        review.update(updateReviewRequestDto);
    }

    @Override
    @Transactional
    public void deleteReview(Long reviewId, UserCredentials user) {
        WaitingReview waitingReview = getReviewByIdAndValidateCustomer(reviewId, user.getId());
        waitingReviewRepository.delete(waitingReview);
    }

    private WaitingReview getReviewByIdAndValidateCustomer(Long reviewId, Long customerId) {
        WaitingReview waitingReview = waitingReviewRepository.findByIdOrThrow(reviewId);

        if (!waitingReview.getCustomer().getId().equals(customerId)) {
            throw new AuthException(AuthErrorCode.UNAUTHORIZED);
        }

        return waitingReview;
    }
}