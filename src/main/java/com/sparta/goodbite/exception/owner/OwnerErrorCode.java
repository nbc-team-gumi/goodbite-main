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
    DUPLICATE_BUSINESS_NUMBER(HttpStatus.LOCKED, "이미 사업자번호가 존재합니다."),
    DUPLICATE_KAKAO_ID(HttpStatus.LOCKED, "이미 카카오 계정이 존재합니다."),
    OWNER_NOT_FOUND(HttpStatus.NOT_FOUND, "회원을 찾을 수 없습니다."),
    OWNER_ALREADY_DELETED(HttpStatus.FORBIDDEN, "이미 탈퇴한 회원입니다."),
    INVALID_BUSINESS_NUMBER(HttpStatus.BAD_REQUEST, "유효하지 않은 사업자 번호입니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
