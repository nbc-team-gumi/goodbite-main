package com.sparta.goodbite.domain.review.dto.validation.validator;

import com.sparta.goodbite.domain.review.dto.validation.constraint.RatingConstraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class RatingValidator implements ConstraintValidator<RatingConstraint, Double> {

    @Override
    public void initialize(RatingConstraint rating) {
    }

    @Override
    public boolean isValid(Double rating, ConstraintValidatorContext context) {
        if (rating == null) {
            return true;
        }
        return 0.0 <= rating && rating <= 5.0 && rating * 10 % 5 == 0;
    }
}