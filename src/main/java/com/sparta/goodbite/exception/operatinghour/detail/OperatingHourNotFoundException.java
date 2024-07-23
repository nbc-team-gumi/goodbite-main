package com.sparta.goodbite.exception.operatinghour.detail;

import com.sparta.goodbite.exception.operatinghour.OperatingHourErrorCode;
import com.sparta.goodbite.exception.operatinghour.OperatingHourException;

public class OperatingHourNotFoundException extends OperatingHourException {

    public OperatingHourNotFoundException(OperatingHourErrorCode operatingHourErrorCode) {
        super(operatingHourErrorCode);
    }

}
