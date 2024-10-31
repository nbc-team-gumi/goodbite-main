package site.mygumi.goodbite.auth.exception.detail;

import site.mygumi.goodbite.auth.exception.AuthErrorCode;
import site.mygumi.goodbite.auth.exception.AuthException;

public class UnauthorizedException extends AuthException {

    public UnauthorizedException(AuthErrorCode authErrorCode) {
        super(authErrorCode);
    }
}