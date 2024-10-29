package site.mygumi.goodbite.domain.operatinghour.exception.detail;

import site.mygumi.goodbite.domain.operatinghour.exception.OperatingHourErrorCode;
import site.mygumi.goodbite.domain.operatinghour.exception.OperatingHourException;

public class OperatingHourNotFoundException extends OperatingHourException {

    public OperatingHourNotFoundException(OperatingHourErrorCode operatingHourErrorCode) {
        super(operatingHourErrorCode);
    }
}