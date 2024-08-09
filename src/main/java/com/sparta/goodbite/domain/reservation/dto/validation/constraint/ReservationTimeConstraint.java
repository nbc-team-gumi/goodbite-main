package com.sparta.goodbite.domain.reservation.dto.validation.constraint;

import com.sparta.goodbite.domain.reservation.dto.validation.validator.ReservationTimeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = ReservationTimeValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ReservationTimeConstraint {

    String message() default "예약 시간은 현재 시간보다 최소 30분 후여야 합니다.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}