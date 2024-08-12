package com.sparta.goodbite.exception.lock;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum LockErrorCode {
    LOCK_ACQUISITION_FAILED(HttpStatus.CONFLICT, "락을 획득하지 못했습니다."),
    LOCK_TIMEOUT(HttpStatus.REQUEST_TIMEOUT, "락이 시간 내에 획득되지 않았습니다."),
    LOCK_INTERRUPTED(HttpStatus.INTERNAL_SERVER_ERROR, "락 획득 중 인터럽트가 발생했습니다."),
    ILLEGAL_LOCK_STATE(HttpStatus.INTERNAL_SERVER_ERROR, "락 상태가 잘못되었습니다."),
    LOCK_RELEASE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "락 해제에 실패했습니다.");
    private final HttpStatus httpStatus;
    private final String message;
}


