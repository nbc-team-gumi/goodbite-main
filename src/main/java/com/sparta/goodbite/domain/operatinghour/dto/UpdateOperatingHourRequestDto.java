package com.sparta.goodbite.domain.operatinghour.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sparta.goodbite.domain.operatinghour.dto.validation.OpenTimeBeforeCloseTimeConstraint;
import java.time.LocalTime;
import lombok.Getter;

@Getter
@OpenTimeBeforeCloseTimeConstraint
public class UpdateOperatingHourRequestDto {

    @JsonFormat(pattern = "HH:mm")
    private LocalTime openTime;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime closeTime;
}