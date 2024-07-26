package com.sparta.goodbite.exception.owner.detail;

import com.sparta.goodbite.exception.owner.OwnerErrorCode;
import com.sparta.goodbite.exception.owner.OwnerException;

public class DuplicateBusinessNumberException extends OwnerException {

    public DuplicateBusinessNumberException(OwnerErrorCode ownerErrorCode) {
        super(ownerErrorCode);
    }
}
