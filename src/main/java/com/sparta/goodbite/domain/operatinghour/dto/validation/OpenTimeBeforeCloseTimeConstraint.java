package com.sparta.goodbite.domain.operatinghour.dto.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = OpenTimeBeforeCloseTimeValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface OpenTimeBeforeCloseTimeConstraint {

    String message() default "오픈 시간은 마감 시간보다 빨라야 합니다.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}