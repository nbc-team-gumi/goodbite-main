package site.mygumi.goodbite.config.application;

import java.time.Duration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * 애플리케이션의 전반적인 설정을 정의하는 구성 클래스입니다.
 * <p>
 * 이 클래스는 외부 API 호출을 위한 {@link RestTemplate} 빈을 설정하며, 연결 시간과 읽기 시간의 초과 시간(timeout)을 설정하여 API 호출 시 무한
 * 대기 상태를 방지합니다.
 * </p>
 *
 * <p>사용 예시:
 * <pre>
 * {@Autowired}
 * private RestTemplate restTemplate;
 * </pre>
 * </p>
 *
 * @author a-white-bit
 */
@Configuration
public class AppConfig {

    /**
     * 외부 API 호출을 위한 {@link RestTemplate} 빈을 생성합니다.
     * <p>연결 시간 초과를 5초, 읽기 시간 초과를 5초로 설정하여, 응답 지연 시 무한 대기 상태를 방지합니다.</p>
     *
     * @param restTemplateBuilder RestTemplate 객체를 구성하는 빌더
     * @return 설정된 {@code RestTemplate} Bean
     */
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder
            // RestTemplate 으로 외부 API 호출 시 일정 시간이 지나도 응답이 없을 때
            // 무한 대기 상태 방지를 위해 강제 종료 설정
            .setConnectTimeout(Duration.ofSeconds(5)) // 5초
            .setReadTimeout(Duration.ofSeconds(5)) // 5초
            .build();
    }
}