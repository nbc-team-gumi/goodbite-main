package com.sparta.goodbite.domain.waiting.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class UpdateWaitingRequestDto {

    @Min(value = 1, message = "웨이팅 인원 수는 0명 일 수 없습니다.")
    @NotNull(message = "웨이팅 인원을 입력해 주세요")
    private Long partySize;

    @Size(min = 0, max = 50, message = "웨이팅 요구사항은 50자 이내로 기입 가능합니다.")
    private String demand;
    
}
