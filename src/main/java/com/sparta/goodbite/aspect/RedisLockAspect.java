package com.sparta.goodbite.aspect;


import com.sparta.goodbite.domain.waiting.dto.PostWaitingRequestDto;
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

    private final RedissonClient redissonClient; // Redisson 클라이언트를 주입받음

    @Around("@annotation(redisLock)") // @RedisLock 어노테이션이 적용된 메서드 주변에 실행될 로직 정의
    public Object around(ProceedingJoinPoint joinPoint, RedisLock redisLock) throws Throwable {
        // MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        // Method method = signature.getMethod();

        // 어노테이션의 key 속성 값을 가져옴
        String key = redisLock.key();

        // 메서드 파라미터에서 필요한 값을 가져옴
        Object[] args = joinPoint.getArgs();
        String restaurantId = "";

        // 특정 파라미터 타입을 탐색하여 restaurantId 값을 가져옴
        for (Object arg : args) {
            if (arg instanceof PostWaitingRequestDto) {
                restaurantId = String.valueOf(((PostWaitingRequestDto) arg).getRestaurantId());
                break;
            }
        }

        // 최종 락 이름 생성: key와 restaurantId를 조합
        String lockName = key + ":" + restaurantId;

        log.info("Attempting to acquire lock: {}", lockName);

        // 최종 락 이름으로 Redisson 락 생성
        RLock lock = redissonClient.getLock(lockName);

        try {
            lock.lock(); // 락 획득
            return joinPoint.proceed(); // 타겟 메서드 실행
        } finally {
            lock.unlock(); // 락 해제
            log.info("Lock released: {}", lockName);
        }
    }
}