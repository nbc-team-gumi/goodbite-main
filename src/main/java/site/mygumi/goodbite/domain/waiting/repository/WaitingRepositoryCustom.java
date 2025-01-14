package site.mygumi.goodbite.domain.waiting.repository;

import java.util.ArrayList;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import site.mygumi.goodbite.domain.waiting.entity.Waiting;

public interface WaitingRepositoryCustom {

    Optional<Waiting> findByRestaurantIdAndCustomerId(Long restaurantId, Long customerId);

    ArrayList<Waiting> findAllByRestaurantIdDeletedAtIsNull(Long restaurantId);

    Long findMaxWaitingOrderByRestaurantId(Long restaurant_id);

    Page<Waiting> findPageByRestaurantId(Long restaurantId, Pageable pageable);

    Optional<Waiting> findByIdAndDeletedAtIsNull(Long waitingId);

    Optional<Waiting> findStatusByRestaurantIdAndCustomerId(Long restaurantId, Long customerId);
}