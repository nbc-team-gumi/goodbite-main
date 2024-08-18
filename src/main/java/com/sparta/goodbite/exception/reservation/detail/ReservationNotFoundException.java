package com.sparta.goodbite.exception.reservation.detail;

import com.sparta.goodbite.exception.reservation.ReservationErrorCode;
import com.sparta.goodbite.exception.reservation.ReservationException;

public class ReservationNotFoundException extends ReservationException {

    public ReservationNotFoundException(ReservationErrorCode reservationErrorCode) {
        super(reservationErrorCode);
    }
}