package site.mygumi.goodbite.domain.operatinghour.dto;

import site.mygumi.goodbite.domain.operatinghour.entity.OperatingHour;
import java.time.DayOfWeek;
import java.time.LocalTime;

public record OperatingHourResponseDto(Long operatingHourId, DayOfWeek dayOfWeek,
                                       LocalTime openTime,
                                       LocalTime closeTime) {

    public static OperatingHourResponseDto from(OperatingHour operatingHour) {
        return new OperatingHourResponseDto(
            operatingHour.getId(),
            operatingHour.getDayOfWeek(),
            operatingHour.getOpenTime(),
            operatingHour.getCloseTime()
        );
    }
}
