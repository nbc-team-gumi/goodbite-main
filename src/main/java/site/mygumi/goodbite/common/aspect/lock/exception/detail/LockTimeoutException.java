package site.mygumi.goodbite.common.aspect.lock.exception.detail;

import site.mygumi.goodbite.common.aspect.lock.exception.LockErrorCode;
import site.mygumi.goodbite.common.aspect.lock.exception.LockException;

public class LockTimeoutException extends LockException {

    public LockTimeoutException(LockErrorCode lockErrorCode) {
        super(lockErrorCode);
    }
}
