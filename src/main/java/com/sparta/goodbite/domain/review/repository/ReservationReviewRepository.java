package com.sparta.goodbite.domain.review.repository;

import com.sparta.goodbite.domain.review.entity.ReservationReview;
import com.sparta.goodbite.exception.review.ReviewErrorCode;
import com.sparta.goodbite.exception.review.detail.ReviewNotFoundException;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationReviewRepository extends JpaRepository<ReservationReview, Long> {

    List<ReservationReview> findAllByRestaurantId(Long restaurantId);

    List<ReservationReview> findAllByCustomerId(Long customerId);

    default ReservationReview findByIdOrThrow(Long waitingReviewId) {
        return findById(waitingReviewId).orElseThrow(
            () -> new ReviewNotFoundException(ReviewErrorCode.REVIEW_NOT_FOUND));
    }
}