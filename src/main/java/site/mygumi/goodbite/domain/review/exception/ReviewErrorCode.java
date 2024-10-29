package site.mygumi.goodbite.domain.review.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ReviewErrorCode {
    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "리뷰를 찾을 수 없습니다."),
    CANNOT_SUBMIT_REVIEW(HttpStatus.FORBIDDEN, "리뷰 작성 기간이 아닙니다.");

    private final HttpStatus httpStatus;
    private final String message;
}