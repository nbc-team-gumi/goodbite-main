package com.sparta.goodbite.exception.reservation;

import lombok.Getter;

@Getter
public class ReservationException extends RuntimeException {

    private final ReservationErrorCode reservationErrorCode;

    public ReservationException(ReservationErrorCode reservationErrorCode) {
        super(reservationErrorCode.getMessage());
        this.reservationErrorCode = reservationErrorCode;
    }
}