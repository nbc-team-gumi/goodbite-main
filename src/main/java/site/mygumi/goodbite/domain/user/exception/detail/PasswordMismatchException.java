package site.mygumi.goodbite.domain.user.exception.detail;

import site.mygumi.goodbite.domain.user.exception.UserErrorCode;
import site.mygumi.goodbite.domain.user.exception.UserException;

public class PasswordMismatchException extends UserException {

    public PasswordMismatchException(UserErrorCode userErrorCode) {
        super(userErrorCode);
    }
}
