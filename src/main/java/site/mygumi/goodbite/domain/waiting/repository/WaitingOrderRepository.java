package site.mygumi.goodbite.domain.waiting.repository;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.stereotype.Repository;
import site.mygumi.goodbite.domain.waiting.exception.WaitingErrorCode;
import site.mygumi.goodbite.domain.waiting.exception.detail.WaitingNotFoundException;

/**
 * 레스토랑의 웨이팅 순서를 Redis를 통해 관리하는 Repository입니다. ZSet(Sorted Set)을 사용하여 웨이팅 번호 (waitingNumber) 기준으로 자동
 * 정렬되며, 레스토랑별/날짜별로 구분됩니다.
 */
@RequiredArgsConstructor
@Repository
public class WaitingOrderRepository {

    private static final String KEY_FORMAT = "restaurant:%d:waiting-order";
    private final StringRedisTemplate redisTemplate;

    /**
     * 새로운 웨이팅을 Redis에 추가합니다. waitingNumber를 score로 사용하여 자동 정렬됩니다.
     *
     * @param restaurantId  레스토랑 ID
     * @param waitingId     웨이팅 ID
     * @param waitingNumber 웨이팅 번호 (정렬 기준)
     */
    public void addWaiting(Long restaurantId, Long waitingId, int waitingNumber) {
        String key = getKey(restaurantId);
        redisTemplate.opsForZSet().add(
            key,
            waitingId.toString(),
            waitingNumber
        );
        setKeyExpiry(key);
    }

    /**
     * Redis에서 특정 웨이팅을 제거합니다. 웨이팅 취소 또는 입장 완료 시 호출됩니다.
     *
     * @param restaurantId 레스토랑 ID
     * @param waitingId    제거할 웨이팅 ID
     */
    public void removeWaiting(Long restaurantId, Long waitingId) {
        redisTemplate.opsForZSet().remove(
            getKey(restaurantId),
            waitingId.toString()
        );
    }

    /**
     * 특정 웨이팅의 현재 대기 순서를 조회합니다. 0부터 시작하는 인덱스를 반환합니다.
     *
     * @param restaurantId 레스토랑 ID
     * @param waitingId    조회할 웨이팅 ID
     * @return 대기 순서 (0부터 시작), 웨이팅이 존재하지 않는 경우 null 반환
     */
    public Integer getWaitingOrder(Long restaurantId, Long waitingId) {
        Long rank = redisTemplate.opsForZSet().rank(
            getKey(restaurantId),
            waitingId.toString()
        );
        return rank != null ? rank.intValue() : null;
    }

    /**
     * 레스토랑의 첫번째 웨이팅 ID를 조회합니다.
     *
     * @param restaurantId 레스토랑 ID
     * @return waitingNumber 순으로 정렬된 웨이팅 ID Set
     */
    public String getFirstWaitingId(Long restaurantId) {
        Set<String> firstId = redisTemplate.opsForZSet().range(
            getKey(restaurantId),
            0,
            0
        );
        if (firstId == null || firstId.isEmpty()) {
            throw new WaitingNotFoundException(WaitingErrorCode.WAITING_NOT_FOUND);
        }
        return firstId.iterator().next();
    }

    /**
     * 레스토랑의 모든 웨이팅 ID 목록을 순서대로 조회합니다.
     *
     * @param restaurantId 레스토랑 ID
     * @return waitingNumber 순으로 정렬된 웨이팅 ID Set
     */
    public Set<String> getWaitingIds(Long restaurantId) {
        return redisTemplate.opsForZSet().range(
            getKey(restaurantId),
            0,
            -1
        );
    }

    /**
     * 다수의 웨이팅을 한 번에 Redis에 추가합니다. DB 동기화 등의 벌크 작업 시 사용됩니다.
     *
     * @param restaurantId 레스토랑 ID
     * @param waitingData  웨이팅 데이터 Map (key: waitingId, value: waitingNumber)
     */
    public void bulkAddWaitings(Long restaurantId, Map<Long, Integer> waitingData) {
        String key = getKey(restaurantId);
        Set<TypedTuple<String>> tuples = waitingData.entrySet().stream()
            .map(entry -> new DefaultTypedTuple<>(
                entry.getKey().toString(),
                entry.getValue().doubleValue()
            ))
            .collect(Collectors.toSet());

        redisTemplate.opsForZSet().add(key, tuples);
        setKeyExpiry(key);
    }

    /**
     * 레스토랑과 현재 날짜로 Redis key를 생성합니다. e.g. "restaurant:{restaurantId}:waiting-order"
     *
     * @param restaurantId 레스토랑 ID
     * @return 생성된 Redis key
     */
    private String getKey(Long restaurantId) {
        return String.format(KEY_FORMAT, restaurantId);
    }

    /**
     * Redis key의 만료 시간을 당일 자정으로 설정합니다.
     *
     * @param key 만료 시간을 설정할 Redis key
     */
    private void setKeyExpiry(String key) {
        LocalDateTime todayMidnight = LocalDate.now().atStartOfDay().plusDays(1);
        Duration ttl = Duration.between(LocalDateTime.now(), todayMidnight);
        redisTemplate.expire(key, ttl.toSeconds(), TimeUnit.SECONDS);
    }
}