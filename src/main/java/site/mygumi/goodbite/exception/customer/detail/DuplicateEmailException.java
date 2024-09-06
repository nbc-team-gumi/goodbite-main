package site.mygumi.goodbite.exception.customer.detail;

import site.mygumi.goodbite.exception.customer.CustomerErrorCode;
import site.mygumi.goodbite.exception.customer.CustomerException;

public class DuplicateEmailException extends CustomerException {

    public DuplicateEmailException(CustomerErrorCode customerErrorCode) {
        super(customerErrorCode);
    }
}
