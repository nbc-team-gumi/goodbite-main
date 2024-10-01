package site.mygumi.goodbite.exception.reservation.detail;

import site.mygumi.goodbite.exception.reservation.ReservationErrorCode;
import site.mygumi.goodbite.exception.reservation.ReservationException;

public class ReservationNotFoundException extends ReservationException {

    public ReservationNotFoundException(ReservationErrorCode reservationErrorCode) {
        super(reservationErrorCode);
    }
}