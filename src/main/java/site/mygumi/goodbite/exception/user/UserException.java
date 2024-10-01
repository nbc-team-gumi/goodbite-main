package site.mygumi.goodbite.exception.user;

import lombok.Getter;

@Getter
public class UserException extends RuntimeException {

    private final UserErrorCode userErrorCode;

    public UserException(UserErrorCode userErrorCode) {
        super(userErrorCode.getMessage());
        this.userErrorCode = userErrorCode;
    }

}
