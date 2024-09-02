package site.mygumi.goodbite.exception.customer.detail;

import site.mygumi.goodbite.exception.customer.CustomerErrorCode;
import site.mygumi.goodbite.exception.customer.CustomerException;

public class DuplicatePhoneNumberException extends CustomerException {

    public DuplicatePhoneNumberException(CustomerErrorCode customerErrorCode) {
        super(customerErrorCode);
    }
}
