package com.sparta.goodbite.domain.waiting.dto;

import com.sparta.goodbite.domain.waiting.entity.Waiting;

public record WaitingResponseDto(

    String restaurantName,
    Long waitingOrder) {

    public static WaitingResponseDto from(Waiting waiting, String restaurantName) {
        return new WaitingResponseDto(restaurantName, waiting.getWaitingOrder());
    }
}
