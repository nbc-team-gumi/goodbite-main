package com.sparta.goodbite.domain.review.dto;

import com.sparta.goodbite.common.validation.constraint.RatingConstraint;
import lombok.Getter;

@Getter
public class UpdateReviewRequestDto {

    @RatingConstraint
    Float rating;

    String content;
}