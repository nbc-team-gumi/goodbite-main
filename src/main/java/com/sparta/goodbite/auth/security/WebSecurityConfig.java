package com.sparta.goodbite.auth.security;

import com.sparta.goodbite.auth.UserRoleEnum;
import com.sparta.goodbite.auth.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity // Spring Security 사용
public class WebSecurityConfig {

    // Bean 객체 authenticationConfiguration 으로부터 인증매니저를 get 가능 : getAuthenticationManager()
    private final AuthenticationConfiguration authenticationConfiguration;
    private final EmailUserDetailsService userDetailsService;
    private final GlobalAccessDeniedHandler accessDeniedHandler;
    private final GlobalAuthenticationEntryPoint authenticationEntryPoint;

    // Manager Bean 등록
    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // PasswordEncoder 필요
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // JWT 인증 필터 Bean 등록
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception {
        return new JwtAuthenticationFilter(authenticationManager(), userDetailsService);
    }

    // JWT 인가 필터 Bean 등록
    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter() {
        return new JwtAuthorizationFilter(userDetailsService);
    }

    // 로그아웃 핸들러 Bean 등록
    @Bean
    public LogoutSuccessHandler logoutSuccessHandler() {
        SimpleUrlLogoutSuccessHandler handler = new SimpleUrlLogoutSuccessHandler();
        // 로그아웃 성공시 로그인 페이지로 리디렉션
        handler.setDefaultTargetUrl("/users/login?logout");
        return handler;
    }

    // 시큐리티 필터 체인 설정 Bean 등록
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            // CSRF 설정: CSRF 보호 비활성 (보안 취약)
            .csrf((csrf) -> csrf.disable())

            // CSRF 설정: 로그인 엔드포인트에 대해 CSRF 보호 비활성화
            //.csrf(csrf -> csrf
            //.ignoringRequestMatchers("/users/login"));

            // 세션을 사용하지 않도록 정책 STATELESS 로 변경
            .sessionManagement((sessionManagement) -> sessionManagement
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // 인가 설정
            .authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests
                .requestMatchers("/",
                    "/customers/signup",
                    "/owners/signup",
                    "/users/login")
                .permitAll()
                .requestMatchers("/admin/**").hasRole(UserRoleEnum.ADMIN.name())
                .requestMatchers("/owner/**").hasRole(UserRoleEnum.OWNER.name())
                .requestMatchers("/customer/**").hasRole(UserRoleEnum.CUSTOMER.name())
                .anyRequest().authenticated())

            // 기본 폼 로그인을 비활성화, 중복 인증 방지
            .formLogin((formLogin) -> formLogin.disable())

            // 로그아웃 설정
            .logout(logout -> logout
                .logoutUrl("/users/logout")
                .logoutSuccessHandler(logoutSuccessHandler())
                .deleteCookies(JwtUtil.AUTHORIZATION_HEADER))

            // 사용자 세부 정보 서비스를 명시적으로 지정
            .userDetailsService(userDetailsService)
            
            // 예외 처리 핸들러
            .exceptionHandling((exceptionHandling) -> exceptionHandling
                .accessDeniedHandler(accessDeniedHandler) // 접근 거부(인가 실패) 시 처리
                .authenticationEntryPoint(authenticationEntryPoint)) // 인증 실패 시 처리

            // 커스텀 필터 끼우기
            // JWT 인가필터 -> JWT 인증필터 -> UsernamePasswordAuthenticationFilter 순으로 설정
            .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(jwtAuthorizationFilter(), JwtAuthenticationFilter.class);

        return http.build();
    }
}