package com.sparta.goodbite.exception.operatinghour;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum OperatingHourErrorCode {
    OPERATINGHOUR_NOT_FOUND(HttpStatus.NOT_FOUND, "영업시간을 찾을 수 없습니다."),
    OPERATINGHOUR_DUPLICATED(HttpStatus.CONFLICT, "해당 요일은 이미 영업시간이 등록되어 있습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
