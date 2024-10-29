package site.mygumi.goodbite.domain.user.owner.exception.detail;

import site.mygumi.goodbite.domain.user.owner.exception.OwnerErrorCode;
import site.mygumi.goodbite.domain.user.owner.exception.OwnerException;

public class OwnerAlreadyDeletedException extends OwnerException {

    public OwnerAlreadyDeletedException(OwnerErrorCode ownerErrorCode) {
        super(ownerErrorCode);
    }
}
