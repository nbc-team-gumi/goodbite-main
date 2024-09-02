package site.mygumi.goodbite.exception.user.detail;

import site.mygumi.goodbite.exception.user.UserErrorCode;
import site.mygumi.goodbite.exception.user.UserException;

public class UserMismatchException extends UserException {

    public UserMismatchException(UserErrorCode userErrorCode) {
        super(userErrorCode);
    }
}
