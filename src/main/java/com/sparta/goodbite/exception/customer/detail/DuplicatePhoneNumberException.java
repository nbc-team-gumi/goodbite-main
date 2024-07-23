package com.sparta.goodbite.exception.customer.detail;

import com.sparta.goodbite.exception.customer.CustomerErrorCode;
import com.sparta.goodbite.exception.customer.CustomerException;

public class DuplicatePhoneNumberException extends CustomerException {

    public DuplicatePhoneNumberException(CustomerErrorCode customerErrorCode) {
        super(customerErrorCode);
    }
}
