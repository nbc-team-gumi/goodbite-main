package site.mygumi.goodbite.domain.waiting.exception.detail;

import site.mygumi.goodbite.domain.waiting.exception.WaitingErrorCode;
import site.mygumi.goodbite.domain.waiting.exception.WaitingException;

public class WaitingCanNotDuplicatedException extends WaitingException {

    public WaitingCanNotDuplicatedException(WaitingErrorCode waitingErrorCode) {
        super(waitingErrorCode);
    }

}
