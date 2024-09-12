package site.mygumi.goodbite.domain.waiting.dto;

import java.time.LocalDateTime;
import site.mygumi.goodbite.domain.waiting.entity.Waiting;
import site.mygumi.goodbite.domain.waiting.entity.Waiting.WaitingStatus;

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
    LocalDateTime deletedAt) {

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