package site.mygumi.goodbite.domain.user.customer.exception;

import lombok.Getter;

@Getter
public class CustomerException extends RuntimeException {

    private final CustomerErrorCode customerErrorCode;

    public CustomerException(CustomerErrorCode customerErrorCode) {
        super(customerErrorCode.getMessage());
        this.customerErrorCode = customerErrorCode;
    }
}
