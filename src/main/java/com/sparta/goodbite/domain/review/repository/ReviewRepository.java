package com.sparta.goodbite.domain.review.repository;

import com.sparta.goodbite.domain.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    default Review findByIdOrThrow(Long reviewId) {
        return findById(reviewId).orElseThrow(() -> new RuntimeException("임시 메시지"));
    }
}