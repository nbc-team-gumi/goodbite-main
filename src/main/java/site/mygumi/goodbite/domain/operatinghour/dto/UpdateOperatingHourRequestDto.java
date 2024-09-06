package site.mygumi.goodbite.domain.operatinghour.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import site.mygumi.goodbite.domain.operatinghour.dto.validation.contraint.LocalTimeFormatConstraint;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;
import lombok.Getter;

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