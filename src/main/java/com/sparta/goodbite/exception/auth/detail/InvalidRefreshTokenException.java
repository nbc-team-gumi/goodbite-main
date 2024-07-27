package com.sparta.goodbite.exception.auth.detail;

import com.sparta.goodbite.exception.auth.AuthErrorCode;
import com.sparta.goodbite.exception.auth.AuthException;

public class InvalidRefreshTokenException extends AuthException {

    public InvalidRefreshTokenException(AuthErrorCode authErrorCode) {
        super(authErrorCode);
    }
}