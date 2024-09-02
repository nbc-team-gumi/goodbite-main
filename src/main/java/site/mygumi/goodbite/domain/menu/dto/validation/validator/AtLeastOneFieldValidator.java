package site.mygumi.goodbite.domain.menu.dto.validation.validator;

import site.mygumi.goodbite.domain.menu.dto.UpdateMenuRequestDto;
import site.mygumi.goodbite.domain.menu.dto.validation.constraint.AtLeastOneFieldConstraint;
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