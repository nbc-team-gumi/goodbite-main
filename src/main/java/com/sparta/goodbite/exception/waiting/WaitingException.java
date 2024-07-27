package com.sparta.goodbite.exception.waiting;

import lombok.Getter;

@Getter
public class WaitingException extends RuntimeException {

    private final WaitingErrorCode waitingErrorCode;

    public WaitingException(WaitingErrorCode waitingErrorCode) {
        super(waitingErrorCode.getMessage());
        this.waitingErrorCode = waitingErrorCode;
    }
}
