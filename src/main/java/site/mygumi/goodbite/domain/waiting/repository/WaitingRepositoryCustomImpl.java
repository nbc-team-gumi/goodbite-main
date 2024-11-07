package site.mygumi.goodbite.domain.waiting.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import site.mygumi.goodbite.domain.waiting.entity.QWaiting;
import site.mygumi.goodbite.domain.waiting.entity.Waiting;
import site.mygumi.goodbite.domain.waiting.entity.Waiting.WaitingStatus;

@RequiredArgsConstructor
public class WaitingRepositoryCustomImpl implements WaitingRepositoryCustom {

    private static final QWaiting qWaiting = QWaiting.waiting;
    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Waiting> findByRestaurantIdAndCustomerId(Long restaurantId,
        Long customerId) {
        Waiting waiting = queryFactory.selectFrom(qWaiting)
            .where(qWaiting.restaurant.id.eq(restaurantId)
                .and(qWaiting.customer.id.eq(customerId))
                .and(qWaiting.deletedAt.isNull()))
            .fetchOne();
        return Optional.ofNullable(waiting);
    }

    @Override
    public ArrayList<Waiting> findAllByRestaurantIdDeletedAtIsNull(Long restaurantId) {
        List<Waiting> result = queryFactory.selectFrom(qWaiting)
            .where(qWaiting.restaurant.id.eq(restaurantId)
                .and(qWaiting.deletedAt.isNull()))
            .fetch();
        return new ArrayList<>(result);
    }

    @Override
    public Long findMaxWaitingOrderByRestaurantId(Long restaurantId) {
        return queryFactory.select(qWaiting.waitingOrder.max())
            .from(qWaiting)
            .where(qWaiting.restaurant.id.eq(restaurantId)
                .and(qWaiting.deletedAt.isNull()))
            .fetchOne();
    }

    @Override
    public Page<Waiting> findPageByRestaurantId(Long restaurantId, Pageable pageable) {
        List<Waiting> waitings = queryFactory.selectFrom(qWaiting)
            .where(qWaiting.restaurant.id.eq(restaurantId)
                .and(qWaiting.deletedAt.isNull()))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        long total = queryFactory.selectFrom(qWaiting)
            .where(qWaiting.restaurant.id.eq(restaurantId)
                .and(qWaiting.deletedAt.isNull()))
            .fetch().size();

        return new PageImpl<>(waitings, pageable, total);
    }

    @Override
    public Optional<Waiting> findByIdAndDeletedAtIsNull(Long waitingId) {
        Waiting waiting = queryFactory.selectFrom(qWaiting)
            .where(qWaiting.id.eq(waitingId)
                .and(qWaiting.deletedAt.isNull()))
            .fetchOne();
        return Optional.ofNullable(waiting);
    }

    @Override
    public Optional<Waiting> findStatusByRestaurantIdAndCustomerId(Long restaurantId,
        Long customerId) {
        Waiting waiting = queryFactory.selectFrom(qWaiting)
            .where(qWaiting.restaurant.id.eq(restaurantId)
                .and(qWaiting.customer.id.eq(customerId))
                .and(qWaiting.status.eq(WaitingStatus.ENTERED)))
            .fetchOne();
        return Optional.ofNullable(waiting);
    }
}