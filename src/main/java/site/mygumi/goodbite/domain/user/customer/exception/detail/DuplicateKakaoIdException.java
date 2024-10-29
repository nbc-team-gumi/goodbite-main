package site.mygumi.goodbite.domain.user.customer.exception.detail;

import site.mygumi.goodbite.domain.user.customer.exception.CustomerErrorCode;
import site.mygumi.goodbite.domain.user.customer.exception.CustomerException;

public class DuplicateKakaoIdException extends CustomerException {

    public DuplicateKakaoIdException(CustomerErrorCode customerErrorCode) {
        super(customerErrorCode);
    }
}
