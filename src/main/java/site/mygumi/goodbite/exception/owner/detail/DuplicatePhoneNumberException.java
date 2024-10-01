package site.mygumi.goodbite.exception.owner.detail;

import site.mygumi.goodbite.exception.owner.OwnerErrorCode;
import site.mygumi.goodbite.exception.owner.OwnerException;

public class DuplicatePhoneNumberException extends OwnerException {

    public DuplicatePhoneNumberException(OwnerErrorCode ownerErrorCode) {
        super(ownerErrorCode);
    }
}
