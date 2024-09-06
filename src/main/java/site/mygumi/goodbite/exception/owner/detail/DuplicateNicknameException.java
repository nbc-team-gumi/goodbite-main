package site.mygumi.goodbite.exception.owner.detail;

import site.mygumi.goodbite.exception.owner.OwnerErrorCode;
import site.mygumi.goodbite.exception.owner.OwnerException;

public class DuplicateNicknameException extends OwnerException {

    public DuplicateNicknameException(OwnerErrorCode ownerErrorCode) {
        super(ownerErrorCode);
    }
}
