package site.mygumi.goodbite.config.security;

import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.security.Key;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class JwtConfig {

    public static Key key;
    private final Dotenv dotenv;

    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(dotenv.get("JWT_SECRET_KEY"));
        key = Keys.hmacShaKeyFor(bytes);
    }
}
