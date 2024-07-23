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

// WebSecurityConfig 에 의해 Bean 등록됨
// JWT 인증 필터
@Slf4j(topic = "로그인 및 JWT 생성")
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    /*
     * UsernamePasswordAuthenticationFilter :
     * 기본적으로 '/login' POST 요청을 처리하며 별도의 Spring MVC Controller 를 지정하지 않아도 응답 처리 해줌
     */

    public JwtAuthenticationFilter() {
        // /users/login 엔드포인트로 들어오는 POST 요청은 이 필터 JwtAuthenticationFilter 에 의해 처리됨
        // 바꿔 말하면 다른 API 요청은 이 필터를 무시함
        setFilterProcessesUrl("/users/login");
    }

    /**
     * 로그인 시도
     * <p>
     * 사용자로부터 Email, Password 를 받아 저장합니다.
     *
     * @param request  서블릿 요청 객체
     * @param response 서블릿 응답 객체
     * @return 사용자 인증 객체
     */
    @Override
    public Authentication attemptAuthentication(
        HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        log.info("로그인 시도");
        try {
            LoginRequestDto requestDto = new ObjectMapper().readValue(request.getInputStream(),
                LoginRequestDto.class);

            // 사용자 Role을 GrantedAuthority로 변환
            String role = requestDto.getRole();
            Collection<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority(role));

            // 사용자 정보 반환
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

        log.info("로그인 성공 및 JWT 생성");

        String email = ((EmailUserDetails) authResult.getPrincipal()).getEmail();
        String role = ((EmailUserDetails) authResult.getPrincipal()).getRole();

        String token = JwtUtil.createAccessToken(email, role);
        JwtUtil.addJwtToCookie(token, response);

        ResponseUtil.servletApi(response, HttpStatus.OK.value(), "로그인 성공");
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request,
        HttpServletResponse response, AuthenticationException failed)
        throws IOException, ServletException {

        log.info("로그인 실패");

        int status = switch (failed) {
            case BadCredentialsException badCredentialsException -> HttpStatus.UNAUTHORIZED.value();
            case DisabledException disabledException -> HttpStatus.FORBIDDEN.value();
            case LockedException lockedException -> HttpStatus.LOCKED.value();
            case null, default -> HttpStatus.INTERNAL_SERVER_ERROR.value();
        };

        ResponseUtil.servletApi(response, HttpStatus.BAD_REQUEST.value(), "로그인 실패");
    }
}