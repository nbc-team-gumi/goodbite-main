package site.mygumi.goodbite.domain.user.owner.exception;

import lombok.Getter;

@Getter
public class OwnerException extends RuntimeException {

    private final OwnerErrorCode ownerErrorCode;

    public OwnerException(OwnerErrorCode ownerErrorCode) {
        super(ownerErrorCode.getMessage());
        this.ownerErrorCode = ownerErrorCode;
    }
}
