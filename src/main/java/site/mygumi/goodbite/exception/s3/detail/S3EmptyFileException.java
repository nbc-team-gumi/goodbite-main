package site.mygumi.goodbite.exception.s3.detail;

import site.mygumi.goodbite.exception.s3.S3ErrorCode;
import site.mygumi.goodbite.exception.s3.S3Exception;

public class S3EmptyFileException extends S3Exception {

    public S3EmptyFileException(S3ErrorCode s3ErrorCode) {
        super(s3ErrorCode);
    }
}