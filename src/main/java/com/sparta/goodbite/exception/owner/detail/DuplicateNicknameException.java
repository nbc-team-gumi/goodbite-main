package com.sparta.goodbite.exception.owner.detail;

import com.sparta.goodbite.exception.owner.OwnerErrorCode;
import com.sparta.goodbite.exception.owner.OwnerException;

public class DuplicateNicknameException extends OwnerException {

    public DuplicateNicknameException(OwnerErrorCode ownerErrorCode) {
        super(ownerErrorCode);
    }
}
