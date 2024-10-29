package site.mygumi.goodbite.domain.user.owner.exception.detail;

import site.mygumi.goodbite.domain.user.owner.exception.OwnerErrorCode;
import site.mygumi.goodbite.domain.user.owner.exception.OwnerException;

public class DuplicateKakaoIdException extends OwnerException {

    public DuplicateKakaoIdException(OwnerErrorCode ownerErrorCode) {
        super(ownerErrorCode);
    }
}
