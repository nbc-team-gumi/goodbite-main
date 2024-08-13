package com.sparta.goodbite.common;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class RedissonConfig {

    @Autowired
    private Environment env;

    @Bean
    public RedissonClient redissonClient() {
        String redisAddress = env.getProperty("spring.data.redis.host");
        String redisPassword = env.getProperty("spring.data.redis.password");
        String redisPort = env.getProperty("spring.data.redis.port");

        Config config = new Config();
        config.useSingleServer().setAddress("redis://" + redisAddress + ":" + redisPort)
            .setPassword(redisPassword);  // Redis 서버에 설정된 비밀번호
        return Redisson.create(config);
    }

}
