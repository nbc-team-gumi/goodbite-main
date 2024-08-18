package com.sparta.goodbite.domain.reservation.dto;

import com.sparta.goodbite.domain.reservation.entity.Reservation;
import com.sparta.goodbite.domain.reservation.entity.ReservationStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public record ReservationResponseDto(Long reservationId, Long restaurantId, String restaurantName,
                                     Long customerId, LocalDate date, LocalTime time,
                                     String requirement, int partySize, ReservationStatus status,
                                     LocalDateTime createdAt, LocalDateTime deletedAt,
                                     List<MenuItemResponseDto> menuItems) {

    public static ReservationResponseDto from(Reservation reservation) {
        return new ReservationResponseDto(
            reservation.getId(),
            reservation.getRestaurant().getId(),
            reservation.getRestaurant().getName(),
            reservation.getCustomer().getId(),
            reservation.getDate(),
            reservation.getTime(),
            reservation.getRequirement(),
            reservation.getPartySize(),
            reservation.getStatus(),
            reservation.getCreatedAt(),
            reservation.getDeletedAt(),
            reservation.getReservationMenus().stream().map(MenuItemResponseDto::from).toList()
        );
    }
}