package com.sparta.goodbite.auth.security;

import com.sparta.goodbite.auth.util.JwtUtil;
import com.sparta.goodbite.common.response.ResponseUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

// JWT 인가 필터 사용자 정의
@Slf4j(topic = "JWT 검증 및 인가")
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final EmailUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
        HttpServletRequest req, HttpServletResponse res, FilterChain filterChain)
        throws ServletException, IOException {

        String accessToken = JwtUtil.getAccessTokenFromRequest(req);

        if (accessToken != null) {
            // prefix 제거
            accessToken = JwtUtil.substringToken(accessToken);

            // 유효하지 않은 액세스 토큰
            if (!JwtUtil.isTokenValid(accessToken)) {
                ResponseUtil.servletApi(res, HttpStatus.UNAUTHORIZED.value(), "토큰이 유효하지 않습니다.");
                return;
            }

            // 액세스 토큰은 맞지만, 만료된 토큰
            if (JwtUtil.isTokenExpired(accessToken)) {

                // 리프레시 토큰 검증
                String refreshToken = JwtUtil.getRefreshTokenFromRequest(req);
                if (refreshToken != null && !JwtUtil.isTokenValid(refreshToken)) {

                    String email = JwtUtil.getEmailFromToken(accessToken);
                    String role = JwtUtil.getAuthorityFromToken(accessToken);

                    // JWT 재발급
                    String newAccessToken = JwtUtil.createAccessToken(email, role);
                    JwtUtil.addJwtToCookie(newAccessToken, res);

                    accessToken = JwtUtil.substringToken(newAccessToken);
                } else {

                    // 리프레시 토큰이 만료되거나 존재하지 않습니다.
                    // 액세스 토큰 재발급 불가
                    // 재로그인 요청

                    // 액세스 토큰, 리프레시 토큰 쿠키 삭제
                    JwtUtil.deleteAccessTokenFromCookie(res);
                    JwtUtil.deleteRefreshTokenFromCookie(res);
                    ResponseUtil.servletApi(res, HttpStatus.UNAUTHORIZED.value(),
                        "토큰이 만료되었습니다. 다시 로그인해주세요.");
                    return;
                }
            }

            setAuthentication(
                JwtUtil.getEmailFromToken(accessToken),
                JwtUtil.getAuthorityFromToken(accessToken)
            );
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
        UserDetails userDetails = userDetailsService.loadUserByEmail(email, role);
        return new UsernamePasswordAuthenticationToken(
            userDetails,
            null,
            userDetails.getAuthorities()
        );
    }
}
