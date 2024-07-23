package com.sparta.goodbite.exception.customer.detail;

import com.sparta.goodbite.exception.customer.CustomerErrorCode;
import com.sparta.goodbite.exception.customer.CustomerException;

public class CustomerAlreadyDeletedException extends CustomerException {
    public CustomerAlreadyDeletedException(CustomerErrorCode customerErrorCode){super(customerErrorCode);}
}
