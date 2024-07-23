package com.sparta.goodbite.domain.operatinghour.dto;

import com.sparta.goodbite.domain.operatinghour.entity.OperatingHour;
import com.sparta.goodbite.domain.restaurant.entity.Restaurant;
import lombok.Getter;

@Getter
public class OperatingHourRequestDto {

    private Long restaurantId;
    private String dayOfWeek;
    private String openTime;
    private String closeTime;

    public OperatingHour toEntity(Restaurant restaurant) {
        return OperatingHour.builder()
            .restaurant(restaurant)
            .dayOfWeek(dayOfWeek)
            .openTime(openTime)
            .closeTime(closeTime)
            .build();
    }
}
