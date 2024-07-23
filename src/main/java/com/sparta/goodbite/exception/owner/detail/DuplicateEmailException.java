package com.sparta.goodbite.exception.owner.detail;

import com.sparta.goodbite.exception.owner.OwnerErrorCode;
import com.sparta.goodbite.exception.owner.OwnerException;

public class DuplicateEmailException extends OwnerException {

    public DuplicateEmailException(OwnerErrorCode ownerErrorCode) {
        super(ownerErrorCode);
    }
}
