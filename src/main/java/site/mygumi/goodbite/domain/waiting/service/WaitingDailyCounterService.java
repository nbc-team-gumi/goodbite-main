package site.mygumi.goodbite.domain.waiting.service;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.mygumi.goodbite.domain.waiting.entity.WaitingDailyCounter;
import site.mygumi.goodbite.domain.waiting.repository.WaitingDailyCounterRepository;

@RequiredArgsConstructor
@Service
public class WaitingDailyCounterService {

    private final WaitingDailyCounterRepository counterRepository;

    @Transactional
    public Integer issueWaitingNumber(Long restaurantId) {
        LocalDate today = LocalDate.now();

        WaitingDailyCounter counter = counterRepository
            .findByRestaurantIdAndDate(restaurantId, today)
            .orElseGet(() -> WaitingDailyCounter.createCounter(restaurantId, today));

        Integer nextNumber = counter.getNextNumber();

        counterRepository.save(counter);

        return nextNumber;
    }
}
