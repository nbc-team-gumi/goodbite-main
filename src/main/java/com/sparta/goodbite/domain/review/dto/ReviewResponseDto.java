package com.sparta.goodbite.domain.review.dto;

import com.sparta.goodbite.domain.menu.entity.Menu;
import com.sparta.goodbite.domain.review.entity.Review;

public record ReviewResponseDto(float rating, String content, Menu menu) {

    public static ReviewResponseDto from(Review review) {
        return new ReviewResponseDto(review.getRating(), review.getContent(), review.getMenu());
    }
}