package com.sparta.goodbite.exception.auth;

import lombok.Getter;

@Getter
public class AuthException extends RuntimeException {

    private final AuthErrorCode authErrorCode;

    public AuthException(AuthErrorCode authErrorCode) {
        super(authErrorCode.getMessage());
        this.authErrorCode = authErrorCode;
    }
}
