package site.mygumi.goodbite.domain.reservation.dto.validation.validator;

import site.mygumi.goodbite.domain.reservation.dto.validation.constraint.LocalDateFormatConstraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class LocalDateFormatValidator implements
    ConstraintValidator<LocalDateFormatConstraint, LocalDate> {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(
        "yyyy-MM-dd");

    @Override
    public boolean isValid(LocalDate date, ConstraintValidatorContext context) {
        if (date == null) {
            return true;
        }
        try {
            DATE_FORMATTER.format(date);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}