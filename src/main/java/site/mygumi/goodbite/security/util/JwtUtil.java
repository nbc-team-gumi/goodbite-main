package site.mygumi.goodbite.security.util;

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
import site.mygumi.goodbite.config.security.JwtConfig;

/**
 * JWT 생성, 검증 및 추출과 관련된 유틸리티 클래스입니다.
 * <p>
 * 이 클래스는 정적 메서드를 통해 JWT 토큰을 생성, 헤더 및 쿠키에 추가/삭제, 유효성 검증 및 사용자 정보 추출 등의 기능을 제공합니다. Access Token과
 * Refresh Token 생성 및 관리에 필요한 다양한 메서드를 포함하고 있습니다.
 * </p>
 *
 * <p>사용 예시:
 * <pre>
 * String accessToken = JwtUtil.createAccessToken(email, authority);
 * JwtUtil.addJwtToHeader(accessToken, response);
 * </pre>
 * </p>
 *
 * @author a-whit-bit
 */
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

    /**
     * Access Token을 생성합니다. Bearer prefix를 포함합니다.
     *
     * @param email     사용자 이메일
     * @param authority 사용자 권한
     * @return 생성된 Access Token 문자열
     */
    public static String createAccessToken(String email, String authority) {
        return BEARER_PREFIX + createToken(email, authority, 1000L * 60 * 60); // 1시간
    }

    /**
     * Refresh Token을 생성합니다. Bearer prefix를 포함하지 않습니다.
     *
     * @param email     사용자 이메일
     * @param authority 사용자 권한
     * @return 생성된 Refresh Token 문자열
     */
    public static String createRefreshToken(String email, String authority) {
        return createToken(email, authority, 1000L * 60 * 60 * 24 * 7); // 7일
    }

    /**
     * JWT를 HTTP 응답 헤더에 추가합니다.
     * <p>Access Token의 경우, UTF-8로 URL 인코딩한 후 `Authorization` 헤더에 추가됩니다.
     * Refresh Token은 `Refresh` 헤더에 추가됩니다.</p>
     *
     * @param token 추가할 JWT 토큰
     * @param res   응답 객체
     * @throws UnsupportedEncodingException 인코딩 예외
     */
    public static void addJwtToHeader(String token, HttpServletResponse res)
        throws UnsupportedEncodingException {
        try {
            // 토큰 종류 확인
            if (isAccessToken(token)) {
                // 인코딩 사용 이유:
                // Cookie Value 에는 공백 포함 불가
                // URLEncoder.encode(): 공백 -> '+'으로 인코딩
                // 퍼센트('%') 인코딩으로 변환
                token = URLEncoder.encode(token, "utf-8").replaceAll("\\+", "%20");

                // 응답 헤더에 JWT 추가
                res.addHeader(AUTHORIZATION_HEADER, token);
            } else {
                res.addHeader(REFRESH_HEADER, token);
            }

        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage());
        }
    }

    /**
     * JWT를 HTTP 쿠키에 추가합니다.
     *
     * @param token 추가할 JWT 토큰
     * @param res   응답 객체
     */
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
//            cookie.setDomain(JwtConfig.serverIp);
            cookie.setPath("/");
//            cookie.setHttpOnly(true); // 클라이언트 JavaScript 에서 쿠키 접근 불가
//            cookie.setSecure(true); // HTTPS를 통해서만 전송
            cookie.setSecure(false); // HTTP에서도 사용 가능
            cookie.setAttribute("SameSite", "Lax"); // SameSite 속성 Lax

            // Response 객체에 Cookie 추가
            res.addCookie(cookie);

