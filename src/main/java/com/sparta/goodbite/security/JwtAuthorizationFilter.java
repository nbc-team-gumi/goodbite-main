package com.sparta.goodbite.security;

import com.sparta.goodbite.common.util.JwtUtil;
import com.sparta.goodbite.common.util.ResponseUtil;
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
    private final List<String> excludedPaths = List.of(
        "/users/login", "/customers/signup",
        "/owners/signup", "/admins/signup");
    private final PathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected void doFilterInternal(
        HttpServletRequest req, HttpServletResponse res, FilterChain filterChain)
        throws ServletException, IOException {

        String requestPath = req.getRequestURI();
        String accessToken = JwtUtil.getAccessTokenFromRequest(req);

        // 로그인, 회원가입 페이지는 통과
        boolean isExcludePath = excludedPaths.stream()
            .anyMatch(path -> pathMatcher.match(path, requestPath));
        if (accessToken == null || isExcludePath) {
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

    // 인증 처리
    private void setAuthentication(String email, String role) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = createAuthentication(email, role);
        context.setAuthentication(authentication);

        SecurityContextHolder.setContext(context);
    }

    // 인증 객체 생성
    private Authentication createAuthentication(String email, String role) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        return new UsernamePasswordAuthenticationToken(
            userDetails,
            null,
            userDetails.getAuthorities()
        );
    }
}
