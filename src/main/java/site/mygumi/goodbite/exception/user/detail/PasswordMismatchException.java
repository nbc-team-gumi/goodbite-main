package site.mygumi.goodbite.exception.user.detail;

import site.mygumi.goodbite.exception.user.UserErrorCode;
import site.mygumi.goodbite.exception.user.UserException;

public class PasswordMismatchException extends UserException {

    public PasswordMismatchException(UserErrorCode userErrorCode) {
        super(userErrorCode);
    }
}
