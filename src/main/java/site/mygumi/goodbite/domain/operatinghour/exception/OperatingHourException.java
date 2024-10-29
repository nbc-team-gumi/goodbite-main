package site.mygumi.goodbite.domain.operatinghour.exception;

import lombok.Getter;

@Getter
public class OperatingHourException extends RuntimeException {

    private final OperatingHourErrorCode operatingHourErrorCode;

    public OperatingHourException(OperatingHourErrorCode operatingHourErrorCode) {
        super(operatingHourErrorCode.getMessage());
        this.operatingHourErrorCode = operatingHourErrorCode;
    }

}
