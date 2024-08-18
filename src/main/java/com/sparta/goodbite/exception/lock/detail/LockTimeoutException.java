package com.sparta.goodbite.exception.lock.detail;

import com.sparta.goodbite.exception.lock.LockErrorCode;
import com.sparta.goodbite.exception.lock.LockException;

public class LockTimeoutException extends LockException {

    public LockTimeoutException(LockErrorCode lockErrorCode) {
        super(lockErrorCode);
    }
}
