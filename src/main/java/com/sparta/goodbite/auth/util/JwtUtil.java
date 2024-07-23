package com.sparta.goodbite.auth.util;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

// Util Class, static class
@Slf4j(topic = "JwtUtil")
public final class JwtUtil {

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String AUTHORIZATION_KEY = "auth";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS256;

    // 외부에서 객체 생성 불가
    private JwtUtil() {
    }

    // JWT 생성하기
    public static String createAccessToken(String email, String authority) {
        Date date = new Date();

        String createdToken = BEARER_PREFIX +
            Jwts.builder()
                .setSubject(email) // 사용자 식별자값(ID)
                .claim(AUTHORIZATION_KEY, authority) // 권한 (USER / ADMIN)
                .setExpiration(new Date(date.getTime() + 1000 * 60 * 60)) // 토큰 만료 시간
                .setIssuedAt(date) // 발급일
                .signWith(JwtConfig.key, SIGNATURE_ALGORITHM) // 암호화 알고리즘
                .compact();

        log.info("사용자 토큰 생성: {}", createdToken);
        return createdToken;
    }

    // JWT Cookie 에 저장
    public static void addJwtToCookie(String token, HttpServletResponse res) {
        try {
            // Cookie Value 에는 공백이 불가능해서 encoding 진행
            // URLEncoder.encode()는 퍼센트('%') 인코딩되지 않고 '+'으로 인코딩되므로 이것을 다시 퍼센트 인코딩으로 바꿔줌
            token = URLEncoder.encode(token, "utf-8").replaceAll("\\+", "%20");
            log.info("URL encoding: {}", token);

            // 쿠키 생성
            Cookie cookie = new Cookie(AUTHORIZATION_HEADER, token); // Name-Value
            cookie.setPath("/");

            // Response 객체에 Cookie 추가
            res.addCookie(cookie);

        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage());
        }
    }

    // JWT 검증 (토큰 기한 만료, 위변조)
    public static boolean validateToken(String token) {
        try {
            // Jwts.parser() is deprecated
            // Jwts.parserBuilder() 사용
            Jwts.parserBuilder().setSigningKey(JwtConfig.key).build().parseClaimsJws(token);
            log.info("토큰 검증 완료: {}", token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.error("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.");
        } catch (ExpiredJwtException e) {
            log.error("Expired JWT token, 만료된 JWT token 입니다.");
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");
        } catch (IllegalArgumentException e) {
            log.error("JWT claims is empty, 잘못된 JWT 토큰 입니다.");
        }
        return false;
    }

    // JWT에서 사용자 정보(Claims)에서 email 가져오기
    public static String getEmailFromToken(String token) {
        // 토큰에서 사용자 정보 뽑는 방법
        // getBody() -> Claims : 토큰에 여러 클레임(key-value) 존재
        // getSubject() -> Subject: JWT의 식별자, email으로 세팅되어있음
        return Jwts.parserBuilder().setSigningKey(JwtConfig.key).build().parseClaimsJws(token)
            .getBody().getSubject();
    }

    // HttpServletRequest 에서 Cookie Value : JWT 가져오기
    public static String getTokenFromRequest(HttpServletRequest req) {
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(AUTHORIZATION_HEADER)) {
                    try {
                        return URLDecoder.decode(cookie.getValue(),
                            "UTF-8"); // Encode 되어 넘어간 Value 다시 Decode
                    } catch (UnsupportedEncodingException e) {
                        return null;
                    }
                }
            }
        }
        return null;
    }

    // JWT 추출 (Bearer 제거)
    public static String substringToken(String token) {
        // PREFIX (Bearer)가 일치해야 하고, 일치한다면 제거
        if (StringUtils.hasText(token) && token.startsWith(BEARER_PREFIX)) {
            return token.substring(7);
        } else {
            log.error("잘못된 JWT 토큰입니다. : {}", token);
            throw new NullPointerException("잘못된 JWT 토큰입니다.");
        }
    }
}