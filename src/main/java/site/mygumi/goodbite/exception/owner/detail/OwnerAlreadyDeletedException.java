package site.mygumi.goodbite.exception.owner.detail;

import site.mygumi.goodbite.exception.owner.OwnerErrorCode;
import site.mygumi.goodbite.exception.owner.OwnerException;

public class OwnerAlreadyDeletedException extends OwnerException {

    public OwnerAlreadyDeletedException(OwnerErrorCode ownerErrorCode) {
        super(ownerErrorCode);
    }
}
