package com.sparta.goodbite.domain.review.dto;

import com.sparta.goodbite.domain.review.entity.Review;
import java.time.LocalDateTime;

public record ReviewResponseDto(Long reviewId, String nickname, Long restaurantId,
                                String restaurantName, float rating, String content,
                                LocalDateTime createdAt) {

    public static ReviewResponseDto from(Review review) {
        return new ReviewResponseDto(
            review.getId(),
            review.getCustomer().getNickname(),
            review.getRestaurant().getId(),
            review.getRestaurant().getName(),
            review.getRating(),
            review.getContent(),
            review.getCreatedAt());
    }
}