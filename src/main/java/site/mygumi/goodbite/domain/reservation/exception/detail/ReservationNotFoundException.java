package site.mygumi.goodbite.domain.reservation.exception.detail;

import site.mygumi.goodbite.domain.reservation.exception.ReservationErrorCode;
import site.mygumi.goodbite.domain.reservation.exception.ReservationException;

public class ReservationNotFoundException extends ReservationException {

    public ReservationNotFoundException(ReservationErrorCode reservationErrorCode) {
        super(reservationErrorCode);
    }
}