package com.sparta.goodbite.exception.user.detail;

import com.sparta.goodbite.exception.user.UserErrorCode;
import com.sparta.goodbite.exception.user.UserException;

public class PasswordMismatchException extends UserException {

    public PasswordMismatchException(UserErrorCode userErrorCode) {
        super(userErrorCode);
    }
}