//            // SameSite 설정 을 위한 Set-Cookie 설정
//            res.addHeader("Set-Cookie",
//                String.format("%s=%s; Path=/; HttpOnly; Secure; SameSite=None",
//                    AUTHORIZATION_HEADER, URLEncoder.encode(token, "UTF-8")));

        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage());
        }
    }

    /**
     * JWT가 유효한지 검증합니다.
     *
     * @param token 검증할 JWT 토큰
     * @return 유효할 경우 true, 유효하지 않을 경우 false
     */
    public static boolean isTokenValid(String token) {
        try {
            return isTokenValidOrExpired(token);
        } catch (ExpiredJwtException e) {
            return false;
        }
    }

    /**
     * JWT가 유효한지 검증하고, 만료 시 예외를 던집니다.
     *
     * @param token 검증할 JWT 토큰
     * @return 유효할 경우 true, 유효하지 않을 경우 false
     * @throws ExpiredJwtException 토큰 만료 예외
     */
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

    /**
     * JWT에서 사용자 이메일을 추출합니다.
     *
     * @param token 사용자 이메일을 추출할 JWT 토큰
     * @return 사용자 이메일
     */
    public static String getEmailFromToken(String token) {
        // 토큰에서 사용자 정보 뽑는 방법
        // getBody() -> Claims : 토큰에 여러 클레임(key-value) 존재
        // getSubject() -> Subject: JWT의 식별자, email으로 세팅되어있음
        return Jwts.parserBuilder().setSigningKey(JwtConfig.key).build().parseClaimsJws(token)
            .getBody().getSubject();
    }

    /**
     * JWT에서 사용자 권한을 추출합니다.
     *
     * @param token 사용자 권한을 추출할 JWT 토큰
     * @return 사용자 권한
     */
    public static String getAuthorityFromToken(String token) {
        // 토큰에서 사용자 정보 뽑는 방법
        // getBody() -> Claims : 토큰에 여러 클레임(key-value) 존재
        return Jwts.parserBuilder().setSigningKey(JwtConfig.key).build().parseClaimsJws(token)
            .getBody().get(AUTHORIZATION_KEY, String.class);
    }

    /**
     * HTTP 요청에서 Access Token을 가져옵니다.
     *
     * @param req 서블릿 요청 객체
     * @return Access Token 문자열, 존재하지 않을 경우 null
     */
    public static String getAccessTokenFromRequest(HttpServletRequest req) {
        // 헤더에서 REFRESH_HEADER 정보를 가져옴
        String accessToken = req.getHeader(AUTHORIZATION_HEADER);

        if (accessToken == null) {
            return null;
        }

        try {
            return URLDecoder.decode(accessToken, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return null;
        }

//        Cookie[] cookies = req.getCookies();
//        if (cookies != null) {
//            for (Cookie cookie : cookies) {
//                if (cookie.getName().equals(AUTHORIZATION_HEADER)) {
//                    try {
//                        return URLDecoder.decode(cookie.getValue(),
//                            "UTF-8"); // Encode 되어 넘어간 Value 다시 Decode
//                    } catch (UnsupportedEncodingException e) {
//                        return null;
//                    }
//                }
//            }
//        }
//        return null;
    }

    /**
     * HTTP 요청에서 Refresh Token을 가져옵니다.
     *
     * @param req 서블릿 요청 객체
     * @return Refresh Token 문자열, 존재하지 않을 경우 null
     */
    public static String getRefreshTokenFromRequest(HttpServletRequest req) {
        // 헤더에서 REFRESH_HEADER 정보를 가져옴
        String refreshToken = req.getHeader(REFRESH_HEADER);

        if (refreshToken == null) {
            return null;
        }

        try {
            return URLDecoder.decode(refreshToken, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return null;
        }

//        Cookie[] cookies = req.getCookies();
//        if (cookies != null) {
//            for (Cookie cookie : cookies) {
//                if (cookie.getName().equals(REFRESH_HEADER)) {
//                    try {
//                        return URLDecoder.decode(cookie.getValue(),
//                            "UTF-8"); // Encode 되어 넘어간 Value 다시 Decode
//                    } catch (UnsupportedEncodingException e) {
//                        return null;
//                    }
//                }
//            }
//        }
//        return null;
    }

    /**
     * Bearer Prefix를 제거한 JWT를 반환합니다.
     *
     * @param token Bearer Prefix가 포함된 JWT 토큰
     * @return Bearer Prefix가 제거된 JWT 토큰
     */
    public static String substringToken(String token) {
        // PREFIX (Bearer)가 일치해야 하고, 일치한다면 제거
        if (isAccessToken(token)) {
            return token.substring(7);
        } else {
            log.error("잘못된 JWT 토큰입니다. : {}", token);
            throw new NullPointerException("잘못된 JWT 토큰입니다.");
        }
    }

    /**
     * 토큰이 Access Token인지 확인합니다.
     *
     * @param token 확인할 토큰
     * @return Access Token일 경우 true, 그렇지 않으면 false
     */
    public static Boolean isAccessToken(String token) {
        return StringUtils.hasText(token) && token.startsWith(BEARER_PREFIX);
    }

    /**
     * 응답에서 Access Token 쿠키를 삭제합니다.
     *
     * @param res 응답 객체
     */
    public static void deleteAccessTokenFromCookie(HttpServletResponse res) {
        Cookie cookie = new Cookie(AUTHORIZATION_HEADER, null);
        cookie.setPath("/");
        cookie.setMaxAge(0); // 쿠키 삭제를 위한 MaxAge 설정
        res.addCookie(cookie);
    }

    /**
     * 응답에서 Refresh Token 쿠키를 삭제합니다.
     *
     * @param res 응답 객체
     */
    public static void deleteRefreshTokenFromCookie(HttpServletResponse res) {
        Cookie cookie = new Cookie(REFRESH_HEADER, null);
        cookie.setPath("/");
        cookie.setMaxAge(0); // 쿠키 삭제를 위한 MaxAge 설정
        res.addCookie(cookie);
    }

    /**
     * 주어진 정보로 JWT를 생성합니다.
     *
     * @param email      사용자 이메일
     * @param authority  사용자 권한
     * @param expiration 만료 시간
     * @return 생성된 JWT 문자열
     */
    private static String createToken(String email, String authority, Long expiration) {
        Date date = new Date();

        return Jwts.builder()
            .setSubject(email)
            .claim(AUTHORIZATION_KEY, authority) // 권한 (ROLE_CUSTOMER / ROLE_OWNER / ROLE_ADMIN)
            .setExpiration(new Date(date.getTime() + expiration))
            .setIssuedAt(date)
            .signWith(JwtConfig.key, SIGNATURE_ALGORITHM)
            .compact();
    }
}