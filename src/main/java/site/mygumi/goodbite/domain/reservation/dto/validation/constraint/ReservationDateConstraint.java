package site.mygumi.goodbite.domain.reservation.dto.validation.constraint;

import site.mygumi.goodbite.domain.reservation.dto.validation.validator.ReservationDateValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ReservationDateValidator.class)
public @interface ReservationDateConstraint {

    String message() default "예약 날짜는 오늘보다 이전일 수 없습니다.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}