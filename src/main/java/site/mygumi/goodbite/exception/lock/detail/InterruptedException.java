package site.mygumi.goodbite.exception.lock.detail;

import site.mygumi.goodbite.exception.lock.LockErrorCode;
import site.mygumi.goodbite.exception.lock.LockException;

public class InterruptedException extends LockException {

    public InterruptedException(LockErrorCode lockErrorCode) {
        super(lockErrorCode);
    }
}
