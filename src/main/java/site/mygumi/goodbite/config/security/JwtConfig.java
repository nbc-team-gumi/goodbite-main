package site.mygumi.goodbite.config.security;

import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.security.Key;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

/**
 * JWT(Json Web Token) 설정을 관리하는 구성 클래스입니다.
 * <p>
 * 이 클래스는 환경 변수 파일(.env)에서 JWT 비밀 키를 가져와 초기화하며, 해당 키를 전역적으로 사용할 수 있도록 설정합니다.
 * </p>
 *
 * <p>사용 예시:
 * <pre>
 * Key signingKey = JwtConfig.key;
 * </pre>
 * </p>
 *
 * @author a-white-bit
 */
@Configuration
@RequiredArgsConstructor
public class JwtConfig {

    public static Key key;
    private final Dotenv dotenv;

    /**
     * JWT 비밀 키를 초기화하는 메서드입니다.
     * <p>
     * 애플리케이션 시작 시 환경 변수 파일(.env)에서 비밀 키를 가져와 {@code key} 필드를 초기화하며, JWT 서명에 사용할 {@link Key} 객체를
     * 생성합니다.
     * </p>
     */
    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(dotenv.get("JWT_SECRET_KEY"));
        key = Keys.hmacShaKeyFor(bytes);
    }
}
