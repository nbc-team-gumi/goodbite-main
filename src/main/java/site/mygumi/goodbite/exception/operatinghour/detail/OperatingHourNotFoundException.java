package site.mygumi.goodbite.exception.operatinghour.detail;

import site.mygumi.goodbite.exception.operatinghour.OperatingHourErrorCode;
import site.mygumi.goodbite.exception.operatinghour.OperatingHourException;

public class OperatingHourNotFoundException extends OperatingHourException {

    public OperatingHourNotFoundException(OperatingHourErrorCode operatingHourErrorCode) {
        super(operatingHourErrorCode);
    }
}