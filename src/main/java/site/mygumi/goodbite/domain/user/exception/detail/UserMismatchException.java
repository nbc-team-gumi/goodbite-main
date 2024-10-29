package site.mygumi.goodbite.domain.user.exception.detail;

import site.mygumi.goodbite.domain.user.exception.UserErrorCode;
import site.mygumi.goodbite.domain.user.exception.UserException;

public class UserMismatchException extends UserException {

    public UserMismatchException(UserErrorCode userErrorCode) {
        super(userErrorCode);
    }
}
