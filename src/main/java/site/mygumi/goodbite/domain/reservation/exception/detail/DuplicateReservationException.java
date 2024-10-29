package site.mygumi.goodbite.domain.reservation.exception.detail;

import site.mygumi.goodbite.domain.reservation.exception.ReservationErrorCode;
import site.mygumi.goodbite.domain.reservation.exception.ReservationException;

public class DuplicateReservationException extends ReservationException {

    public DuplicateReservationException(ReservationErrorCode reservationErrorCode) {
        super(reservationErrorCode);
    }
}