package com.sparta.goodbite.domain.operatinghour.dto;

import com.sparta.goodbite.domain.operatinghour.entity.OperatingHour;

public record OperatingHourResponseDto(String dayOfWeek, String openTime, String closeTime) {

    public static OperatingHourResponseDto from(OperatingHour operatingHour) {
        return new OperatingHourResponseDto(
            operatingHour.getDayOfWeek(),
            operatingHour.getOpenTime(),
            operatingHour.getCloseTime()
        );
    }
}
