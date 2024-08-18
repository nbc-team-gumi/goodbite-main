package com.sparta.goodbite.auth.security;

import com.sparta.goodbite.auth.util.JwtUtil;
import com.sparta.goodbite.common.response.ResponseUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

// JWT 인가 필터 사용자 정의
@Slf4j(topic = "JWT 검증 및 인가")
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final EmailUserDetailsService userDetailsService;
    private final PathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected void doFilterInternal(
        HttpServletRequest req, HttpServletResponse res, FilterChain filterChain)
        throws ServletException, IOException {

        String requestPath = req.getRequestURI();
        String method = req.getMethod();
        String accessToken = JwtUtil.getAccessTokenFromRequest(req);

        if (isExcludedPath(requestPath) || isGetMethodExcludedPath(requestPath, method)
            || accessToken == null) {
            filterChain.doFilter(req, res);
            return;
        }

        // prefix 제거
        accessToken = JwtUtil.substringToken(accessToken);

        // 유효하지 않은 액세스 토큰
        try {
            if (JwtUtil.isTokenValidOrExpired(accessToken)) {
                setAuthentication(
                    JwtUtil.getEmailFromToken(accessToken),
                    JwtUtil.getAuthorityFromToken(accessToken)
                );
            }
        } catch (ExpiredJwtException e) {
            ResponseUtil.servletApi(res, HttpStatus.UNAUTHORIZED.value(),
                "토큰이 만료되었습니다. 토큰을 재발급해주세요.");
            JwtUtil.deleteAccessTokenFromCookie(res);
            return;
        }

        filterChain.doFilter(req, res);
    }

    // 특정 경로가 제외 경로에 해당하는지 검사
    private boolean isExcludedPath(String requestPath) {
        List<String> excludedPaths = List.of(
            "/customers/signup",
            "/owners/signup",
            "/users/login",
            "/users/refresh",
            "/"
        );
        return excludedPaths.stream()
            .anyMatch(path -> pathMatcher.match(path, requestPath));
    }

    // GET 요청 중 인가가 필요한 경로 확인
    private boolean isGetMethodExcludedPath(String requestPath, String method) {
        if (!"GET".equalsIgnoreCase(method)) {
            return false; // GET 메서드가 아닌 경우 인가 필요
        }

        // 인가가 필요한 경로
        List<String> authorizedPaths = List.of(
            "/restaurants/my",
            "/restaurants/{restaurantId}/waitings",
            "/restaurants/{restaurantId}/reservations",
            "/reviews/my",
            "/waitings",
            "/waitings/{waitingId}",
            "/reservations/{reservationId}",
            "/reservations/my",
            "/owners",
            "/customers"
        );

        // 인가가 필요한 경로에 해당하지 않는 GET 메서드는 인가 패스
        return authorizedPaths.stream()
            .noneMatch(path -> pathMatcher.match(path, requestPath));
    }

    // 인증 처리
    private void setAuthentication(String email, String role) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = createAuthentication(email, role);
        context.setAuthentication(authentication);

        SecurityContextHolder.setContext(context);
    }

    // 인증 객체 생성
    private Authentication createAuthentication(String email, String role) {
        UserDetails userDetails = userDetailsService.loadUserByEmail(email, role);
        return new UsernamePasswordAuthenticationToken(
            userDetails,
            null,
            userDetails.getAuthorities()
        );
    }
}
