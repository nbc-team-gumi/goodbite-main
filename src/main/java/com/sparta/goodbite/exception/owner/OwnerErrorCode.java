package com.sparta.goodbite.exception.owner;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum OwnerErrorCode {
    DUPLICATE_NICKNAME(HttpStatus.LOCKED, "이미 닉네임이 존재합니다."),
    DUPLICATE_EMAIL(HttpStatus.LOCKED, "이미 이메일이 존재합니다."),
    DUPLICATE_PHONE_NUMBER(HttpStatus.LOCKED, "이미 전화번호가 존재합니다."),
    DUPLICATE_BUSINESS_NUMBER(HttpStatus.LOCKED, "이미 사업자번호가 존재합니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
