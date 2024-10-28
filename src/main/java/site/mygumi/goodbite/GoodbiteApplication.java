package site.mygumi.goodbite;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * <p>
 * 이 클래스는 {@code main} 메서드를 통해 Spring Boot 애플리케이션을 시작하며, {@code @EnableJpaAuditing} 어노테이션을 통해 JPA의
 * 자동 감사(Auditing) 기능을 활성화합니다.
 * </p>
 *
 * @author a-white-bit
 * @version 0.0.1
 * @since 2024-10-28
 */
@SpringBootApplication
@EnableJpaAuditing
public class GoodbiteApplication {

    public static void main(String[] args) {
        SpringApplication.run(GoodbiteApplication.class, args);
    }
}