package com.sparta.goodbite.exception.owner.detail;

import com.sparta.goodbite.exception.owner.OwnerErrorCode;
import com.sparta.goodbite.exception.owner.OwnerException;


public class OwnerNotFoundException extends OwnerException {

    public OwnerNotFoundException(OwnerErrorCode ownerErrorCode) {
        super(ownerErrorCode);
    }
}
