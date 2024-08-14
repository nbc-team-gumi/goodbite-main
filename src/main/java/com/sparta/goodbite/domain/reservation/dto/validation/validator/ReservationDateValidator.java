package com.sparta.goodbite.domain.reservation.dto.validation.validator;

import com.sparta.goodbite.domain.reservation.dto.validation.constraint.ReservationDateConstraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class ReservationDateValidator implements
    ConstraintValidator<ReservationDateConstraint, LocalDate> {

    @Override
    public boolean isValid(LocalDate date, ConstraintValidatorContext context) {
        if (date == null) {
            return true;
        }

        return !date.isBefore(LocalDate.now());
    }
}