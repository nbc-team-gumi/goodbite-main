package com.sparta.goodbite.exception.user.detail;

import com.sparta.goodbite.exception.user.UserErrorCode;
import com.sparta.goodbite.exception.user.UserException;


public class SamePasswordException extends UserException {

    public SamePasswordException(UserErrorCode userErrorCode) {
        super(userErrorCode);
    }
}
