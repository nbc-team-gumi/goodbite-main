package site.mygumi.goodbite.exception.owner.detail;

import site.mygumi.goodbite.exception.owner.OwnerErrorCode;
import site.mygumi.goodbite.exception.owner.OwnerException;

public class DuplicateBusinessNumberException extends OwnerException {

    public DuplicateBusinessNumberException(OwnerErrorCode ownerErrorCode) {
        super(ownerErrorCode);
    }
}
