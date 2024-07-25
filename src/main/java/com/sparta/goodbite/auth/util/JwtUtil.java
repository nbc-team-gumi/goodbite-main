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
    public static final String REFRESH_HEADER = "Refresh";
    public static final String AUTHORIZATION_KEY = "auth";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS256;

    // 외부에서 객체 생성 불가
    private JwtUtil() {
    }

    // 액세스 토큰 생성
    // Prefix: Bearer
    public static String createAccessToken(String email, String authority) {
        Date date = new Date();

        String createdToken = BEARER_PREFIX +
            Jwts.builder()
                .setSubject(email) // 사용자 식별자값(ID)
                .claim(AUTHORIZATION_KEY, authority) // 권한 (ROLE_CUSTOMER / ROLE_OWNER / ROLE_ADMIN)
                .setExpiration(new Date(date.getTime() + 1000 * 60 * 60)) // 1시간
                .setIssuedAt(date) // 발급일
                .signWith(JwtConfig.key, SIGNATURE_ALGORITHM) // 암호화 알고리즘
                .compact();

        log.debug("사용자 토큰 생성: {}", createdToken);
        return createdToken;
    }

    // 리프레시 토큰 생성
    // Prefix: 없음
    public static String createRefreshToken(String email, String authority) {
        Date date = new Date();

        return Jwts.builder()
            .setSubject(email)
            .claim(AUTHORIZATION_KEY, authority) // 권한 (ROLE_CUSTOMER / ROLE_OWNER / ROLE_ADMIN)
            .setExpiration(new Date(date.getTime() + 1000 * 60 * 60 * 24 * 7)) // 7일
            .setIssuedAt(date)
            .signWith(JwtConfig.key, SIGNATURE_ALGORITHM)
            .compact();
    }

    // JWT Cookie 에 저장
    public static void addJwtToCookie(String token, HttpServletResponse res) {
        try {
            Cookie cookie;

            // 토큰 종류 확인
            if (isAccessToken(token)) {
                // 인코딩 사용 이유:
                // Cookie Value 에는 공백 포함 불가
                // URLEncoder.encode(): 공백 -> '+'으로 인코딩
                // 퍼센트('%') 인코딩으로 변환
                token = URLEncoder.encode(token, "utf-8").replaceAll("\\+", "%20");

                // 쿠키 생성
                cookie = new Cookie(AUTHORIZATION_HEADER, token); // Name-Value
            } else {
                cookie = new Cookie(REFRESH_HEADER, token); // Name-Value
            }
            log.debug("URL encoding: {}", token);
            cookie.setPath("/");
            cookie.setHttpOnly(true); // 클라이언트 JavaScript 에서 쿠키 접근 불가
            //cookie.setSecure(true); // HTTPS 사용 시 Secure 설정

            // Response 객체에 Cookie 추가
            res.addCookie(cookie);

        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage());
        }
    }

    // JWT 위변조, 만료 검증
    public static boolean isTokenValid(String token) {
        try {
            return isTokenValidOrExpired(token);
        } catch (ExpiredJwtException e) {
            return false;
        }
    }

    // JWT 위변조 검증, 만료시 ExpiredJwtException throw
    public static boolean isTokenValidOrExpired(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(JwtConfig.key).build().parseClaimsJws(token);
            log.debug("토큰 검증 완료: {}", token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.error("Invalid JWT signature, 유효하지 않은 JWT 서명 입니다.");
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");
        } catch (IllegalArgumentException e) {
            log.error("JWT claims is empty, 잘못된 JWT 토큰 입니다.");
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT token, 만료된 JWT token 입니다.");
            throw e;
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

    // JWT에서 사용자 정보(Claims)에서 authority 가져오기
    public static String getAuthorityFromToken(String token) {
        // 토큰에서 사용자 정보 뽑는 방법
        // getBody() -> Claims : 토큰에 여러 클레임(key-value) 존재
        return Jwts.parserBuilder().setSigningKey(JwtConfig.key).build().parseClaimsJws(token)
            .getBody().get(AUTHORIZATION_KEY, String.class);
    }

    // HttpServletRequest 에서 Cookie Value : Access Token 가져오기
    public static String getAccessTokenFromRequest(HttpServletRequest req) {
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

    // HttpServletRequest 에서 Cookie Value : Refresh Token 가져오기
    public static String getRefreshTokenFromRequest(HttpServletRequest req) {
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(REFRESH_HEADER)) {
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
        if (isAccessToken(token)) {
            return token.substring(7);
        } else {
            log.error("잘못된 JWT 토큰입니다. : {}", token);
            throw new NullPointerException("잘못된 JWT 토큰입니다.");
        }
    }

    // 액세스 토큰 확인 (Bearer)
    public static Boolean isAccessToken(String token) {
        return StringUtils.hasText(token) && token.startsWith(BEARER_PREFIX);
    }

    // 액세스 토큰 쿠키 삭제
    public static void deleteAccessTokenFromCookie(HttpServletResponse res) {
        Cookie cookie = new Cookie(AUTHORIZATION_HEADER, null);
        cookie.setPath("/");
        cookie.setMaxAge(0); // 쿠키 삭제를 위한 MaxAge 설정
        res.addCookie(cookie);
    }

    // 리프레시 토큰 쿠키 삭제
    public static void deleteRefreshTokenFromCookie(HttpServletResponse res) {
        Cookie cookie = new Cookie(REFRESH_HEADER, null);
        cookie.setPath("/");
        cookie.setMaxAge(0); // 쿠키 삭제를 위한 MaxAge 설정
        res.addCookie(cookie);
    }
}