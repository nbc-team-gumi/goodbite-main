package com.sparta.goodbite.domain.operatinghour.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sparta.goodbite.domain.operatinghour.dto.validation.OpenTimeBeforeCloseTimeConstraint;
import com.sparta.goodbite.domain.operatinghour.entity.OperatingHour;
import com.sparta.goodbite.domain.operatinghour.enums.DayOfWeekEnum;
import com.sparta.goodbite.domain.restaurant.entity.Restaurant;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;
import lombok.Getter;

@Getter
@OpenTimeBeforeCloseTimeConstraint
public class CreateOperatingHourRequestDto {

    @NotNull(message = "가게를 입력해주세요.")
    private Long restaurantId;

    private DayOfWeekEnum dayOfWeek;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime openTime;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime closeTime;

    public OperatingHour toEntity(Restaurant restaurant) {
        return OperatingHour.builder()
            .restaurant(restaurant)
            .dayOfWeek(dayOfWeek)
            .openTime(openTime)
            .closeTime(closeTime)
            .build();
    }
}