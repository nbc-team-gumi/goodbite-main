package site.mygumi.goodbite.common.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 환경 변수 파일(.env)을 로드하는 설정 클래스입니다.
 * <p>
 * 이 클래스는 {@link Dotenv} 라이브러리를 사용하여 .env 파일의 내용을 로드하고, Spring 애플리케이션 내에서 사용할 수 있도록 {@code Dotenv}
 * 객체를 생성하여 빈으로 등록합니다.
 * </p>
 *
 * @author a-white-bit
 */
@Configuration
public class DotenvConfig {

    /**
     * {@link Dotenv} 객체를 생성하여 환경 변수 파일(.env)을 로드하고 빈으로 등록합니다.
     *
     * @return 로드된 환경 변수를 포함하는 {@code Dotenv} 객체
     */
    @Bean
    public Dotenv dotenv() {
        return Dotenv.configure().load();
    }
}