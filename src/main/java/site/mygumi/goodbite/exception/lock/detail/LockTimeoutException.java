package site.mygumi.goodbite.exception.lock.detail;

import site.mygumi.goodbite.exception.lock.LockErrorCode;
import site.mygumi.goodbite.exception.lock.LockException;

public class LockTimeoutException extends LockException {

    public LockTimeoutException(LockErrorCode lockErrorCode) {
        super(lockErrorCode);
    }
}
