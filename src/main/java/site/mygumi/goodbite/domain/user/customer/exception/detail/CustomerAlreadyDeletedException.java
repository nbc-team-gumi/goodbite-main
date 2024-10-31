package site.mygumi.goodbite.domain.user.customer.exception.detail;

import site.mygumi.goodbite.domain.user.customer.exception.CustomerErrorCode;
import site.mygumi.goodbite.domain.user.customer.exception.CustomerException;

public class CustomerAlreadyDeletedException extends CustomerException {

    public CustomerAlreadyDeletedException(CustomerErrorCode customerErrorCode) {
        super(customerErrorCode);
    }
}
