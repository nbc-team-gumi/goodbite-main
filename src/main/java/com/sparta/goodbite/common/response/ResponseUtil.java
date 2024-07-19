package com.sparta.goodbite.common.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public abstract class ResponseUtil {

    public static ResponseEntity<MessageResponseDto> of(HttpStatus httpStatus, String message) {
        return ResponseEntity.status(httpStatus)
            .body(new MessageResponseDto(httpStatus.value(), message));
    }

    public static <T> ResponseEntity<DataResponseDto<T>> of(HttpStatus httpStatus, String message,
        T data) {
        return ResponseEntity.status(httpStatus)
            .body(new DataResponseDto<>(httpStatus.value(), message, data));
    }

    public static <T> ResponseEntity<DataResponseDto<T>> createOk(T data) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(new DataResponseDto<>(HttpStatus.OK.value(), "생성 성공", data));
    }

    public static <T> ResponseEntity<DataResponseDto<T>> findOk(T data) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(new DataResponseDto<>(HttpStatus.OK.value(), "조회 성공", data));
    }

    public static <T> ResponseEntity<DataResponseDto<T>> updateOk(T data) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(new DataResponseDto<>(HttpStatus.OK.value(), "수정 성공", data));
    }

    public static ResponseEntity<MessageResponseDto> deleteOk() {
        return ResponseEntity.status(HttpStatus.OK)
            .body(new MessageResponseDto(HttpStatus.OK.value(), "삭제 성공"));
    }

    public static ResponseEntity<MessageResponseDto> createOk() {
        return ResponseEntity.status(HttpStatus.OK)
            .body(new MessageResponseDto(HttpStatus.OK.value(), "생성 성공"));
    }

    public static ResponseEntity<MessageResponseDto> updateOk() {
        return ResponseEntity.status(HttpStatus.OK)
            .body(new MessageResponseDto(HttpStatus.OK.value(), "수정 성공"));
    }
}