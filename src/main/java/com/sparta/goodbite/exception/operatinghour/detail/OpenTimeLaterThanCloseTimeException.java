package com.sparta.goodbite.exception.operatinghour.detail;

import com.sparta.goodbite.exception.operatinghour.OperatingHourErrorCode;
import com.sparta.goodbite.exception.operatinghour.OperatingHourException;

public class OpenTimeLaterThanCloseTimeException extends OperatingHourException {

    public OpenTimeLaterThanCloseTimeException(OperatingHourErrorCode operatingHourErrorCode) {
        super(operatingHourErrorCode);
    }
}
