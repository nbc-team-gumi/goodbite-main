package site.mygumi.goodbite.domain.waiting.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import site.mygumi.goodbite.domain.waiting.entity.Waiting;
import site.mygumi.goodbite.exception.waiting.WaitingErrorCode;
import site.mygumi.goodbite.exception.waiting.WaitingException;
import site.mygumi.goodbite.exception.waiting.detail.WaitingCanNotDuplicatedException;
import site.mygumi.goodbite.exception.waiting.detail.WaitingNotFoundException;

public interface WaitingRepository extends JpaRepository<Waiting, Long>, WaitingRepositoryCustom {

    // 삭제된 것 까지 조회함. 주의
    default Waiting findByIdOrThrow(Long waitingId) {
        return findById(waitingId).orElseThrow(
            () -> new WaitingException(WaitingErrorCode.WAITING_NOT_FOUND));
    }

    default Waiting findNotDeletedByIdOrThrow(Long waitingId) {
        return findByIdAndDeletedAtIsNull(waitingId)
            .orElseThrow(() -> new WaitingException(WaitingErrorCode.WAITING_NOT_FOUND));
    }

    default void validateByRestaurantIdAndCustomerId(Long restaurantId, Long customerId) {
        findByRestaurantIdAndCustomerId(restaurantId, customerId).ifPresent(ignored -> {
            throw new WaitingCanNotDuplicatedException(WaitingErrorCode.WAITING_DUPLICATED);
        });
    }

    default ArrayList<Waiting> findAllByRestaurantIdOrThrow(Long restaurantId) {
        ArrayList<Waiting> waitings = findAllByRestaurantIdDeletedAtIsNull(restaurantId);
        if (waitings.isEmpty()) {
            throw new WaitingNotFoundException(WaitingErrorCode.WAITING_NOT_FOUND);
        }
        return waitings;
    }

    ArrayList<Waiting> findAllByCustomerId(Long customerId);

    Page<Waiting> findPageByCustomerId(Long customerId, Pageable pageable);

    List<Waiting> findAllByRestaurantId(Long restaurantId);
}