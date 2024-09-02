package site.mygumi.goodbite.exception.customer.detail;

import site.mygumi.goodbite.exception.customer.CustomerErrorCode;
import site.mygumi.goodbite.exception.customer.CustomerException;

public class DuplicateNicknameException extends CustomerException {

    public DuplicateNicknameException(CustomerErrorCode customerErrorCode) {
        super(customerErrorCode);
    }
}
