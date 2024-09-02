package site.mygumi.goodbite.exception.customer.detail;

import site.mygumi.goodbite.exception.customer.CustomerErrorCode;
import site.mygumi.goodbite.exception.customer.CustomerException;

public class CustomerAlreadyDeletedException extends CustomerException {

    public CustomerAlreadyDeletedException(CustomerErrorCode customerErrorCode) {
        super(customerErrorCode);
    }
}
