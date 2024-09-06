package site.mygumi.goodbite.exception.owner.detail;

import site.mygumi.goodbite.exception.owner.OwnerErrorCode;
import site.mygumi.goodbite.exception.owner.OwnerException;

public class DuplicateKakaoIdException extends OwnerException {

    public DuplicateKakaoIdException(OwnerErrorCode ownerErrorCode) {
        super(ownerErrorCode);
    }
}
