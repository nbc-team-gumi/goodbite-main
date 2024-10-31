package site.mygumi.goodbite.domain.user.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode {
    PASSWORD_MISMATCH(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지않습니다."),
    SAME_PASSWORD(HttpStatus.BAD_REQUEST, "새로운 비밀번호와 기존 비밀번호가 동일합니다."),
    USER_MISMATCH(HttpStatus.UNAUTHORIZED, "권한이 없는 사용자입니다.");

    private final HttpStatus httpStatus;
    private final String message;

}
