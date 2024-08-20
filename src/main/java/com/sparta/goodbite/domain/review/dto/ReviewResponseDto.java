package com.sparta.goodbite.domain.review.dto;

import com.sparta.goodbite.domain.review.entity.ReservationReview;
import com.sparta.goodbite.domain.review.entity.WaitingReview;
import java.time.LocalDateTime;

public record ReviewResponseDto(Long reviewId, String nickname, Long restaurantId,
                                String restaurantName, double rating, String content,
                                LocalDateTime createdAt) {

    public static ReviewResponseDto from(ReservationReview review) {
        return new ReviewResponseDto(
            review.getId(),
            review.getCustomer().getNickname(),
            review.getRestaurant().getId(),
            review.getRestaurant().getName(),
            review.getRating(),
            review.getContent(),
            review.getCreatedAt());
    }

    public static ReviewResponseDto from(WaitingReview review) {
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