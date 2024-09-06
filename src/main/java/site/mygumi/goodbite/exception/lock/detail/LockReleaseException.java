package site.mygumi.goodbite.exception.lock.detail;

import site.mygumi.goodbite.exception.lock.LockErrorCode;
import site.mygumi.goodbite.exception.lock.LockException;

public class LockReleaseException extends LockException {

    public LockReleaseException(LockErrorCode lockErrorCode) {
        super(lockErrorCode);
    }
}
