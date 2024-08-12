package com.sparta.goodbite.exception.customer.detail;

import com.sparta.goodbite.exception.customer.CustomerErrorCode;
import com.sparta.goodbite.exception.customer.CustomerException;

public class DuplicateKakaoIdException extends CustomerException {

    public DuplicateKakaoIdException(CustomerErrorCode customerErrorCode) {
        super(customerErrorCode);
    }
}
