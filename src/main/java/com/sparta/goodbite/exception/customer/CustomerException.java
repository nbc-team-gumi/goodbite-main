package com.sparta.goodbite.exception.customer;

import lombok.Getter;

@Getter
public class CustomerException extends RuntimeException {

    private final CustomerErrorCode customerErrorCode;

    public CustomerException(CustomerErrorCode customerErrorCode) {
        super(customerErrorCode.getMessage());
        this.customerErrorCode = customerErrorCode;
    }
}
