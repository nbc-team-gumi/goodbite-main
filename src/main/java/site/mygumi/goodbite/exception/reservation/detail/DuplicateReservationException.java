package site.mygumi.goodbite.exception.reservation.detail;

import site.mygumi.goodbite.exception.reservation.ReservationErrorCode;
import site.mygumi.goodbite.exception.reservation.ReservationException;

public class DuplicateReservationException extends ReservationException {

    public DuplicateReservationException(ReservationErrorCode reservationErrorCode) {
        super(reservationErrorCode);
    }
}