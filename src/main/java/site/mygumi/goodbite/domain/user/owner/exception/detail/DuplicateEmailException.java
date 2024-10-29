package site.mygumi.goodbite.domain.user.owner.exception.detail;

import site.mygumi.goodbite.domain.user.owner.exception.OwnerErrorCode;
import site.mygumi.goodbite.domain.user.owner.exception.OwnerException;

public class DuplicateEmailException extends OwnerException {

    public DuplicateEmailException(OwnerErrorCode ownerErrorCode) {
        super(ownerErrorCode);
    }
}
