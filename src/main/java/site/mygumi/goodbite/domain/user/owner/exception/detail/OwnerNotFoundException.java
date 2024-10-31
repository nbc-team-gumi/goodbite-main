package site.mygumi.goodbite.domain.user.owner.exception.detail;

import site.mygumi.goodbite.domain.user.owner.exception.OwnerErrorCode;
import site.mygumi.goodbite.domain.user.owner.exception.OwnerException;


public class OwnerNotFoundException extends OwnerException {

    public OwnerNotFoundException(OwnerErrorCode ownerErrorCode) {
        super(ownerErrorCode);
    }
}
