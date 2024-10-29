package site.mygumi.goodbite.domain.reservation.exception.detail;

import site.mygumi.goodbite.domain.reservation.exception.ReservationErrorCode;
import site.mygumi.goodbite.domain.reservation.exception.ReservationException;

public class InvalidReservationTimeException extends ReservationException {

    public InvalidReservationTimeException(ReservationErrorCode reservationErrorCode) {
        super(reservationErrorCode);
    }
}