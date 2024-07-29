package com.sparta.goodbite.domain.menu.dto.validation.constraint;

import com.sparta.goodbite.domain.menu.dto.validation.validator.AtLeastOneFieldValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = AtLeastOneFieldValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface AtLeastOneFieldConstraint {

    String message() default "필드 중 적어도 하나는 비어 있지 않아야 합니다.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}