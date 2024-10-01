package site.mygumi.goodbite.exception.operatinghour.detail;

import site.mygumi.goodbite.exception.operatinghour.OperatingHourErrorCode;
import site.mygumi.goodbite.exception.operatinghour.OperatingHourException;

public class OperatingHourDuplicatedException extends OperatingHourException {

    public OperatingHourDuplicatedException(OperatingHourErrorCode operatingHourErrorCode) {
        super(operatingHourErrorCode);
    }

}
