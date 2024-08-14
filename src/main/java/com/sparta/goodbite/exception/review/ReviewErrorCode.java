package com.sparta.goodbite.exception.review;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ReviewErrorCode {
    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "리뷰를 찾을 수 없습니다."),
    REVIEW_NOT_AUTHORIZED(HttpStatus.UNAUTHORIZED, "리뷰를 작성할 권한이 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}