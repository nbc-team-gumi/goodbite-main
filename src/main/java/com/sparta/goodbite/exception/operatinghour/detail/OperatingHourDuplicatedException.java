package com.sparta.goodbite.exception.operatinghour.detail;

import com.sparta.goodbite.exception.operatinghour.OperatingHourErrorCode;
import com.sparta.goodbite.exception.operatinghour.OperatingHourException;

public class OperatingHourDuplicatedException extends OperatingHourException {

    public OperatingHourDuplicatedException(OperatingHourErrorCode operatingHourErrorCode) {
        super(operatingHourErrorCode);
    }

}
