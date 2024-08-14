package com.sparta.goodbite.domain.reservation.dto.validation.validator;

import com.sparta.goodbite.domain.reservation.dto.CreateReservationRequestDto;
import com.sparta.goodbite.domain.reservation.dto.validation.constraint.ReservationDateTimeConstraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class ReservationDateTimeValidator implements
    ConstraintValidator<ReservationDateTimeConstraint, CreateReservationRequestDto> {

    @Override
    public boolean isValid(CreateReservationRequestDto dto, ConstraintValidatorContext context) {
        if (dto.getDate() == null || dto.getTime() == null) {
            return true; // 날짜 또는 시간이 없으면 다른 제약 조건이 처리해야 함
        }

        LocalDateTime dateTime = LocalDateTime.of(dto.getDate(), dto.getTime());
        LocalDateTime now = LocalDateTime.now();

        return now.until(dateTime, ChronoUnit.MINUTES) >= 30;
    }
}