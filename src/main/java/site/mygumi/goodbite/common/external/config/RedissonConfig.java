package site.mygumi.goodbite.common.external.config;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.RequiredArgsConstructor;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RedissonConfig {

    private final Dotenv dotenv;

    @Bean
    public RedissonClient redissonClient() {
        String redisAddress = dotenv.get("REDIS_SERVER");
        //String redisPassword = dotenv.get("REDIS_PASSWORD");
        String redisPort = dotenv.get("REDIS_PORT");

        Config config = new Config();
        config.useSingleServer().setAddress("redis://" + redisAddress + ":" + redisPort);
        //    .setPassword(redisPassword);  // Redis 서버에 설정된 비밀번호
        return Redisson.create(config);
    }

}
