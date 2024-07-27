package com.sparta.goodbite.exception.operatinghour;

import lombok.Getter;

@Getter
public class OperatingHourException extends RuntimeException {

    private final OperatingHourErrorCode operatingHourErrorCode;

    public OperatingHourException(OperatingHourErrorCode operatingHourErrorCode) {
        super(operatingHourErrorCode.getMessage());
        this.operatingHourErrorCode = operatingHourErrorCode;
    }

}
