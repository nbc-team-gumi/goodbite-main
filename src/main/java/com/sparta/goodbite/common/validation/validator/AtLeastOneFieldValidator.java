package com.sparta.goodbite.common.validation.validator;

import com.sparta.goodbite.common.validation.constraint.AtLeastOneFieldConstraint;
import com.sparta.goodbite.domain.menu.dto.UpdateMenuRequestDto;
import io.micrometer.common.util.StringUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AtLeastOneFieldValidator implements
    ConstraintValidator<AtLeastOneFieldConstraint, UpdateMenuRequestDto> {

    @Override
    public void initialize(AtLeastOneFieldConstraint constraintAnnotation) {
    }

    @Override
    public boolean isValid(UpdateMenuRequestDto dto, ConstraintValidatorContext context) {
        return dto.getPrice() != null || StringUtils.isNotBlank(dto.getName())
            || StringUtils.isNotBlank(dto.getDescription());
    }
}