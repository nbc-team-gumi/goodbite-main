package com.sparta.goodbite.domain.reservation.dto.validation.validator;

import com.sparta.goodbite.domain.reservation.dto.validation.constraint.TimeIntervalConstraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalTime;

public class TimeIntervalValidator implements
    ConstraintValidator<TimeIntervalConstraint, LocalTime> {

    @Override
    public boolean isValid(LocalTime time, ConstraintValidatorContext context) {
        if (time == null) {
            return true;
        }

        int minutes = time.getMinute();
        return minutes == 0 || minutes == 30;
    }
}