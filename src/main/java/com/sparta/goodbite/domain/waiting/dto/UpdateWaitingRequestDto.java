package com.sparta.goodbite.domain.waiting.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class UpdateWaitingRequestDto {

    @Min(value = 1, message = "웨이팅 인원 수는 0명일 수 없습니다.")
    @Max(value = 10, message = "웨이팅 인원이 11명 이상일 시, 가게로 문의주세요.")
    @NotNull(message = "웨이팅 인원을 입력해 주세요")
    private Long partySize;

    @Size(min = 0, max = 50, message = "웨이팅 요구사항은 50자 이내로 기입 가능합니다.")
    private String demand;

}
