package com.sparta.goodbite.exception.reservation.detail;

import com.sparta.goodbite.exception.reservation.ReservationErrorCode;
import com.sparta.goodbite.exception.reservation.ReservationException;

public class InvalidReservationTimeException extends ReservationException {

    public InvalidReservationTimeException(ReservationErrorCode reservationErrorCode) {
        super(reservationErrorCode);
    }
}