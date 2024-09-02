package site.mygumi.goodbite.exception.reservation.detail;

import site.mygumi.goodbite.exception.reservation.ReservationErrorCode;
import site.mygumi.goodbite.exception.reservation.ReservationException;

public class InvalidReservationTimeException extends ReservationException {

    public InvalidReservationTimeException(ReservationErrorCode reservationErrorCode) {
        super(reservationErrorCode);
    }
}