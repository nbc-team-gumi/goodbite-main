package com.sparta.goodbite.exception.auth.detail;

import com.sparta.goodbite.exception.auth.AuthErrorCode;
import com.sparta.goodbite.exception.auth.AuthException;

public class UnauthorizedException extends AuthException {

    public UnauthorizedException(AuthErrorCode authErrorCode) {
        super(authErrorCode);
    }
}