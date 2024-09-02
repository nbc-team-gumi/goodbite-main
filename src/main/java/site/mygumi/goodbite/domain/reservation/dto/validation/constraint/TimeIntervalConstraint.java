package site.mygumi.goodbite.domain.reservation.dto.validation.constraint;

import site.mygumi.goodbite.domain.reservation.dto.validation.validator.TimeIntervalValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = TimeIntervalValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface TimeIntervalConstraint {

    String message() default "시간은 30분 단위로 설정해야 합니다.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}