package com.sparta.goodbite.domain.review.dto;

import com.sparta.goodbite.domain.review.dto.validation.constraint.RatingConstraint;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public abstract class CreateReviewRequestDto {

    @NotNull(message = "평점을 입력해 주세요.")
    @RatingConstraint
    protected float rating;
    @NotNull(message = "리뷰 내용을 입력해 주세요.")
    protected String content;
    @NotNull(message = "식당 ID를 입력해 주세요.")
    private Long restaurantId;
}