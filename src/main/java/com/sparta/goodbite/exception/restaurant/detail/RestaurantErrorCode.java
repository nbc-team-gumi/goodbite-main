package com.sparta.goodbite.exception.restaurant.detail;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum RestaurantErrorCode {
    RESTUARNAT_NOT_FOUND(HttpStatus.NOT_FOUND, "가게를 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
