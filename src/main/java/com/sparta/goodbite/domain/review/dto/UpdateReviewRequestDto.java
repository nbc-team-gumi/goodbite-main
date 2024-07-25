package com.sparta.goodbite.domain.review.dto;

import com.sparta.goodbite.domain.review.dto.validation.RatingConstraint;
import lombok.Getter;

@Getter
public class UpdateReviewRequestDto {

    @RatingConstraint
    Float rating;

    String content;
}