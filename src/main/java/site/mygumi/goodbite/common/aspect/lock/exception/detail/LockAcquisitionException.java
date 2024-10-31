package site.mygumi.goodbite.common.aspect.lock.exception.detail;

import site.mygumi.goodbite.common.aspect.lock.exception.LockErrorCode;
import site.mygumi.goodbite.common.aspect.lock.exception.LockException;

public class LockAcquisitionException extends LockException {

    public LockAcquisitionException(LockErrorCode lockErrorCode) {
        super(lockErrorCode);
    }
}
