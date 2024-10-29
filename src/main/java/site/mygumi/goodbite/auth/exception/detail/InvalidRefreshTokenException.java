package site.mygumi.goodbite.auth.exception.detail;

import site.mygumi.goodbite.auth.exception.AuthErrorCode;
import site.mygumi.goodbite.auth.exception.AuthException;

public class InvalidRefreshTokenException extends AuthException {

    public InvalidRefreshTokenException(AuthErrorCode authErrorCode) {
        super(authErrorCode);
    }
}