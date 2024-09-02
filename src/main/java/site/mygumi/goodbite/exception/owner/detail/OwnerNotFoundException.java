package site.mygumi.goodbite.exception.owner.detail;

import site.mygumi.goodbite.exception.owner.OwnerErrorCode;
import site.mygumi.goodbite.exception.owner.OwnerException;


public class OwnerNotFoundException extends OwnerException {

    public OwnerNotFoundException(OwnerErrorCode ownerErrorCode) {
        super(ownerErrorCode);
    }
}
