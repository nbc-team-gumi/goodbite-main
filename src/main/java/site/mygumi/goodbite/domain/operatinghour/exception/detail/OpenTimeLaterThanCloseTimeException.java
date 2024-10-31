package site.mygumi.goodbite.domain.operatinghour.exception.detail;

import site.mygumi.goodbite.domain.operatinghour.exception.OperatingHourErrorCode;
import site.mygumi.goodbite.domain.operatinghour.exception.OperatingHourException;

public class OpenTimeLaterThanCloseTimeException extends OperatingHourException {

    public OpenTimeLaterThanCloseTimeException(OperatingHourErrorCode operatingHourErrorCode) {
        super(operatingHourErrorCode);
    }
}
