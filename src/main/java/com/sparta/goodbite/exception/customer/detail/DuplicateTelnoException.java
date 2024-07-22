package com.sparta.goodbite.exception.customer.detail;

import com.sparta.goodbite.exception.customer.CustomerErrorCode;
import com.sparta.goodbite.exception.customer.CustomerException;

public class DuplicateTelnoException extends CustomerException {
    public DuplicateTelnoException(CustomerErrorCode customerErrorCode){super(customerErrorCode);}
}
