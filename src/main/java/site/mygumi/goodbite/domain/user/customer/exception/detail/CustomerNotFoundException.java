package site.mygumi.goodbite.domain.user.customer.exception.detail;

import site.mygumi.goodbite.domain.user.customer.exception.CustomerErrorCode;
import site.mygumi.goodbite.domain.user.customer.exception.CustomerException;

public class CustomerNotFoundException extends CustomerException {

    public CustomerNotFoundException(CustomerErrorCode customerErrorCode) {
        super(customerErrorCode);
    }
}
