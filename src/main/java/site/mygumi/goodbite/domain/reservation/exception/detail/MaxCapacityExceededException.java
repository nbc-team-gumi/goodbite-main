package site.mygumi.goodbite.domain.reservation.exception.detail;

import site.mygumi.goodbite.domain.reservation.exception.ReservationErrorCode;
import site.mygumi.goodbite.domain.reservation.exception.ReservationException;

public class MaxCapacityExceededException extends ReservationException {

    public MaxCapacityExceededException(ReservationErrorCode reservationErrorCode) {
        super(reservationErrorCode);
    }
}