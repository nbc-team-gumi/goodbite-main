package com.sparta.goodbite.aspect;


import com.sparta.goodbite.domain.waiting.dto.PostWaitingRequestDto;
import com.sparta.goodbite.exception.lock.LockErrorCode;
import com.sparta.goodbite.exception.lock.LockException;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class RedisLockAspect {

    private final RedissonClient redissonClient;

    @Around("@annotation(redisLock)")
    public Object around(ProceedingJoinPoint joinPoint, RedisLock redisLock) throws Throwable {
        long waitTime = redisLock.waitTime();
        long leaseTime = redisLock.leaseTime();

        // 메서드 파라미터에서 필요한 값을 가져옴
        Object[] args = joinPoint.getArgs();
        String restaurantId = "";

        for (Object arg : args) {
            if (arg instanceof PostWaitingRequestDto) {
                restaurantId = String.valueOf(((PostWaitingRequestDto) arg).getRestaurantId());
                break;
            }
        }

        // 락 이름을 레스토랑 ID로 설정
        String lockName = restaurantId;
        log.info("락 획득 시도: {}", lockName);

        RLock lock = redissonClient.getLock(lockName);

        boolean isLockAcquired = false;

        try {
            // tryLock을 사용하여 락을 획득 시도, 대기 시간과 임대 시간 적용
            isLockAcquired = lock.tryLock(waitTime, leaseTime, TimeUnit.SECONDS);

            if (!isLockAcquired) {
                log.warn("락 획득 실패: 락 {}을 {}초 동안 대기했으나 획득하지 못했습니다. ", lockName,
                    waitTime);
                throw new LockException(LockErrorCode.LOCK_ACQUISITION_FAILED);
            }

            log.info("락 획득: {}", lockName);
            return joinPoint.proceed(); // 타겟 메서드 실행
        } catch (InterruptedException e) {
            log.error("락을 획득하려는 도중에 인터럽트되었습니다: {}", lockName, e);
            throw new LockException(LockErrorCode.LOCK_INTERRUPTED);
        } finally {
            if (isLockAcquired && lock.isHeldByCurrentThread()) {
                try {
                    lock.unlock();
                    log.info("락 해제: {}", lockName);
                } catch (IllegalMonitorStateException e) {
                    log.error("락 해제 실패: 락 {}이(가) 해제되지 않았습니다. 메서드: {}, 파라미터: {}, 예외: {}",
                        lockName, joinPoint.getSignature().getName(), args, e);
                    throw new LockException(LockErrorCode.LOCK_RELEASE_FAILED);
                }
            }
        }
    }
}
