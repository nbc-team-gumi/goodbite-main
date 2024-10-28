package site.mygumi.goodbite.config.security;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.server.Ssl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * SSL(Secure Sockets Layer) 설정을 관리하는 구성 클래스입니다.
 * <p>
 * 이 클래스는 환경 변수 파일(.env)에서 SSL 관련 정보를 가져와 {@link Ssl} 객체를 초기화하고, SSL 설정을 위한 빈으로 등록합니다. 이를 통해 SSL
 * 인증서를 기반으로 보안을 강화할 수 있습니다.
 * </p>
 *
 * @author a-white-bit
 */
@Configuration
@RequiredArgsConstructor
public class SslConfig {

    private final Dotenv dotenv;

    /**
     * SSL 설정을 위한 {@link Ssl} 객체를 초기화하고 빈으로 등록합니다.
     * <p>
     * 환경 변수에서 키스토어 경로, 패스워드 등을 가져와 {@code Ssl} 객체의 속성을 설정합니다. 키스토어 경로와 패스워드가 정의되지 않은 경우 기본 경로 및
     * 비밀번호를 사용합니다.
     * </p>
     *
     * @return SSL 설정이 적용된 {@code Ssl} 객체
     */
    @Bean
    public Ssl ssl() {
        Ssl ssl = new Ssl();
        ssl.setKeyStore(dotenv.get("SSL_KEYSTORE_PATH", "classpath:/api.goodbite.site.p12"));
        ssl.setKeyStorePassword(dotenv.get("SSL_KEY"));
        ssl.setKeyAlias("goodbite");
        ssl.setKeyStoreType("PKCS12");
        return ssl;
    }
}