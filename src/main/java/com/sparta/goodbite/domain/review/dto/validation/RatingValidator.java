package com.sparta.goodbite.domain.review.dto.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class RatingValidator implements ConstraintValidator<RatingConstraint, Float> {

    @Override
    public void initialize(RatingConstraint rating) {
    }

    @Override
    public boolean isValid(Float rating, ConstraintValidatorContext context) {
        return rating != null && 0.0 <= rating && rating <= 5.0 && rating * 10 % 5 == 0;
    }
}