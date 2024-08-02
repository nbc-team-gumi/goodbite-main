package com.sparta.goodbite.auth.util;

import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.security.Key;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {

    public static Key key;
    public static String serverIp;
    // 환경변수 넣어주기 위한 config class
    @Value("${JWT_SECRET_KEY}") // BASE64 encoded
    private String SECRET_KEY;

    // Aws 클라우드 서버
    @Value("${EC2_HOST}")
    private String EC2_HOST;

    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(SECRET_KEY);
        key = Keys.hmacShaKeyFor(bytes);
        serverIp = EC2_HOST;
    }
}
