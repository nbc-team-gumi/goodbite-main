package com.sparta.goodbite.domain.reservation.dto.validation.validator;

import com.sparta.goodbite.domain.reservation.dto.validation.constraint.ReservationTimeConstraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

public class ReservationTimeValidator implements
    ConstraintValidator<ReservationTimeConstraint, LocalTime> {

    @Override
    public boolean isValid(LocalTime time, ConstraintValidatorContext context) {
        if (time == null) {
            return true;
        }
        LocalTime now = LocalTime.now();
        return now.until(time, ChronoUnit.MINUTES) >= 30;
    }
}