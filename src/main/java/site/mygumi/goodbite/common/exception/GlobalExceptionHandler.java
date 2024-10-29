package site.mygumi.goodbite.common.exception;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import site.mygumi.goodbite.auth.exception.AuthException;
import site.mygumi.goodbite.common.aspect.lock.exception.LockException;
import site.mygumi.goodbite.common.response.MessageResponseDto;
import site.mygumi.goodbite.common.response.ResponseUtil;
import site.mygumi.goodbite.domain.menu.exception.MenuException;
import site.mygumi.goodbite.domain.operatinghour.exception.OperatingHourException;
import site.mygumi.goodbite.domain.reservation.exception.ReservationException;
import site.mygumi.goodbite.domain.restaurant.exception.RestaurantException;
import site.mygumi.goodbite.domain.review.exception.ReviewException;
import site.mygumi.goodbite.domain.user.customer.exception.CustomerException;
import site.mygumi.goodbite.domain.user.owner.exception.OwnerException;
import site.mygumi.goodbite.domain.waiting.exception.WaitingException;

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
