package com.sparta.goodbite.domain.review.dto;

import com.sparta.goodbite.domain.review.entity.Review;

public record ReviewResponseDto(Long reviewId, Long restaurantId, float rating, String content) {

    public static ReviewResponseDto from(Review review) {
        return new ReviewResponseDto(
            review.getId(),
            review.getRestaurant().getId(),
            review.getRating(),
            review.getContent());
    }
}