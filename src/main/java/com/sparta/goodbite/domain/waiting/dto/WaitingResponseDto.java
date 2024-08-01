package com.sparta.goodbite.domain.waiting.dto;

import com.sparta.goodbite.domain.waiting.entity.Waiting;
import com.sparta.goodbite.domain.waiting.entity.Waiting.WaitingStatus;
import java.time.LocalDateTime;

public record WaitingResponseDto(

    Long waitingId,
    Long restaurantId,
    String restaurantName,
    WaitingStatus waitingStatus,
    Long waitingOrder,
    String demand,
    Long customerId,
    String customerNickname,
    Long partySize,
    LocalDateTime createAt,
    LocalDateTime deletedAt

) {

    public static WaitingResponseDto of(Waiting waiting) {
        return new WaitingResponseDto(
            waiting.getId(),
            waiting.getRestaurant().getId(),
            waiting.getRestaurant().getName(),
            waiting.getStatus(),
            waiting.getWaitingOrder(),
            waiting.getDemand(),
            waiting.getCustomer().getId(),
            waiting.getCustomer().getNickname(),
            waiting.getPartySize(),
            waiting.getCreatedAt(),
            waiting.getDeletedAt());
    }

}
