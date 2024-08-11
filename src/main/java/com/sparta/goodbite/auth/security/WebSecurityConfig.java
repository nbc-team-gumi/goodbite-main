package com.sparta.goodbite.auth.security;

import com.sparta.goodbite.auth.UserRole;
import com.sparta.goodbite.auth.util.JwtUtil;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
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

    @Value("${SUBDOMAIN_URL}")
    private String SUBDOMAIN_URL;

    @Value("${DOMAIN_URL}")
    private String DOMAIN_URL;

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

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowCredentials(true); // 자격 증명 허용
        config.addAllowedOrigin(SUBDOMAIN_URL); // 프론트엔드 서브도메인
        config.addAllowedOrigin(DOMAIN_URL); // 프론트엔드 도메인
        config.addAllowedHeader("*"); // 모든 헤더 허용
        config.addAllowedMethod("*"); // 모든 HTTP 메소드 허용
        config.addExposedHeader("Authorization"); // Authorization 헤더 노출
        config.addExposedHeader("Refresh"); // Refresh 헤더 노출
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
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

    // HTTPS 사용, 리디렉션
//    @Bean
//    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> servletContainer() {
//        return server -> {
//            server.addAdditionalTomcatConnectors(createHttpConnector());
//        };
//    }
//
//    private Connector createHttpConnector() {
//        Connector connector = new Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL);
//        connector.setScheme("http");
//        connector.setPort(8080);
//        connector.setSecure(false);
//        connector.setRedirectPort(443);
//        return connector;
//    }

    // 시큐리티 필터 체인 설정 Bean 등록
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            // HTTP -> HTTPS 리다이렉트
//            .requiresChannel(channel -> channel.anyRequest().requiresSecure())

            // CORS 설정: 사용자 재정의 cors 필터
            .addFilterBefore(corsFilter(), CorsFilter.class)

            // CSRF 설정: CSRF 보호 비활성 (보안 취약)
            .csrf((csrf) -> csrf.disable())

            // CSRF 설정: 로그인 엔드포인트에 대해 CSRF 보호 비활성화
            //.csrf(csrf -> csrf
            //.ignoringRequestMatchers("/users/login"));

            // 세션을 사용하지 않도록 정책 STATELESS 로 변경
            .sessionManagement((sessionManagement) -> sessionManagement.sessionCreationPolicy(
                SessionCreationPolicy.STATELESS))

            // 인가 설정
            .authorizeHttpRequests(
                (authorizeHttpRequests) -> authorizeHttpRequests
                    .requestMatchers(
                        "/customers/signup",
                        "/owners/signup",
                        "/users/login",
                        "/users/refresh",
                        "/error")
                    .permitAll()
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

            // 시큐리티 필터 순서
            // ChannelProcessingFilter : 채널 HTTP -> HTTPS 리디렉션
            // SecurityContextPersistenceFilter : SecurityContext 저장/로드
            // ConcurrentSessionFilter : 동시 세션 처리
            // HeaderWriterFilter : 응답 헤더 설정
            // CorsFilter : CORS 처리
            // + SameSiteCookieFilter : SameSite 쿠키 처리
            // CsrfFilter : CSRF 방지
            // LogoutFilter : 로그아웃 요청 처리
            // + JWTAuthenticationFilter : JWT 인증
            // UsernamePasswordAuthenticationFilter : 이름, 비밀번호 인증
            // + JWTAuthorizationFilter : JWT 인가
            // DefaultLoginPageGeneratingFilter (x) : 커스텀 설정
            // DefaultLogoutPageGeneratingFilter (x) : 커스텀 설정
            // BasicAuthenticationFilter : HTTP 기본 인증
            // RequestCacheAwareFilter : 요청 캐시
            // SecurityContextHolderAwareRequestFilter : SecurityContextHolder 에 인증 정보 전달
            // AnonymousAuthenticationFilter : 익명 사용자 인증
            // SessionManagementFilter : 세션 관리
            // ExceptionTranslationFilter : 예외처리, 필요한 경우 로그인 페이지 리다이렉트
            // FilterSecurityInterceptor (x) : authorizeHttpRequests 로 대체됨

            // 커스텀 필터
            .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
            .addFilterAfter(jwtAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
//            .addFilterAfter(new SameSiteCookieFilter(), CorsFilter.class);

        return http.build();
    }
}