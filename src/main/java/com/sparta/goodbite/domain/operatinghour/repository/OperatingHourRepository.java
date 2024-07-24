package com.sparta.goodbite.domain.operatinghour.repository;

import com.sparta.goodbite.domain.operatinghour.entity.OperatingHour;
import com.sparta.goodbite.domain.operatinghour.enums.DayOfWeekEnum;
import com.sparta.goodbite.domain.restaurant.entity.Restaurant;
import com.sparta.goodbite.exception.operatinghour.OperatingHourErrorCode;
import com.sparta.goodbite.exception.operatinghour.detail.OperatingHourNotFoundException;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OperatingHourRepository extends JpaRepository<OperatingHour, Long> {

    default OperatingHour findByIdOrThrow(Long operatingHourId) {
        return findById(operatingHourId).orElseThrow(() -> new OperatingHourNotFoundException(
            OperatingHourErrorCode.OPERATINGHOUR_NOT_FOUND));
    }

    List<OperatingHour> findAllByRestaurantId(Long restaurantId);

    boolean existsByDayOfWeekAndRestaurant(DayOfWeekEnum dayOfWeek, Restaurant restaurant);
}
