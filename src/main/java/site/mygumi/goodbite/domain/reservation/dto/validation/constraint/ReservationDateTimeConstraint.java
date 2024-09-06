package site.mygumi.goodbite.domain.reservation.dto.validation.constraint;

import site.mygumi.goodbite.domain.reservation.dto.validation.validator.ReservationDateTimeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ReservationDateTimeValidator.class)
public @interface ReservationDateTimeConstraint {

    String message() default "예약 시간은 현재 시간보다 최소 30분 후여야 합니다.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}