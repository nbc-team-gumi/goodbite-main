package com.sparta.goodbite.exception;

import com.sparta.goodbite.common.response.MessageResponseDto;
import com.sparta.goodbite.common.response.ResponseUtil;
import com.sparta.goodbite.exception.menu.MenuException;
import com.sparta.goodbite.exception.review.ReviewException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MenuException.class)
    public ResponseEntity<MessageResponseDto> handleMenuException(MenuException e) {
        log.error("에러 발생: ", e);
        return ResponseUtil.of(e.getMenuErrorCode().getHttpStatus(), e.getMessage());
    }

    @ExceptionHandler(ReviewException.class)
    public ResponseEntity<MessageResponseDto> handleReviewException(ReviewException e) {
        log.error("에러 발생: ", e);
        return ResponseUtil.of(e.getReviewErrorCode().getHttpStatus(), e.getMessage());
    }
}