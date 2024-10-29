package site.mygumi.goodbite.common.aspect.lock.exception.detail;

import site.mygumi.goodbite.common.aspect.lock.exception.LockErrorCode;
import site.mygumi.goodbite.common.aspect.lock.exception.LockException;

public class LockReleaseException extends LockException {

    public LockReleaseException(LockErrorCode lockErrorCode) {
        super(lockErrorCode);
    }
}
