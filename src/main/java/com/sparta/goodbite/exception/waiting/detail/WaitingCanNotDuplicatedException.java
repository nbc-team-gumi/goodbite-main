package com.sparta.goodbite.exception.waiting.detail;

import com.sparta.goodbite.exception.waiting.WaitingErrorCode;
import com.sparta.goodbite.exception.waiting.WaitingException;

public class WaitingCanNotDuplicatedException extends WaitingException {

    public WaitingCanNotDuplicatedException(WaitingErrorCode waitingErrorCode) {
        super(waitingErrorCode);
    }

}
