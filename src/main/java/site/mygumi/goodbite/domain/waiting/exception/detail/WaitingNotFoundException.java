package site.mygumi.goodbite.domain.waiting.exception.detail;

import site.mygumi.goodbite.domain.waiting.exception.WaitingErrorCode;
import site.mygumi.goodbite.domain.waiting.exception.WaitingException;

public class WaitingNotFoundException extends WaitingException {

    public WaitingNotFoundException(WaitingErrorCode waitingErrorCode) {
        super(waitingErrorCode);
    }

}
