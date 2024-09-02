package site.mygumi.goodbite.exception.customer.detail;

import site.mygumi.goodbite.exception.customer.CustomerErrorCode;
import site.mygumi.goodbite.exception.customer.CustomerException;

public class CustomerNotFoundException extends CustomerException {

    public CustomerNotFoundException(CustomerErrorCode customerErrorCode) {
        super(customerErrorCode);
    }
}
