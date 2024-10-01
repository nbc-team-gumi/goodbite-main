package site.mygumi.goodbite.exception.owner.detail;

import site.mygumi.goodbite.exception.owner.OwnerErrorCode;
import site.mygumi.goodbite.exception.owner.OwnerException;

public class DuplicateEmailException extends OwnerException {

    public DuplicateEmailException(OwnerErrorCode ownerErrorCode) {
        super(ownerErrorCode);
    }
}
