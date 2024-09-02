package site.mygumi.goodbite.exception.auth.detail;

import site.mygumi.goodbite.exception.auth.AuthErrorCode;
import site.mygumi.goodbite.exception.auth.AuthException;

public class InvalidRefreshTokenException extends AuthException {

    public InvalidRefreshTokenException(AuthErrorCode authErrorCode) {
        super(authErrorCode);
    }
}