package site.mygumi.goodbite.domain.reservation.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ReservationErrorCode {
    RESERVATION_NOT_FOUND(HttpStatus.NOT_FOUND, "예약을 찾을 수 없습니다."),
    INVALID_RESERVATION_TIME(HttpStatus.BAD_REQUEST, "예약 가능한 시간이 아닙니다."),
    DUPLICATE_RESERVATION(HttpStatus.BAD_REQUEST, "이미 예약이 있습니다."),
    MAX_CAPACITY_EXCEEDED(HttpStatus.BAD_REQUEST, "예약 인원이 해당 시간의 수용 가능 인원을 초과합니다.");

    private final HttpStatus httpStatus;
    private final String message;
}