package com.sparta.goodbite.exception.user.detail;

import com.sparta.goodbite.exception.user.UserErrorCode;
import com.sparta.goodbite.exception.user.UserException;

public class UserMismatchException extends UserException {

    public UserMismatchException(UserErrorCode userErrorCode) {
        super(userErrorCode);
    }
}
