package site.mygumi.goodbite.domain.operatinghour.dto.validation.contraint;

import site.mygumi.goodbite.domain.operatinghour.dto.validation.validator.LocalTimeFormatValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = LocalTimeFormatValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface LocalTimeFormatConstraint {

    String message() default "시간은 HH:mm 형식이어야 합니다.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}