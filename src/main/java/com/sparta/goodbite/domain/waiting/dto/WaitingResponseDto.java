package com.sparta.goodbite.domain.waiting.dto;

import com.sparta.goodbite.domain.waiting.entity.Waiting;
import com.sparta.goodbite.domain.waiting.entity.Waiting.WaitingStatus;
import java.time.LocalDateTime;

public record WaitingResponseDto(

    Long waitingId,
    String restaurantName,
    WaitingStatus waitingStatus,
    Long waitingOrder,
    String demand,
    LocalDateTime createAt,
    LocalDateTime deletedAt

) {

    public static WaitingResponseDto of(Waiting waiting, String restaurantName) {
        return new WaitingResponseDto(waiting.getId(), restaurantName, waiting.getStatus(),
            waiting.getWaitingOrder(), waiting.getDemand(), waiting.getCreatedAt(),
            waiting.getDeletedAt());
    }

}
