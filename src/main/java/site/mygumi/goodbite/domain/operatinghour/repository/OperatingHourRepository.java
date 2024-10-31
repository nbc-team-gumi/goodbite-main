package site.mygumi.goodbite.domain.operatinghour.repository;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import site.mygumi.goodbite.domain.operatinghour.entity.OperatingHour;
import site.mygumi.goodbite.domain.operatinghour.exception.OperatingHourErrorCode;
import site.mygumi.goodbite.domain.operatinghour.exception.detail.OperatingHourNotFoundException;
import site.mygumi.goodbite.domain.restaurant.entity.Restaurant;

public interface OperatingHourRepository extends JpaRepository<OperatingHour, Long> {

    boolean existsByDayOfWeekAndRestaurant(DayOfWeek dayOfWeek, Restaurant restaurant);

    Optional<OperatingHour> findByRestaurantIdAndDayOfWeek(Long restaurantId, DayOfWeek dayOfWeek);

    List<OperatingHour> findAllByRestaurantId(Long restaurantId);

    default OperatingHour findByIdOrThrow(Long operatingHourId) {
        return findById(operatingHourId).orElseThrow(() -> new OperatingHourNotFoundException(
            OperatingHourErrorCode.OPERATINGHOUR_NOT_FOUND));
    }

    default OperatingHour findByRestaurantIdAndDayOfWeekOrThrow(Long restaurantId,
        DayOfWeek dayOfWeek) {

        return findByRestaurantIdAndDayOfWeek(restaurantId, dayOfWeek).orElseThrow(
            () -> new OperatingHourNotFoundException(
                OperatingHourErrorCode.OPERATINGHOUR_NOT_FOUND));
    }
}
