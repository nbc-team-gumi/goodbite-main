package com.sparta.goodbite.exception;

import com.sparta.goodbite.common.response.MessageResponseDto;
import com.sparta.goodbite.common.response.ResponseUtil;
import com.sparta.goodbite.exception.customer.CustomerException;
import com.sparta.goodbite.exception.menu.MenuException;
import com.sparta.goodbite.exception.owner.OwnerException;
import com.sparta.goodbite.exception.restaurant.RestaurantException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomerException.class)
    public ResponseEntity<MessageResponseDto> handleCustomerException(CustomerException e) {
        log.error("에러 발생: ", e);
        return ResponseUtil.of(e.getCustomerErrorCode().getHttpStatus(), e.getMessage());
    }

    @ExceptionHandler(OwnerException.class)
    public ResponseEntity<MessageResponseDto> handleOwnerException(OwnerException e) {
        log.error("에러 발생: ", e);
        return ResponseUtil.of(e.getOwnerErrorCode().getHttpStatus(), e.getMessage());
    }

    @ExceptionHandler(MenuException.class)
    public ResponseEntity<MessageResponseDto> handleMenuException(MenuException e) {
        log.error("에러 발생: ", e);
        return ResponseUtil.of(e.getMenuErrorCode().getHttpStatus(), e.getMessage());
    }

    @ExceptionHandler(RestaurantException.class)
    public ResponseEntity<MessageResponseDto> handleRestaurantException(RestaurantException e) {
        log.error("에러 발생: ", e);
        return ResponseUtil.of(e.getRestaurantErrorCode().getHttpStatus(), e.getMessage());
    }
}