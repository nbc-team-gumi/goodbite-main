package com.sparta.goodbite.exception;

import com.sparta.goodbite.common.response.MessageResponseDto;
import com.sparta.goodbite.common.response.ResponseUtil;
import com.sparta.goodbite.exception.customer.CustomerException;
import com.sparta.goodbite.exception.auth.AuthException;
import com.sparta.goodbite.exception.menu.MenuException;
import com.sparta.goodbite.exception.operatinghour.OperatingHourException;
import com.sparta.goodbite.exception.owner.OwnerException;
import com.sparta.goodbite.exception.restaurant.RestaurantException;
import com.sparta.goodbite.exception.review.ReviewException;
import com.sparta.goodbite.exception.waiting.WaitingException;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
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

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<MessageResponseDto> handleAuthException(AuthException e) {
        log.error("에러 발생: ", e);
        return ResponseUtil.of(e.getAuthErrorCode().getHttpStatus(), e.getMessage());
    }

    @ExceptionHandler(ReviewException.class)
    public ResponseEntity<MessageResponseDto> handleReviewException(ReviewException e) {
        log.error("에러 발생: ", e);
        return ResponseUtil.of(e.getReviewErrorCode().getHttpStatus(), e.getMessage());
    }

    @ExceptionHandler(RestaurantException.class)
    public ResponseEntity<MessageResponseDto> handleRestaurantException(RestaurantException e) {
        log.error("에러 발생: ", e);
        return ResponseUtil.of(e.getRestaurantErrorCode().getHttpStatus(), e.getMessage());
    }

    @ExceptionHandler(OperatingHourException.class)
    public ResponseEntity<MessageResponseDto> handleOperatingHourException(
        OperatingHourException e) {
        log.error("에러 발생: ", e);
        return ResponseUtil.of(e.getOperatingHourErrorCode().getHttpStatus(), e.getMessage());
    }
  
    @ExceptionHandler(WaitingException.class)
    public ResponseEntity<MessageResponseDto> handleWaitingException(WaitingException e) {
        log.error("에러 발생: ", e);
        return ResponseUtil.of(e.getWaitingErrorCode().getHttpStatus(), e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }
}
