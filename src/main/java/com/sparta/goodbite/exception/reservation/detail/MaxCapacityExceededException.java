package com.sparta.goodbite.exception.reservation.detail;

import com.sparta.goodbite.exception.reservation.ReservationErrorCode;
import com.sparta.goodbite.exception.reservation.ReservationException;

public class MaxCapacityExceededException extends ReservationException {

    public MaxCapacityExceededException(ReservationErrorCode reservationErrorCode) {
        super(reservationErrorCode);
    }
}