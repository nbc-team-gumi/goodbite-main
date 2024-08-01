package com.sparta.goodbite.auth.security;

import com.sparta.goodbite.auth.UserRole;
import com.sparta.goodbite.auth.util.JwtUtil;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.web.filter.CorsFilter;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity // @PreAuthorize 애너테이션 활성화
@EnableWebSecurity // Spring Security 사용
public class WebSecurityConfig {

    // Bean 객체 authenticationConfiguration 으로부터 인증매니저를 get 가능 : getAuthenticationManager()
    private final AuthenticationConfiguration authenticationConfiguration;
    private final EmailUserDetailsService userDetailsService;
    private final GlobalAccessDeniedHandler accessDeniedHandler;
    private final GlobalAuthenticationEntryPoint authenticationEntryPoint;
    private final CorsFilter corsFilter;

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

    // Validator 유효성 검증
    @Bean
    public Validator validator() {
        return Validation.buildDefaultValidatorFactory().getValidator();
    }

    // JWT 인증 필터 Bean 등록
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception {
        return new JwtAuthenticationFilter(authenticationManager(), userDetailsService,
            validator());
    }

    // JWT 인가 필터 Bean 등록
    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter() {
        return new JwtAuthorizationFilter(userDetailsService);
    }

    // 로그아웃 핸들러 Bean 등록
    @Bean
    public LogoutSuccessHandler logoutSuccessHandler() {

//        // 로그아웃 페이지로 리디렉션 (프론트 개발시 고려)
//        SimpleUrlLogoutSuccessHandler handler = new SimpleUrlLogoutSuccessHandler();
//        handler.setDefaultTargetUrl("/users/login?logout");
//        return handler;

        return new EmailLogoutSuccessHandler();
    }

//    @Bean
//    public CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration configuration = new CorsConfiguration();
//
//        // 출처 허용
//        configuration.addAllowedOrigin("http://localhost:8080");
//        // 모든 http 메서드 허용
//        configuration.addAllowedMethod("*");
//        // 모든 헤더 허용
//        configuration.addAllowedHeader("*");
//        // 자격 증명 허용
//        configuration.setAllowCredentials(true);
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", configuration);
//        return source;
//    }

    // 시큐리티 필터 체인 설정 Bean 등록
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            // CSRF 설정: CSRF 보호 비활성 (보안 취약)
            .csrf((csrf) -> csrf.disable())

            // CSRF 설정: 로그인 엔드포인트에 대해 CSRF 보호 비활성화
            //.csrf(csrf -> csrf
            //.ignoringRequestMatchers("/users/login"));

            // CORS 설정
//            .cors((cors) -> cors.configurationSource(corsConfigurationSource()))
            .addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)

            // 세션을 사용하지 않도록 정책 STATELESS 로 변경
            .sessionManagement((sessionManagement) -> sessionManagement.sessionCreationPolicy(
                SessionCreationPolicy.STATELESS))

            // 인가 설정
            .authorizeHttpRequests(
                (authorizeHttpRequests) -> authorizeHttpRequests
                    .requestMatchers("/", "/owners/signup", "/customers/signup", "/users/login",
                        "/users/refresh",
                        "/error")
                    .permitAll()
                    .requestMatchers("/admins/**").hasRole(UserRole.ADMIN.name())
                    .requestMatchers("/owners/**").hasRole(UserRole.OWNER.name())
                    .requestMatchers("/customers/**").hasRole(UserRole.CUSTOMER.name())
                    .requestMatchers(HttpMethod.GET, "/menus/**").permitAll() // 메뉴 조회는 모두 가능
                    .requestMatchers(HttpMethod.GET, "/reviews/**").permitAll() // 리뷰 조회는 모두 가능
                    .requestMatchers(HttpMethod.GET, "/restaurants/**")
                    .permitAll() // 레스토랑 조회는 모두 가능
                    .anyRequest().authenticated())

            // 기본 폼 로그인을 비활성화, 중복 인증 방지
            .formLogin((formLogin) -> formLogin.disable())

            // 로그아웃 설정
            .logout(logout -> logout.logoutUrl("/users/logout")
                .logoutSuccessHandler(logoutSuccessHandler())
                .deleteCookies(JwtUtil.AUTHORIZATION_HEADER, JwtUtil.REFRESH_HEADER))

            // 사용자 세부 정보 서비스를 명시적으로 지정
            .userDetailsService(userDetailsService)

            // 예외 처리 핸들러
            .exceptionHandling((exceptionHandling) -> exceptionHandling.accessDeniedHandler(
                    accessDeniedHandler) // 접근 거부(인가 실패) 시 처리
                .authenticationEntryPoint(authenticationEntryPoint)) // 인증 실패 시 처리

            // 커스텀 필터 끼우기
            // JWT 인가필터 -> LogoutFilter -> JWT 인증필터 -> UsernamePasswordAuthenticationFilter 순으로 설정
            .addFilterBefore(jwtAuthorizationFilter(), LogoutFilter.class)
            .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}