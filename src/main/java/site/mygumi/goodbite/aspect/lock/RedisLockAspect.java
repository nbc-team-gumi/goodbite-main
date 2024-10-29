package site.mygumi.goodbite.aspect.lock;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;
import site.mygumi.goodbite.domain.waiting.dto.PostWaitingRequestDto;
import site.mygumi.goodbite.exception.lock.LockErrorCode;
import site.mygumi.goodbite.exception.lock.LockException;

import java.util.concurrent.TimeUnit;

/**
 * Redis를 이용한 분산 락을 처리하는 Aspect 클래스입니다.
 * <p>
 * {@link RedisLock} 어노테이션이 적용된 메서드 호출 시 이 Aspect가 실행되며,
 * 특정 리소스(예: restaurant ID)에 대해 락을 시도합니다.
 * 락을 획득하면 메서드가 실행되며, 획득하지 못할 경우 {@link LockException} 예외를 발생시킵니다.
 * </p>
 * <b>사용 예:</b>
 * <pre>
 *     @RedisLock(waitTime = 5, leaseTime = 10)
 *     public void methodToLock(PostWaitingRequestDto dto) {
 *         // 메서드 구현부
 *     }
 * </pre>
 *
 * @author Kang Hyun Ji / Qwen
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class RedisLockAspect {

    /**
     * 분산 락 획득을 위한 Redisson 클라이언트
     */
    private final RedissonClient redissonClient;

    /**
     * {@link RedisLock} 어노테이션이 적용된 메서드 호출 전후에 분산 락을 관리하는 어드바이스입니다.
     * <p>
     * 이 메서드는 {@link PostWaitingRequestDto} 파라미터로부터 추출한 restaurant ID를 기준으로 락을 획득합니다.
     * 대기 시간 동안 락을 획득하지 못하면 {@link LockException}을 발생시킵니다.
     * </p>
     *
     * @param joinPoint 현재 실행 중인 조인 포인트(메서드 호출 정보 포함)
     * @param redisLock 타겟 메서드에 적용된 RedisLock 어노테이션
     * @return 락을 획득한 후 타겟 메서드의 실행 결과를 반환
     * @throws Throwable 타겟 메서드 실행 시 발생할 수 있는 예외
     */
    @Around("@annotation(redisLock)")
    public Object around(ProceedingJoinPoint joinPoint, RedisLock redisLock) throws Throwable {
        // RedisLock 어노테이션에서 대기 시간과 임대 시간 가져오기
        long waitTime = redisLock.waitTime();
        long leaseTime = redisLock.leaseTime();

        // 메서드 파라미터에서 PostWaitingRequestDto를 찾아 restaurant ID 추출
        Object[] args = joinPoint.getArgs();
        String restaurantId = "";

        for (Object arg : args) {
            if (arg instanceof PostWaitingRequestDto) {
                restaurantId = String.valueOf(((PostWaitingRequestDto) arg).getRestaurantId());
                break;
            }
        }

        // restaurant ID를 락 이름으로 설정
        String lockName = restaurantId;
        log.debug("락 획득 시도: {}", lockName);

        // Redisson을 통해 락 객체 가져오기
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

            log.debug("락 획득: {}", lockName);
            return joinPoint.proceed(); // 타겟 메서드 실행
        } catch (InterruptedException e) {
            // 락 획득 중 인터럽트 발생 시 로그 기록 및 예외 발생
            log.error("락을 획득하려는 도중에 인터럽트되었습니다: {}", lockName, e);
            throw new LockException(LockErrorCode.LOCK_INTERRUPTED);
        } finally {
            // 락을 획득한 경우 현재 스레드가 락을 보유 중일 때 락 해제
            if (isLockAcquired && lock.isHeldByCurrentThread()) {
                try {
                    lock.unlock();
                    log.debug("락 해제: {}", lockName);
                } catch (IllegalMonitorStateException e) {
                    // 락 해제 실패 시 로그 기록 및 예외 발생
                    log.error("락 해제 실패: 락 {}이(가) 해제되지 않았습니다. 메서드: {}, 파라미터: {}, 예외: {}", lockName,
                            joinPoint.getSignature().getName(), args, e);
                    throw new LockException(LockErrorCode.LOCK_RELEASE_FAILED);
                }
            }
        }
    }
}
