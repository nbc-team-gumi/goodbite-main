package com.sparta.goodbite.domain.waiting.dto;

import com.sparta.goodbite.domain.waiting.entity.Waiting;

public record WaitingResponseDto(

    Long waitingId,
    String restaurantName,
    Long waitingOrder

) {

    public static WaitingResponseDto of(Waiting waiting, String restaurantName) {
        return new WaitingResponseDto(waiting.getId(), restaurantName, waiting.getWaitingOrder());
    }

}
