package com.sparta.goodbite.domain.review.service;

import com.sparta.goodbite.common.UserCredentials;
import com.sparta.goodbite.domain.customer.entity.Customer;
import com.sparta.goodbite.domain.reservation.entity.Reservation;
import com.sparta.goodbite.domain.reservation.repository.ReservationRepository;
import com.sparta.goodbite.domain.restaurant.entity.Restaurant;
import com.sparta.goodbite.domain.restaurant.repository.RestaurantRepository;
import com.sparta.goodbite.domain.review.dto.CreateReservationReviewRequestDto;
import com.sparta.goodbite.domain.review.dto.ReviewResponseDto;
import com.sparta.goodbite.domain.review.dto.UpdateReviewRequestDto;
import com.sparta.goodbite.domain.review.entity.ReservationReview;
import com.sparta.goodbite.domain.review.entity.Review;
import com.sparta.goodbite.domain.review.repository.ReservationReviewRepository;
import com.sparta.goodbite.exception.auth.AuthErrorCode;
import com.sparta.goodbite.exception.auth.AuthException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ReservationReviewServiceImpl implements
    ReviewService<CreateReservationReviewRequestDto, UpdateReviewRequestDto> {

    private final RestaurantRepository restaurantRepository;
    private final ReservationRepository reservationRepository;
    private final ReservationReviewRepository reservationReviewRepository;

    @Override
    @Transactional
    public void createReview(CreateReservationReviewRequestDto createReservationReviewRequestDto,
        UserCredentials user) {
        Restaurant restaurant = restaurantRepository.findByIdOrThrow(
            createReservationReviewRequestDto.getRestaurantId());
        Reservation reservation = reservationRepository.findByIdOrThrow(
            createReservationReviewRequestDto.getReservationId());
        reservationReviewRepository.save(
            createReservationReviewRequestDto.toEntity(restaurant, (Customer) user, reservation));
    }

    @Override
    @Transactional(readOnly = true)
    public ReviewResponseDto getReview(Long reviewId) {
        return ReviewResponseDto.from(reservationReviewRepository.findByIdOrThrow(reviewId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponseDto> getAllReviews() {
        return reservationReviewRepository.findAll().stream().map(ReviewResponseDto::from).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponseDto> getAllReviewsByRestaurantId(Long restaurantId) {
        return reservationReviewRepository.findAllByRestaurantId(restaurantId).stream()
            .map(ReviewResponseDto::from).toList();
    }

    @Override
    public List<ReviewResponseDto> getMyReviews(UserCredentials user) {
        return reservationReviewRepository.findAllByCustomerId(user.getId()).stream()
            .map(ReviewResponseDto::from).toList();
    }

    @Override
    public void updateReview(Long reviewId, UpdateReviewRequestDto updateReviewRequestDto,
        UserCredentials user) {

        Review review = getReviewByIdAndValidateCustomer(reviewId, user.getId());
        review.update(updateReviewRequestDto);
    }

    @Override
    public void deleteReview(Long reviewId, UserCredentials user) {
        ReservationReview reservationReview = getReviewByIdAndValidateCustomer(reviewId,
            user.getId());
        reservationReviewRepository.delete(reservationReview);
    }

    private ReservationReview getReviewByIdAndValidateCustomer(Long reviewId, Long customerId) {
        ReservationReview reservationReview = reservationReviewRepository.findByIdOrThrow(reviewId);

        if (!reservationReview.getCustomer().getId().equals(customerId)) {
            throw new AuthException(AuthErrorCode.UNAUTHORIZED);
        }

        return reservationReview;
    }
}