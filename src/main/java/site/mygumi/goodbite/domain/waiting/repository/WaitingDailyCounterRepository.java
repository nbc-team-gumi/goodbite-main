package site.mygumi.goodbite.domain.waiting.repository;

import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import site.mygumi.goodbite.domain.waiting.entity.WaitingDailyCounter;

public interface WaitingDailyCounterRepository extends JpaRepository<WaitingDailyCounter, Long> {

    Optional<WaitingDailyCounter> findByRestaurantIdAndDate(Long restaurantId, LocalDate date);
}