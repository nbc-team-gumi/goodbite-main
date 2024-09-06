package site.mygumi.goodbite.exception.lock.detail;

import site.mygumi.goodbite.exception.lock.LockErrorCode;
import site.mygumi.goodbite.exception.lock.LockException;

public class LockAcquisitionException extends LockException {

    public LockAcquisitionException(LockErrorCode lockErrorCode) {
        super(lockErrorCode);
    }
}
