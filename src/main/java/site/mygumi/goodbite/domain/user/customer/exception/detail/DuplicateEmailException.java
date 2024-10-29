package site.mygumi.goodbite.domain.user.customer.exception.detail;

import site.mygumi.goodbite.domain.user.customer.exception.CustomerErrorCode;
import site.mygumi.goodbite.domain.user.customer.exception.CustomerException;

public class DuplicateEmailException extends CustomerException {

    public DuplicateEmailException(CustomerErrorCode customerErrorCode) {
        super(customerErrorCode);
    }
}
