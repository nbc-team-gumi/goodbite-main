package site.mygumi.goodbite.exception.auth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode {
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "접근 권한이 없습니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "리프레시 토큰이 없거나 유효하지 않습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}