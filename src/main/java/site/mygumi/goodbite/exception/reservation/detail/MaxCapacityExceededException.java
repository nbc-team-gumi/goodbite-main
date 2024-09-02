package site.mygumi.goodbite.exception.reservation.detail;

import site.mygumi.goodbite.exception.reservation.ReservationErrorCode;
import site.mygumi.goodbite.exception.reservation.ReservationException;

public class MaxCapacityExceededException extends ReservationException {

    public MaxCapacityExceededException(ReservationErrorCode reservationErrorCode) {
        super(reservationErrorCode);
    }
}