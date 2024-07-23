package com.sparta.goodbite.exception.owner;

import lombok.Getter;

@Getter
public class OwnerException extends RuntimeException {

    private final OwnerErrorCode ownerErrorCode;

    public OwnerException(OwnerErrorCode ownerErrorCode) {
        super(ownerErrorCode.getMessage());
        this.ownerErrorCode = ownerErrorCode;
    }
}
