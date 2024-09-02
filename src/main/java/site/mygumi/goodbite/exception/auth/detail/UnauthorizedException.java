package site.mygumi.goodbite.exception.auth.detail;

import site.mygumi.goodbite.exception.auth.AuthErrorCode;
import site.mygumi.goodbite.exception.auth.AuthException;

public class UnauthorizedException extends AuthException {

    public UnauthorizedException(AuthErrorCode authErrorCode) {
        super(authErrorCode);
    }
}