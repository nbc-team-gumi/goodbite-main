package com.sparta.goodbite.domain.waiting.dto;

import com.sparta.goodbite.domain.waiting.entity.Waiting.WaitingStatus;
import com.sparta.goodbite.domain.waiting.entity.Waiting.WaitingType;


public record WaitingRequestDto(
//    Long waitingOrder,
    WaitingStatus waitingStatus,
    Long partySize,
    WaitingType waitingType,
    String demand
) {

}
// {
//
//    public static WaitingRequestDto from(Waiting waiting) {
//        return new WaitingRequestDto(
//            waiting.getWaitingOrder(),
//            waiting.getStatus(),
//            waiting.getPartySize(),
//            waiting.getWaitingType(),
//            waiting.getDemand());
//    }
//
//}
