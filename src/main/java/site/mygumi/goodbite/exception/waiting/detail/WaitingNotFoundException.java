package site.mygumi.goodbite.exception.waiting.detail;

import site.mygumi.goodbite.exception.waiting.WaitingErrorCode;
import site.mygumi.goodbite.exception.waiting.WaitingException;

public class WaitingNotFoundException extends WaitingException {

    public WaitingNotFoundException(WaitingErrorCode waitingErrorCode) {
        super(waitingErrorCode);
    }

}
