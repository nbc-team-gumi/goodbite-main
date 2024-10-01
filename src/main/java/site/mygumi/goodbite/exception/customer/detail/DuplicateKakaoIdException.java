package site.mygumi.goodbite.exception.customer.detail;

import site.mygumi.goodbite.exception.customer.CustomerErrorCode;
import site.mygumi.goodbite.exception.customer.CustomerException;

public class DuplicateKakaoIdException extends CustomerException {

    public DuplicateKakaoIdException(CustomerErrorCode customerErrorCode) {
        super(customerErrorCode);
    }
}
