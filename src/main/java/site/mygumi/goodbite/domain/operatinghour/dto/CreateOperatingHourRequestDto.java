package site.mygumi.goodbite.domain.operatinghour.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import java.time.DayOfWeek;
import java.time.LocalTime;
import lombok.Getter;
import site.mygumi.goodbite.domain.operatinghour.dto.validation.contraint.LocalTimeFormatConstraint;
import site.mygumi.goodbite.domain.operatinghour.entity.OperatingHour;
import site.mygumi.goodbite.domain.restaurant.entity.Restaurant;

/**
 * 영업시간 생성 요청을 위한 DTO 클레스입니다. 레스토랑 ID, 요일, 오픈 시간, 마감 시간을 포함합니다.
 *
 * @author haeuni00
 */
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

    /**
     * 요청 받은 DTO를 영업시간 엔티티로 변환합니다.
     *
     * @param restaurant 영업시간이 등록될 레스토랑 엔티티
     * @return 영업시간 엔티티
     */
    public OperatingHour toEntity(Restaurant restaurant) {
        return OperatingHour.builder()
            .restaurant(restaurant)
            .dayOfWeek(dayOfWeek)
            .openTime(openTime)
            .closeTime(closeTime)
            .build();
    }
}