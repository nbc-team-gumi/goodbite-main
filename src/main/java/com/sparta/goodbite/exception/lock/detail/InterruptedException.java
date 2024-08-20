package com.sparta.goodbite.exception.lock.detail;

import com.sparta.goodbite.exception.lock.LockErrorCode;
import com.sparta.goodbite.exception.lock.LockException;

public class InterruptedException extends LockException {

    public InterruptedException(LockErrorCode lockErrorCode) {
        super(lockErrorCode);
    }
}
