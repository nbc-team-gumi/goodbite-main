package site.mygumi.goodbite.domain.waiting.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import site.mygumi.goodbite.domain.waiting.entity.Waiting;
import site.mygumi.goodbite.domain.waiting.exception.WaitingErrorCode;
import site.mygumi.goodbite.domain.waiting.exception.WaitingException;
import site.mygumi.goodbite.domain.waiting.exception.detail.WaitingCanNotDuplicatedException;
import site.mygumi.goodbite.domain.waiting.exception.detail.WaitingNotFoundException;

public interface WaitingRepository extends JpaRepository<Waiting, Long> {

    // 삭제된 것 까지 조회함. 주의
    default Waiting findByIdOrThrow(Long waitingId) {
        return findById(waitingId).orElseThrow(
            () -> new WaitingException(WaitingErrorCode.WAITING_NOT_FOUND));
    }

    default Waiting findNotDeletedByIdOrThrow(Long waitingId) {
        return findByIdAndDeletedAtIsNull(waitingId)
            .orElseThrow(() -> new WaitingException(WaitingErrorCode.WAITING_NOT_FOUND));
    }

    @Query("SELECT w FROM Waiting w WHERE w.restaurant.id = :restaurantId AND w.customer.id = :customerId AND w.deletedAt IS NULL")
    Optional<Waiting> findByRestaurantIdAndCustomerId(Long restaurantId, Long customerId);

    @Query("SELECT w FROM Waiting w WHERE w.restaurant.id = :restaurantId AND w.deletedAt IS NULL")
    ArrayList<Waiting> findAllByRestaurantIdDeletedAtIsNull(
        Long restaurantId);

    @Query("SELECT w FROM Waiting w WHERE w.customer.id = :customerId")
    ArrayList<Waiting> findALLByCustomerId(Long customerId);

    @Query("SELECT MAX(w.waitingOrder) FROM Waiting w WHERE w.restaurant.id = :restaurant_id AND w.deletedAt IS NULL")
    Long findMaxWaitingOrderByRestaurantId(@Param("restaurant_id") Long restaurant_id);

    @Query("SELECT w FROM Waiting w WHERE w.restaurant.id = :restaurantId AND w.deletedAt IS NULL")
    Page<Waiting> findPageByRestaurantId(Long restaurantId, Pageable pageable);

    default void validateByRestaurantIdAndCustomerId(Long restaurantId, Long customerId) {
        findByRestaurantIdAndCustomerId(restaurantId, customerId).ifPresent(_waiting -> {
            throw new WaitingCanNotDuplicatedException(WaitingErrorCode.WAITING_DUPLICATED);
        });
    }

    default ArrayList<Waiting> findALLByRestaurantIdOrThrow(Long restaurantId) {
        ArrayList<Waiting> waitings = findAllByRestaurantIdDeletedAtIsNull(restaurantId);
        if (waitings.isEmpty()) {
            throw new WaitingNotFoundException(WaitingErrorCode.WAITING_NOT_FOUND);
        }
        return waitings;
    }

    @Query("SELECT w FROM Waiting w WHERE w.id = :waitingId AND w.deletedAt IS NULL")
    Optional<Waiting> findByIdAndDeletedAtIsNull(@Param("waitingId") Long waitingId);

    Page<Waiting> findPageByCustomerId(Long customerId, Pageable pageable);

    @Query("SELECT w FROM Waiting w WHERE w.restaurant.id = :restaurantId AND w.customer.id = :customerId AND w.status = 'SEATED'")
    Optional<Waiting> findStatusByRestaurantIdAndCustomerId(Long restaurantId, Long customerId);

    List<Waiting> findAllByRestaurantId(Long restaurantId);
}