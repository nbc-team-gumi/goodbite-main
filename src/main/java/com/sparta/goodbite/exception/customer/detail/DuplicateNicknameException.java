package com.sparta.goodbite.exception.customer.detail;

import com.sparta.goodbite.exception.customer.CustomerErrorCode;
import com.sparta.goodbite.exception.customer.CustomerException;

public class DuplicateNicknameException extends CustomerException {

    public DuplicateNicknameException(CustomerErrorCode customerErrorCode) {
        super(customerErrorCode);
    }
}
