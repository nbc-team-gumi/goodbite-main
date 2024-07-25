package com.sparta.goodbite.exception.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode {
    INVALID_CURRENT_PASSWORD(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지않습니다."),
    PASSWORD_SAME_AS_OLD(HttpStatus.BAD_REQUEST, "새로운 비밀번호와 기존 비밀번호가 동일합니다.");

    private final HttpStatus httpStatus;
    private final String message;

}
