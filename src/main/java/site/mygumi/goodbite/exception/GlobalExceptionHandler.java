package site.mygumi.goodbite.exception;

import site.mygumi.goodbite.common.response.MessageResponseDto;
import site.mygumi.goodbite.common.response.ResponseUtil;
import site.mygumi.goodbite.exception.auth.AuthException;
import site.mygumi.goodbite.exception.customer.CustomerException;
import site.mygumi.goodbite.exception.lock.LockException;
import site.mygumi.goodbite.exception.menu.MenuException;
import site.mygumi.goodbite.exception.operatinghour.OperatingHourException;
import site.mygumi.goodbite.exception.owner.OwnerException;
import site.mygumi.goodbite.exception.reservation.ReservationException;
import site.mygumi.goodbite.exception.restaurant.RestaurantException;
import site.mygumi.goodbite.exception.review.ReviewException;
import site.mygumi.goodbite.exception.waiting.WaitingException;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
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

    @ExceptionHandler(ReservationException.class)
    public ResponseEntity<MessageResponseDto> handleReservationException(ReservationException e) {
        log.error("에러 발생: ", e);
        return ResponseUtil.of(e.getReservationErrorCode().getHttpStatus(), e.getMessage());
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

    @ExceptionHandler(LockException.class)
    public ResponseEntity<MessageResponseDto> handleWaitingException(LockException e) {
        log.error("에러 발생: ", e);
        return ResponseUtil.of(e.getLockErrorCode().getHttpStatus(), e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<MessageResponseDto> handleValidationExceptions(
        MethodArgumentNotValidException ex) {

        List<String> errorMessages = new ArrayList<>();

        ex.getBindingResult().getFieldErrors().forEach((error) -> {
            String errorMessage = error.getDefaultMessage();
            errorMessages.add(errorMessage);
        });

        ex.getBindingResult().getGlobalErrors().forEach((error) -> {
            String errorMessage = error.getDefaultMessage();
            errorMessages.add(errorMessage);
        });

        String combinedErrorMessage = String.join("\n", errorMessages);
        return ResponseUtil.of(HttpStatus.BAD_REQUEST, combinedErrorMessage);
    }
}
