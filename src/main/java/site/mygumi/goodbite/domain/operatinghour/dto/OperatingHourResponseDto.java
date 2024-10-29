package site.mygumi.goodbite.domain.operatinghour.dto;

import java.time.DayOfWeek;
import java.time.LocalTime;
import site.mygumi.goodbite.domain.operatinghour.entity.OperatingHour;

/**
 * 영업시간 정보를 응답하기 위한 DTO 클래스입니다. 영업시간 ID, 요일, 오픈 시간, 마감 시간을 포함합니다.
 *
 * @param operatingHourId 영업시간 ID
 * @param dayOfWeek       요일
 * @param openTime        오픈 시간
 * @param closeTime       마감 시간
 * @author haeuni00
 */
public record OperatingHourResponseDto(Long operatingHourId, DayOfWeek dayOfWeek,
                                       LocalTime openTime,
                                       LocalTime closeTime) {

    /**
     * 영업시간 엔티티로 응답 DTO를 생성합니다.
     *
     * @param operatingHour 응답으로 변환할 영업시간 엔티티
     * @return 영업시간 정보를 포함한 DTO
     */
    public static OperatingHourResponseDto from(OperatingHour operatingHour) {
        return new OperatingHourResponseDto(
            operatingHour.getId(),
            operatingHour.getDayOfWeek(),
            operatingHour.getOpenTime(),
            operatingHour.getCloseTime()
        );
    }
}
