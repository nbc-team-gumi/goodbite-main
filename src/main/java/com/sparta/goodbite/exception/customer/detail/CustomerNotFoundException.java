package com.sparta.goodbite.exception.customer.detail;

import com.sparta.goodbite.exception.customer.CustomerErrorCode;
import com.sparta.goodbite.exception.customer.CustomerException;

public class CustomerNotFoundException extends CustomerException {
    public CustomerNotFoundException(CustomerErrorCode customerErrorCode){super(customerErrorCode);}
}
