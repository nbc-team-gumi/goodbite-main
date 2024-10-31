package site.mygumi.goodbite.common.aspect.lock.exception.detail;

import site.mygumi.goodbite.common.aspect.lock.exception.LockErrorCode;
import site.mygumi.goodbite.common.aspect.lock.exception.LockException;

public class InterruptedException extends LockException {

    public InterruptedException(LockErrorCode lockErrorCode) {
        super(lockErrorCode);
    }
}
