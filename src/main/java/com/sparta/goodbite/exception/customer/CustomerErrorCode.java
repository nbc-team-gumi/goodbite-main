package com.sparta.goodbite.exception.customer;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CustomerErrorCode {
    DUPLICATE_NICKNAME(HttpStatus.LOCKED, "이미 닉네임이 존재합니다."),
    DUPLICATE_EMAIL(HttpStatus.LOCKED, "이미 이메일이 존재합니다."),
    DUPLICATE_PHONE_NUMBER(HttpStatus.LOCKED, "이미 전화번호가 존재합니다."),
    CUSTOMER_NOT_FOUND(HttpStatus.NOT_FOUND, "회원을 찾을 수 없습니다."),
    CUSTOMER_ALREADY_DELETED(HttpStatus.FORBIDDEN, "이미 탈퇴한 회원입니다.");
    
    private final HttpStatus httpStatus;
    private final String message;
}
