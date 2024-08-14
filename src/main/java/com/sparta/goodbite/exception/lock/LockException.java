package com.sparta.goodbite.exception.lock;

import lombok.Getter;

@Getter
public class LockException extends RuntimeException {

    private final LockErrorCode lockErrorCode;

    public LockException(LockErrorCode lockErrorCode) {
        super(lockErrorCode.getMessage());
        this.lockErrorCode = lockErrorCode;
    }

/*    public LockException(LockErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.status = errorCode.getHttpStatus();

    }*/
}
