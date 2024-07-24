package com.sparta.goodbite.domain.operatinghour.dto;

import com.sparta.goodbite.domain.operatinghour.entity.OperatingHour;
import com.sparta.goodbite.domain.operatinghour.enums.DayOfWeekEnum;
import java.time.LocalTime;

public record OperatingHourResponseDto(DayOfWeekEnum dayOfWeek, LocalTime openTime,
                                       LocalTime closeTime) {

    public static OperatingHourResponseDto from(OperatingHour operatingHour) {
        return new OperatingHourResponseDto(
            operatingHour.getDayOfWeek(),
            operatingHour.getOpenTime(),
            operatingHour.getCloseTime()
        );
    }
}
