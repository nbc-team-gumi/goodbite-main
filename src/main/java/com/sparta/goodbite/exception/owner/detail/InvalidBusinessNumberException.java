package com.sparta.goodbite.exception.owner.detail;

import com.sparta.goodbite.exception.owner.OwnerErrorCode;
import com.sparta.goodbite.exception.owner.OwnerException;

public class InvalidBusinessNumberException extends OwnerException {

    public InvalidBusinessNumberException(OwnerErrorCode ownerErrorCode) {
        super(ownerErrorCode);

    }
}
