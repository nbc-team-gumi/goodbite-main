package com.sparta.goodbite.exception.customer.detail;

import com.sparta.goodbite.exception.customer.CustomerErrorCode;
import com.sparta.goodbite.exception.customer.CustomerException;

public class DuplicateEmailException extends CustomerException {

    public DuplicateEmailException(CustomerErrorCode customerErrorCode) {
        super(customerErrorCode);
    }
}
