package com.sparta.goodbite.domain.reservation.dto;

import com.sparta.goodbite.domain.reservation.entity.Reservation;
import com.sparta.goodbite.domain.reservation.entity.ReservationStatus;
import java.time.LocalDate;
import java.time.LocalTime;

public record ReservationResponseDto(Long reservationId, Long restaurantId, Long customerId,
                                     LocalDate date, LocalTime time, String requirement,
                                     int partySize, ReservationStatus status) {

    public static ReservationResponseDto from(Reservation reservation) {
        return new ReservationResponseDto(
            reservation.getId(),
            reservation.getRestaurant().getId(),
            reservation.getCustomer().getId(),
            reservation.getDate(),
            reservation.getTime(),
            reservation.getRequirement(),
            reservation.getPartySize(),
            reservation.getStatus()
        );
    }
}