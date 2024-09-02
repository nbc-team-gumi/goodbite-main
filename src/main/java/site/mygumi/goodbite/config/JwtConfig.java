package site.mygumi.goodbite.config;

import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.security.Key;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {

    public static Key key;

    // 환경변수 넣어주기 위한 config class
    @Value("${JWT_SECRET_KEY}") // BASE64 encoded
    private String SECRET_KEY;

    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(SECRET_KEY);
        key = Keys.hmacShaKeyFor(bytes);
    }
}
