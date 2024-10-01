package site.mygumi.goodbite.domain.operatinghour.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import site.mygumi.goodbite.domain.operatinghour.dto.validation.contraint.LocalTimeFormatConstraint;
import site.mygumi.goodbite.domain.operatinghour.entity.OperatingHour;
import site.mygumi.goodbite.domain.restaurant.entity.Restaurant;
import jakarta.validation.constraints.NotNull;
import java.time.DayOfWeek;
import java.time.LocalTime;
import lombok.Getter;

@Getter
public class CreateOperatingHourRequestDto {

    @NotNull(message = "가게를 입력해주세요.")
    private Long restaurantId;

    @NotNull(message = "요일을 입력해주세요.")
    private DayOfWeek dayOfWeek;

    @NotNull(message = "오픈 시간을 입력해주세요.")
    @JsonFormat(pattern = "HH:mm")
    @LocalTimeFormatConstraint
    private LocalTime openTime;

    @NotNull(message = "마감 시간을 입력해주세요.")
    @JsonFormat(pattern = "HH:mm")
    @LocalTimeFormatConstraint
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