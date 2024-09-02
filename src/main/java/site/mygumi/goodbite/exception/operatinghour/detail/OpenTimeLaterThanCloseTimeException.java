package site.mygumi.goodbite.exception.operatinghour.detail;

import site.mygumi.goodbite.exception.operatinghour.OperatingHourErrorCode;
import site.mygumi.goodbite.exception.operatinghour.OperatingHourException;

public class OpenTimeLaterThanCloseTimeException extends OperatingHourException {

    public OpenTimeLaterThanCloseTimeException(OperatingHourErrorCode operatingHourErrorCode) {
        super(operatingHourErrorCode);
    }
}
