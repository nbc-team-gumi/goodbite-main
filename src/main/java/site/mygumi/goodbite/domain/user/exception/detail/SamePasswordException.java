package site.mygumi.goodbite.domain.user.exception.detail;

import site.mygumi.goodbite.domain.user.exception.UserErrorCode;
import site.mygumi.goodbite.domain.user.exception.UserException;


public class SamePasswordException extends UserException {

    public SamePasswordException(UserErrorCode userErrorCode) {
        super(userErrorCode);
    }
}
