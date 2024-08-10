package com.sparta.goodbite.aspect;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect //aspect클래스임을 의미
@Component //스프링빈으로 등록
@RequiredArgsConstructor
public class RedisLockAspect {

    private final RedissonClient redissonClient; //주입

    @Around("@annotation(redisLock)") //@RedisLock 어노테이션이 적용된 메서드 주변에 실행될 로직 정의
    public Object around(ProceedingJoinPoint joinPoint, RedisLock redisLock) throws Throwable { //
        String lockName = redisLock.value(); //어노테이션의 값을 가져온다.
        RLock lock = redissonClient.getLock(lockName); //락객체생성. 락객체를 얻는 메서드. 실제로 락을 걸지는 않는다.
        log.info("Attempting to acquire lock: {}", lockName);
        lock.lock(); //락획득-RLock 객체가 호출되었을 때 해당 락을 즉시 획득하는 메서드.
        // 이 메서드는 락을 걸 수 있을 때까지 기다리며, 락이 성공적으로 획득될 때까지 블로킹된다.
        // 락을 획득할때까지 대기. 획득에 성공하면 블로킹이 해제된다.
        // 락을 반드시 획득해야 하는 상황에 사용
        log.info("Lock acquired: {}",
            lockName);
        // trylock과의 차이: 락을 비블로킹방식으로 시도. 실패시 false반환 대기시간을 설정해 락을 시도할수 있다.
        // 락을 반드시 획득할 필요는 없고, 락을 얻지 못할 경우 대체 동작을 수행할 수 있는 경우에 유용하다.
        // 예를 들어, 락을 얻지 못하면 다른 작업을 수행하거나, 이후에 다시 시도할 수 있습니다
        try {
            return joinPoint.proceed();//타겟 메서드를 실행
        } finally {
            lock.unlock();// 락해제- 락의 해제 시점이 비즈니스로직 트랜잭션 커밋이후가 되도록함.
            // 만약 락의 해제 시점이 비즈니스 로직 트랜잭션 커밋 이전에 발생하면
            // 비즈니스 로직 트랜잭션이 커밋되기 이전에 다른 스레드에서 락을 얻어 비즈니스 로직을 수행할 수 있기때문
            log.info("Lock released: {}", lockName);
        }
    }
}
