package site.mygumi.goodbite.exception.user.detail;

import site.mygumi.goodbite.exception.user.UserErrorCode;
import site.mygumi.goodbite.exception.user.UserException;


public class SamePasswordException extends UserException {

    public SamePasswordException(UserErrorCode userErrorCode) {
        super(userErrorCode);
    }
}
