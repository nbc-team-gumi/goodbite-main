package site.mygumi.goodbite.exception.owner.detail;

import site.mygumi.goodbite.exception.owner.OwnerErrorCode;
import site.mygumi.goodbite.exception.owner.OwnerException;

public class InvalidBusinessNumberException extends OwnerException {

    public InvalidBusinessNumberException(OwnerErrorCode ownerErrorCode) {
        super(ownerErrorCode);

    }
}