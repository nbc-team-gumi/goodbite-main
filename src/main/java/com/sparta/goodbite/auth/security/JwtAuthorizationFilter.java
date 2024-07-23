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
import org.springframework.util.StringUtils;
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

        String tokenValue = JwtUtil.getTokenFromRequest(req);

        if (StringUtils.hasText(tokenValue)) {
            // JWT 토큰 substring
            tokenValue = JwtUtil.substringToken(tokenValue);

            if (!JwtUtil.validateToken(tokenValue)) {
                log.error("유효하지 않은 액세스 토큰: {}", tokenValue);
                ResponseUtil.servletApi(res, HttpStatus.UNAUTHORIZED.value(), "");
                return;
            }

            try {
                setAuthentication(
                    JwtUtil.getEmailFromToken(tokenValue),
                    JwtUtil.getAuthorityFromToken(tokenValue)
                );
            } catch (Exception e) {
                log.error(e.getMessage());
                ResponseUtil.servletApi(res, HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    e.getMessage());
                return;
            }
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
