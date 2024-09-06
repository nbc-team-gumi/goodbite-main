package site.mygumi.goodbite.exception.waiting.detail;

import site.mygumi.goodbite.exception.waiting.WaitingErrorCode;
import site.mygumi.goodbite.exception.waiting.WaitingException;

public class WaitingCanNotDuplicatedException extends WaitingException {

    public WaitingCanNotDuplicatedException(WaitingErrorCode waitingErrorCode) {
        super(waitingErrorCode);
    }

}
