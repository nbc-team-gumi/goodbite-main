package com.sparta.goodbite.domain.review.dto;

import com.sparta.goodbite.domain.review.dto.validation.constraint.RatingConstraint;
import lombok.Getter;

@Getter
public class UpdateReviewRequestDto {

    @RatingConstraint
    Float rating;

    String content;
}