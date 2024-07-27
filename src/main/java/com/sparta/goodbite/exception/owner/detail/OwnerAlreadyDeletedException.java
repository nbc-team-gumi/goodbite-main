package com.sparta.goodbite.exception.owner.detail;

import com.sparta.goodbite.exception.owner.OwnerErrorCode;
import com.sparta.goodbite.exception.owner.OwnerException;

public class OwnerAlreadyDeletedException extends OwnerException {

    public OwnerAlreadyDeletedException(OwnerErrorCode ownerErrorCode) {
        super(ownerErrorCode);
    }
}
