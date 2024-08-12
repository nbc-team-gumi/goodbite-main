package com.sparta.goodbite.exception.s3.detail;

import com.sparta.goodbite.exception.s3.S3ErrorCode;
import com.sparta.goodbite.exception.s3.S3Exception;

public class S3InvalidS3UrlException extends S3Exception {

    public S3InvalidS3UrlException(S3ErrorCode s3ErrorCode) {
        super(s3ErrorCode);
    }
}
