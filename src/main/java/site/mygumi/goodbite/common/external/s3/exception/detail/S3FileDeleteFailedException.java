package site.mygumi.goodbite.common.external.s3.exception.detail;

import site.mygumi.goodbite.common.external.s3.exception.S3ErrorCode;
import site.mygumi.goodbite.common.external.s3.exception.S3Exception;

public class S3FileDeleteFailedException extends S3Exception {

    public S3FileDeleteFailedException(S3ErrorCode s3ErrorCode) {
        super(s3ErrorCode);
    }
}
