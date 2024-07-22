package com.sparta.goodbite.domain.waiting.dto;

import com.sparta.goodbite.domain.waiting.entity.Waiting.WaitingStatus;
import com.sparta.goodbite.domain.waiting.entity.Waiting.WaitingType;
import lombok.Getter;

@Getter
public class PostWaitingRequestDto {

    private Long restaurantId;
    //    Long waitingOrder,
    private WaitingStatus waitingStatus;
    private Long partySize;
    private WaitingType waitingType;
    private String demand;

}
