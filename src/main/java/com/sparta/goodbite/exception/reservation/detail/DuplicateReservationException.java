package com.sparta.goodbite.exception.reservation.detail;

import com.sparta.goodbite.exception.reservation.ReservationErrorCode;
import com.sparta.goodbite.exception.reservation.ReservationException;

public class DuplicateReservationException extends ReservationException {

    public DuplicateReservationException(ReservationErrorCode reservationErrorCode) {
        super(reservationErrorCode);
    }
}