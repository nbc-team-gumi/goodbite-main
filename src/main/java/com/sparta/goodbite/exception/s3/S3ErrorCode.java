package com.sparta.goodbite.exception.s3;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum S3ErrorCode {
    EMPTY_FILE(HttpStatus.BAD_REQUEST, "파일이 비어 있습니다."),
    INVALID_FILE_EXTENSION(HttpStatus.BAD_REQUEST, "유효하지 않은 파일 확장자입니다."),
    INVALID_S3_URL(HttpStatus.BAD_REQUEST, "유효하지 않은 S3 URL 입니다."),
    FILE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패하였습니다."),
    FILE_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "파일 삭제에 실패하였습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}