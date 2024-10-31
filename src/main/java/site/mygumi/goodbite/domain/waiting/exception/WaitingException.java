package site.mygumi.goodbite.domain.waiting.exception;

import lombok.Getter;

@Getter
public class WaitingException extends RuntimeException {

    private final WaitingErrorCode waitingErrorCode;

    public WaitingException(WaitingErrorCode waitingErrorCode) {
        super(waitingErrorCode.getMessage());
        this.waitingErrorCode = waitingErrorCode;
    }
}
