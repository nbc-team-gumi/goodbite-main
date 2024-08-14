package com.sparta.goodbite.domain.review.repository;

import com.sparta.goodbite.domain.review.entity.WaitingReview;
import com.sparta.goodbite.exception.review.ReviewErrorCode;
import com.sparta.goodbite.exception.review.detail.ReviewNotFoundException;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WaitingReviewRepository extends JpaRepository<WaitingReview, Long> {

    List<WaitingReview> findAllByRestaurantId(Long restaurantId);

    List<WaitingReview> findAllByCustomerId(Long customerId);

    default WaitingReview findByIdOrThrow(Long waitingReviewId) {
        return findById(waitingReviewId).orElseThrow(
            () -> new ReviewNotFoundException(ReviewErrorCode.REVIEW_NOT_FOUND));
    }
}