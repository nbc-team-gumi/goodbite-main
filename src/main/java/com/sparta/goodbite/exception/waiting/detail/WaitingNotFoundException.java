package com.sparta.goodbite.exception.waiting.detail;

import com.sparta.goodbite.exception.waiting.WaitingErrorCode;
import com.sparta.goodbite.exception.waiting.WaitingException;

public class WaitingNotFoundException extends WaitingException {

    public WaitingNotFoundException(WaitingErrorCode waitingErrorCode) {
        super(waitingErrorCode);
    }

}
