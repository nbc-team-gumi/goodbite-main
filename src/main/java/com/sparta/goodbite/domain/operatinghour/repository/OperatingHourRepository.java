package com.sparta.goodbite.domain.operatinghour.repository;

import com.sparta.goodbite.domain.operatinghour.entity.OperatingHour;
import com.sparta.goodbite.exception.operatinghour.OperatingHourErrorCode;
import com.sparta.goodbite.exception.operatinghour.detail.OperatingHourNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OperatingHourRepository extends JpaRepository<OperatingHour, Long> {

    default OperatingHour findByIdOrThrow(Long operatingHourId) {
        return findById(operatingHourId).orElseThrow(() -> new OperatingHourNotFoundException(
            OperatingHourErrorCode.OPERATINGHOUR_NOT_FOUND));
    }
}
