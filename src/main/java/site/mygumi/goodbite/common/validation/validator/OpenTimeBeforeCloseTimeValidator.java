package site.mygumi.goodbite.common.validation.validator;

import site.mygumi.goodbite.common.validation.constraint.OpenTimeBeforeCloseTimeConstraint;
import site.mygumi.goodbite.domain.operatinghour.dto.CreateOperatingHourRequestDto;
import site.mygumi.goodbite.domain.operatinghour.dto.UpdateOperatingHourRequestDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalTime;

public class OpenTimeBeforeCloseTimeValidator implements
    ConstraintValidator<OpenTimeBeforeCloseTimeConstraint, Object> {

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context) {
        LocalTime openTime = null;
        LocalTime closeTime = null;

        if (obj instanceof CreateOperatingHourRequestDto dto) {
            openTime = dto.getOpenTime();
            closeTime = dto.getCloseTime();
        } else if (obj instanceof UpdateOperatingHourRequestDto dto) {
            openTime = dto.getOpenTime();
            closeTime = dto.getCloseTime();
        }

        if (openTime == null || closeTime == null) {
            return true; // null 값은 다른 유효성 검사에서 처리하도록 하고, 여기서는 의도된 유효성 검사만 통과
        }
        return openTime.isBefore(closeTime);
    }
}