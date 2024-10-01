package site.mygumi.goodbite.exception.operatinghour;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum OperatingHourErrorCode {
    OPERATINGHOUR_NOT_FOUND(HttpStatus.NOT_FOUND, "영업시간을 찾을 수 없습니다."),
    OPERATINGHOUR_DUPLICATED(HttpStatus.CONFLICT, "해당 요일은 이미 영업시간이 등록되어 있습니다."),
    OPERATINGHOUR_OPEN_AFTER_CLOSE(HttpStatus.BAD_REQUEST, "오픈 시간이 마감 시간보다 늦을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
