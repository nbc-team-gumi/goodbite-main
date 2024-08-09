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
    public Object around(ProceedingJoinPoint joinPoint, RedisLock redisLock) throws Throwable {
        String lockName = redisLock.value(); //어노테이션의 값을 가져온다.
        RLock lock = redissonClient.getLock(lockName); //락객체생성
        log.info("Attempting to acquire lock: {}", lockName);
        lock.lock(); //락획득
        log.info("Lock acquired: {}", lockName);
        try {
            return joinPoint.proceed();//메서드를 실행
        } finally {
            lock.unlock();//락해제
            log.info("Lock released: {}", lockName);
        }
    }
}
