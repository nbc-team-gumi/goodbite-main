package site.mygumi.goodbite.aspect.lock;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 분산 락을 위한 커스텀 어노테이션입니다. 이 어노테이션이 적용된 메서드는
 * Redis 락을 통해 동시 접근을 제어합니다.
 * <p>
 * Redis 기반의 락을 사용하여 특정 리소스에 대해 한 번에 하나의 스레드만 접근하도록
 * 설정할 수 있으며, 지정된 대기 시간 동안 락을 얻지 못하면 예외를 발생시킵니다.
 * </p>
 * <b>사용 예:</b>
 * <pre>
 *     {@code
 *     @RedisLock(key = "myResource", waitTime = 10L, leaseTime = 5L)
 *     public void someMethod() {
 *         // 메서드 구현
 *     }
 *     }
 * </pre>
 *
 * @author Kang Hyun Ji / Qwen
 * @see site.mygumi.goodbite.aspect.lock.RedisLockAspect
 */
@Target(ElementType.METHOD) //어노테이션이 메서드에 지정됨을 의미
@Retention(RetentionPolicy.RUNTIME) //어노텐이션이 런타임까지 유지됨을 의미
public @interface RedisLock { //커스텀 어노테이션 정의

    /**
     * 메서드마다 고유한 키를 지정하기 위한 속성.
     * 락이 적용될 리소스를 식별하기 위해 사용됩니다.
     *
     * @return 락의 고유 키
     */
    String key();

    /**
     * 락을 얻기 위해 대기할 최대 시간(초 단위).
     * 대기 시간 동안 락을 획득하지 못하면 락 획득에 실패합니다.
     * 기본값은 5초입니다.
     *
     * @return 락 대기 시간 (초)
     */
    long waitTime() default 5L;

    /**
     * 락 획득 후 임대할 수 있는 최대 시간(초 단위).
     * 지정된 시간이 지나면 락이 자동으로 해제됩니다.
     * 기본값은 3초입니다.
     *
     * @return 락 임대 시간 (초)
     */
    long leaseTime() default 3L;
}
