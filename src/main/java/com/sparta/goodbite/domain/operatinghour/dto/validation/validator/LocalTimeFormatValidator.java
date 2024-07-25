package com.sparta.goodbite.domain.operatinghour.dto.validation.validator;

import com.sparta.goodbite.domain.operatinghour.dto.validation.contraint.LocalTimeFormatConstraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class LocalTimeFormatValidator implements
    ConstraintValidator<LocalTimeFormatConstraint, LocalTime> {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    @Override
    public boolean isValid(LocalTime value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // null 값은 다른 유효성 검사에서 처리하도록 하고, 여기서는 의도된 유효성 검사만 통과
        }
        try {
            TIME_FORMATTER.format(value);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}