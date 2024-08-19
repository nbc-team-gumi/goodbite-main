package com.sparta.goodbite.domain.waiting.dto;

import com.sparta.goodbite.domain.waiting.entity.Waiting.WaitingStatus;
import com.sparta.goodbite.domain.waiting.entity.Waiting.WaitingType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class PostWaitingRequestDto {

    @NotNull(message = "레스토랑 Id를 입력하세요")
    private Long restaurantId;

    private WaitingStatus waitingStatus;

    @Min(value = 1, message = "웨이팅 인원 수는 0명 일 수 없습니다.")
    @Max(value = 10, message = "웨이팅 인원이 11명 이상일 시, 가게로 문의주세요.")
    @NotNull(message = "웨이팅 인원을 입력해 주세요")
    private Long partySize;

    private WaitingType waitingType;

    @Size(min = 0, max = 50, message = "웨이팅 요구사항은 50자 이내로 기입 가능합니다.")
    private String demand;

}
