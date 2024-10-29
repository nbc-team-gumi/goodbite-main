package site.mygumi.goodbite.domain.user.owner.exception.detail;

import site.mygumi.goodbite.domain.user.owner.exception.OwnerErrorCode;
import site.mygumi.goodbite.domain.user.owner.exception.OwnerException;

public class InvalidBusinessNumberException extends OwnerException {

    public InvalidBusinessNumberException(OwnerErrorCode ownerErrorCode) {
        super(ownerErrorCode);

    }
}
