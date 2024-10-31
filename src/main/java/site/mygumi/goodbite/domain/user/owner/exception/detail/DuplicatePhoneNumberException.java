package site.mygumi.goodbite.domain.user.owner.exception.detail;

import site.mygumi.goodbite.domain.user.owner.exception.OwnerErrorCode;
import site.mygumi.goodbite.domain.user.owner.exception.OwnerException;

public class DuplicatePhoneNumberException extends OwnerException {

    public DuplicatePhoneNumberException(OwnerErrorCode ownerErrorCode) {
        super(ownerErrorCode);
    }
}
