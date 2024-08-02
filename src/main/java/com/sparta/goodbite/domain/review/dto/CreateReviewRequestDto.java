package com.sparta.goodbite.domain.review.dto;

import com.sparta.goodbite.common.validation.constraint.RatingConstraint;
import com.sparta.goodbite.domain.customer.entity.Customer;
import com.sparta.goodbite.domain.restaurant.entity.Restaurant;
import com.sparta.goodbite.domain.review.entity.Review;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CreateReviewRequestDto {

    @NotNull(message = "식당 ID를 입력해 주세요.")
    private Long restaurantId;

    @NotNull(message = "평점을 입력해 주세요.")
    @RatingConstraint
    private float rating;

    @NotNull(message = "리뷰 내용을 입력해 주세요.")
    private String content;

    public Review toEntity(Restaurant restaurant, Customer customer) {
        return Review.builder()
            .rating(rating)
            .content(content)
            .restaurant(restaurant)
            .customer(customer)
            .build();
    }
}