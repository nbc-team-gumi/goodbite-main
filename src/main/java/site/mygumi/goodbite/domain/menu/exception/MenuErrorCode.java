package site.mygumi.goodbite.domain.menu.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MenuErrorCode {
    MENU_NOT_FOUND(HttpStatus.NOT_FOUND, "메뉴를 찾을 수 없습니다."),
    MENU_CREATE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "메뉴 생성에 실패하였습니다."),
    MENU_UPDATE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "메뉴 수정에 실패하였습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}