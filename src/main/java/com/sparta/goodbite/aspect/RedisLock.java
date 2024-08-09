package com.sparta.goodbite.aspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//자체적으로는 아무런 기능을 하지 않으며, 어노테이션의 속성값을 정의
@Target(ElementType.METHOD) //어노테이션이 메서드에 지정됨을 의미
@Retention(RetentionPolicy.RUNTIME) //어노텐이션이 런타임까지 유지됨을 의미
public @interface RedisLock { //커스텀 어노테이션을 정의

    String value();//어노테이션에 설정할 값을 정의
}
