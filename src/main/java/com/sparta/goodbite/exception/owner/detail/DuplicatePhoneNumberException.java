package com.sparta.goodbite.exception.owner.detail;

import com.sparta.goodbite.exception.owner.OwnerErrorCode;
import com.sparta.goodbite.exception.owner.OwnerException;

public class DuplicatePhoneNumberException extends OwnerException {

    public DuplicatePhoneNumberException(OwnerErrorCode ownerErrorCode) {
        super(ownerErrorCode);
    }
}
