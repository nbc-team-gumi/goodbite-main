package site.mygumi.goodbite.domain.user.customer.exception.detail;

import site.mygumi.goodbite.domain.user.customer.exception.CustomerErrorCode;
import site.mygumi.goodbite.domain.user.customer.exception.CustomerException;

public class DuplicateNicknameException extends CustomerException {

    public DuplicateNicknameException(CustomerErrorCode customerErrorCode) {
        super(customerErrorCode);
    }
}
