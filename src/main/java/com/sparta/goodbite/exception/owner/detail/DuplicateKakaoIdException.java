package com.sparta.goodbite.exception.owner.detail;

import com.sparta.goodbite.exception.owner.OwnerErrorCode;
import com.sparta.goodbite.exception.owner.OwnerException;

public class DuplicateKakaoIdException extends OwnerException {

    public DuplicateKakaoIdException(OwnerErrorCode ownerErrorCode) {
        super(ownerErrorCode);
    }
}
