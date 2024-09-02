package site.mygumi.goodbite.aspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD) //어노테이션이 메서드에 지정됨을 의미
@Retention(RetentionPolicy.RUNTIME) //어노텐이션이 런타임까지 유지됨을 의미
public @interface RedisLock { //커스텀 어노테이션 정의

    String key(); // 메서드마다 고유한 키를 지정하기 위한 속성

    // 락을 얻기 위해 기다릴 수 있는 시간
    long waitTime() default 5L;

    // 락 획득 후 임대할 수 있는 시간
    long leaseTime() default 3L;
}
