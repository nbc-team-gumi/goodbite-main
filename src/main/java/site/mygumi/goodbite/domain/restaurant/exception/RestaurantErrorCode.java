package site.mygumi.goodbite.domain.restaurant.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum RestaurantErrorCode {
    RESTAURANT_NOT_FOUND(HttpStatus.NOT_FOUND, "가게를 찾을 수 없습니다."),
    RESTAURANT_CREATE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "가게 생성에 실패하였습니다."),
    RESTAURANT_UPDATE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "가게 수정에 실패하였습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}