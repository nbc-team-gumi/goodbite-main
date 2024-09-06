package site.mygumi.goodbite.domain.review.dto;

import site.mygumi.goodbite.domain.review.dto.validation.constraint.RatingConstraint;
import lombok.Getter;

@Getter
public class UpdateReviewRequestDto {

    @RatingConstraint
    Double rating;

    String content;
}