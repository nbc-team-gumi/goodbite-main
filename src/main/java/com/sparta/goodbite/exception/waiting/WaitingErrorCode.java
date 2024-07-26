package com.sparta.goodbite.exception.waiting;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum WaitingErrorCode {
    WAITING_NOT_FOUND(HttpStatus.NOT_FOUND, "웨이팅을 찾을 수 없습니다."),
    WAITING_DUPLICATED(HttpStatus.BAD_REQUEST, "같은 식당에 웨이팅을 중복해서 할 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
