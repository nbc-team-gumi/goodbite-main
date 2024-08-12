package com.sparta.goodbite.exception.lock.detail;

import com.sparta.goodbite.exception.lock.LockErrorCode;
import com.sparta.goodbite.exception.lock.LockException;

public class LockAcquisitionException extends LockException {

    public LockAcquisitionException(LockErrorCode lockErrorCode) {
        super(lockErrorCode);
    }
}
