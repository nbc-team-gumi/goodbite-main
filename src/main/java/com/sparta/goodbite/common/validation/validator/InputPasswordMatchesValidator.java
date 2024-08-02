package com.sparta.goodbite.common.validation.validator;

import com.sparta.goodbite.common.validation.constraint.InputPasswordMatchesConstraint;
import com.sparta.goodbite.domain.customer.dto.CustomerSignupRequestDto;
import com.sparta.goodbite.domain.customer.dto.UpdateCustomerPasswordRequestDto;
import com.sparta.goodbite.domain.owner.dto.OwnerSignUpRequestDto;
import com.sparta.goodbite.domain.owner.dto.UpdateOwnerPasswordRequestDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class InputPasswordMatchesValidator implements
    ConstraintValidator<InputPasswordMatchesConstraint, Object> {

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context) {

        return switch (obj) {
            case OwnerSignUpRequestDto dto -> dto.getPassword().equals(dto.getConfirmedPassword());
            case CustomerSignupRequestDto dto ->
                dto.getPassword().equals(dto.getConfirmedPassword());
            case UpdateOwnerPasswordRequestDto dto ->
                dto.getNewPassword().equals(dto.getConfirmedNewPassword());
            case UpdateCustomerPasswordRequestDto dto ->
                dto.getNewPassword().equals(dto.getConfirmedNewPassword());
            default -> {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("유효하지 않은 요청 DTO입니다.")
                    .addConstraintViolation();
                yield false;
            }
        };
    }
}