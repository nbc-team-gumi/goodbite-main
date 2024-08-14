package com.sparta.goodbite.exception.lock.detail;

import com.sparta.goodbite.exception.lock.LockErrorCode;
import com.sparta.goodbite.exception.lock.LockException;

public class LockReleaseException extends LockException {

    public LockReleaseException(LockErrorCode lockErrorCode) {
        super(lockErrorCode);
    }
}
