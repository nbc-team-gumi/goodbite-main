package site.mygumi.goodbite.domain.reservation.dto.validation.constraint;

import site.mygumi.goodbite.domain.reservation.dto.validation.validator.LocalDateFormatValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = LocalDateFormatValidator.class)
public @interface LocalDateFormatConstraint {

    String message() default "날짜는 yyyy-MM-dd 형식이어야 합니다.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}