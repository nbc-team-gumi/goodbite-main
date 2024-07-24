package com.sparta.goodbite.auth.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.goodbite.auth.dto.LoginRequestDto;
import com.sparta.goodbite.auth.util.JwtUtil;
import com.sparta.goodbite.common.response.ResponseUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// JWT 인증 필터
@Slf4j(topic = "로그인 및 JWT 생성")
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    public JwtAuthenticationFilter() {
        // 이 필터가 다음 엔드포인트 POST 요청을 처리하도록 설정
        setFilterProcessesUrl("/users/login");
    }

    @Override
    public Authentication attemptAuthentication(
        HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        log.debug("로그인 시도");

        try {
            // 이 필터가 서블릿 요청을 가로채 로그인 처리를 수행
            LoginRequestDto requestDto = new ObjectMapper().readValue(request.getInputStream(),
                LoginRequestDto.class);

            // 사용자 Role을 GrantedAuthority로 변환
            String role = requestDto.getRole();
            Collection<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority(role));

            // 사용자 인증 정보 매니저에게 위임
            return getAuthenticationManager().authenticate(
                new UsernamePasswordAuthenticationToken(
                    requestDto.getEmail(),
                    requestDto.getPassword(),
                    authorities
                )
            );
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
        HttpServletResponse response, FilterChain chain, Authentication authResult)
        throws IOException, ServletException {

        log.debug("로그인 성공 및 액세스 토큰, 리프레시 토큰 생성");

        String email = ((EmailUserDetails) authResult.getPrincipal()).getEmail();
        String role = ((EmailUserDetails) authResult.getPrincipal()).getRole();

        String accessToken = JwtUtil.createAccessToken(email, role);
        String refreshToken = JwtUtil.createRefreshToken(email);

        JwtUtil.addJwtToCookie(accessToken, response);
        JwtUtil.addJwtToCookie(refreshToken, response);

        ResponseUtil.servletApi(response, HttpStatus.OK.value(), "로그인 성공");
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request,
        HttpServletResponse response, AuthenticationException failed)
        throws IOException, ServletException {

        log.debug("로그인 실패");

        int status = switch (failed) {
            case BadCredentialsException badCredentialsException -> HttpStatus.UNAUTHORIZED.value();
            case DisabledException disabledException -> HttpStatus.FORBIDDEN.value();
            case LockedException lockedException -> HttpStatus.LOCKED.value();
            case null, default -> HttpStatus.INTERNAL_SERVER_ERROR.value();
        };

        ResponseUtil.servletApi(response, status, "로그인 실패");
    }
}