package com.sparta.goodbite.exception.s3.detail;

import com.sparta.goodbite.exception.s3.S3ErrorCode;
import com.sparta.goodbite.exception.s3.S3Exception;

public class S3FileDeleteFailedException extends S3Exception {

    public S3FileDeleteFailedException(S3ErrorCode s3ErrorCode) {
        super(s3ErrorCode);
    }
}
