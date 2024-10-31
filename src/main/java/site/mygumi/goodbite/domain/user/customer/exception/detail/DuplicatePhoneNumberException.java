package site.mygumi.goodbite.domain.user.customer.exception.detail;

import site.mygumi.goodbite.domain.user.customer.exception.CustomerErrorCode;
import site.mygumi.goodbite.domain.user.customer.exception.CustomerException;

public class DuplicatePhoneNumberException extends CustomerException {

    public DuplicatePhoneNumberException(CustomerErrorCode customerErrorCode) {
        super(customerErrorCode);
    }
}
