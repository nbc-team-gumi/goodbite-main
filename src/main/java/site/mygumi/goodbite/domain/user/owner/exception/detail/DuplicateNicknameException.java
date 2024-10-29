package site.mygumi.goodbite.domain.user.owner.exception.detail;

import site.mygumi.goodbite.domain.user.owner.exception.OwnerErrorCode;
import site.mygumi.goodbite.domain.user.owner.exception.OwnerException;

public class DuplicateNicknameException extends OwnerException {

    public DuplicateNicknameException(OwnerErrorCode ownerErrorCode) {
        super(ownerErrorCode);
    }
}
