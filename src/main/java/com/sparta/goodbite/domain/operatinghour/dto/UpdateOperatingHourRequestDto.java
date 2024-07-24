package com.sparta.goodbite.domain.operatinghour.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalTime;
import lombok.Getter;

@Getter
public class UpdateOperatingHourRequestDto {

    @JsonFormat(pattern = "HH:mm")
    private LocalTime openTime;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime closeTime;

}
