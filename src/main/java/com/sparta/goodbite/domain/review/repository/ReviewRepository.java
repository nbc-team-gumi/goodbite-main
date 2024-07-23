package com.sparta.goodbite.domain.review.repository;

import com.sparta.goodbite.domain.review.entity.Review;
import com.sparta.goodbite.exception.review.ReviewErrorCode;
import com.sparta.goodbite.exception.review.detail.ReviewNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    default Review findByIdOrThrow(Long reviewId) {
        return findById(reviewId).orElseThrow(
            () -> new ReviewNotFoundException(ReviewErrorCode.REVIEW_NOT_FOUND));
    }
}