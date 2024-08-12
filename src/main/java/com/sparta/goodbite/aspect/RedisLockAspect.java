package com.sparta.goodbite.aspect;


import com.sparta.goodbite.domain.waiting.dto.PostWaitingRequestDto;
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
        String key = redisLock.key();
        long waitTime = redisLock.waitTime();
        long leaseTime = redisLock.leaseTime();

        // 메서드 파라미터에서 필요한 값을 가져옴
        Object[] args = joinPoint.getArgs();
        String fieldValue = "";

        for (Object arg : args) {
            if (arg instanceof PostWaitingRequestDto) {
                fieldValue = String.valueOf(((PostWaitingRequestDto) arg).getRestaurantId());
                break;
            }
        }

        String lockName = key + ":" + fieldValue;

        log.info("Attempting to acquire lock: {}", lockName);

        RLock lock = redissonClient.getLock(lockName);

        boolean isLockAcquired = false;

        try {
            // tryLock을 사용하여 락을 획득 시도, 대기 시간과 임대 시간 적용
            isLockAcquired = lock.tryLock(waitTime, leaseTime, TimeUnit.SECONDS);

            if (!isLockAcquired) {
                log.warn("Could not acquire lock: {} after waiting for {} seconds", lockName,
                    waitTime);
                throw new RuntimeException("Unable to acquire lock: " + lockName);
            }

            log.info("Lock acquired: {}", lockName);
            return joinPoint.proceed(); // 타겟 메서드 실행
        } catch (InterruptedException e) {
            log.error("Interrupted while trying to acquire lock: {}", lockName, e);
            throw new RuntimeException("Interrupted while trying to acquire lock: " + lockName, e);
        } finally {
            if (isLockAcquired && lock.isHeldByCurrentThread()) {
                lock.unlock(); // 락 해제
                log.info("Lock released: {}", lockName);
            }
        }
    }
}
