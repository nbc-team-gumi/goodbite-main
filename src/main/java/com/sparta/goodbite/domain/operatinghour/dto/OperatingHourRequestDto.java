package com.sparta.goodbite.domain.operatinghour.dto;

import com.sparta.goodbite.domain.operatinghour.entity.OperatingHour;
import com.sparta.goodbite.domain.restaurant.entity.Restaurant;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class OperatingHourRequestDto {

    @NotNull
    private Long restaurantId;
    @NotNull
    private String dayOfWeek;
    @Pattern(regexp = "^(?:[01]\\d|2[0-4]):[0-5]\\d$", message = "24:00 형식으로 입력해주세요.")
    private String openTime;
    @Pattern(regexp = "^(?:[01]\\d|2[0-4]):[0-5]\\d$", message = "24:00 형식으로 입력해주세요.")
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
