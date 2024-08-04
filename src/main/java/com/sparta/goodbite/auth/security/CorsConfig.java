package com.sparta.goodbite.auth.security;

import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    private final List<String> origins = List.of(
        "http://localhost:3000");

    @Bean
    public CorsFilter corsFilter() {

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(origins);
        config.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type"));
        config.setExposedHeaders(List.of("Set-Cookie")); // 클라이언트에서 접근 가능한 헤더
        config.setAllowedMethods(List.of("*"));
        config.setAllowCredentials(true);
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}