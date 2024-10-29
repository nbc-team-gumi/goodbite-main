package site.mygumi.goodbite.domain.user.owner.exception.detail;

import site.mygumi.goodbite.domain.user.owner.exception.OwnerErrorCode;
import site.mygumi.goodbite.domain.user.owner.exception.OwnerException;

public class DuplicateBusinessNumberException extends OwnerException {

    public DuplicateBusinessNumberException(OwnerErrorCode ownerErrorCode) {
        super(ownerErrorCode);
    }
}
