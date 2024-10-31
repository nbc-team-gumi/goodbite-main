package site.mygumi.goodbite.domain.operatinghour.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;
import lombok.Getter;
import site.mygumi.goodbite.domain.operatinghour.dto.validation.contraint.LocalTimeFormatConstraint;

/**
 * 영업시간 수정 요청을 위한 DTO 클레스입니다. 오픈 시간과 마감 시간을 포함합니다.
 *
 * @author haeuni00
 */
@Getter
public class UpdateOperatingHourRequestDto {

    @NotNull(message = "오픈 시간을 입력해주세요.")
    @JsonFormat(pattern = "HH:mm")
    @LocalTimeFormatConstraint
    private LocalTime openTime;

    @NotNull(message = "마감 시간을 입력해주세요.")
    @JsonFormat(pattern = "HH:mm")
    @LocalTimeFormatConstraint
    private LocalTime closeTime;
}