package com.sparta.goodbite.domain.waiting.dto;

import com.sparta.goodbite.domain.customer.entity.Customer;
import com.sparta.goodbite.domain.waiting.entity.Waiting;
import com.sparta.goodbite.domain.waiting.entity.Waiting.WaitingStatus;
import java.time.LocalDateTime;

public record WaitingResponseDto(

    Long waitingId,
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

    public static WaitingResponseDto of(Waiting waiting, String restaurantName, Customer customer) {
        return new WaitingResponseDto(
            waiting.getId(),
            restaurantName,
            waiting.getStatus(),
            waiting.getWaitingOrder(),
            waiting.getDemand(),
            customer.getId(),
            customer.getNickname(),
            waiting.getPartySize(),
            waiting.getCreatedAt(),
            waiting.getDeletedAt());
    }

}
