package site.mygumi.goodbite.domain.operatinghour.exception.detail;

import site.mygumi.goodbite.domain.operatinghour.exception.OperatingHourErrorCode;
import site.mygumi.goodbite.domain.operatinghour.exception.OperatingHourException;

public class OperatingHourDuplicatedException extends OperatingHourException {

    public OperatingHourDuplicatedException(OperatingHourErrorCode operatingHourErrorCode) {
        super(operatingHourErrorCode);
    }

}
