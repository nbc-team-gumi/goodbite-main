package com.sparta.goodbite.auth.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sparta.goodbite.auth.dto.KakaoUserResponseDto;
import com.sparta.goodbite.auth.service.AuthService;
import com.sparta.goodbite.common.response.DataResponseDto;
import com.sparta.goodbite.common.response.MessageResponseDto;
import com.sparta.goodbite.common.response.ResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j(topic = "AuthController")
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/users/refresh")
    public ResponseEntity<MessageResponseDto> updateAccessToken(HttpServletRequest request,
        HttpServletResponse response) throws UnsupportedEncodingException {
        authService.updateAccessToken(request, response);
        return ResponseUtil.createOk();
    }

    @GetMapping("/users/kakao/callback")
    public ResponseEntity<DataResponseDto<KakaoUserResponseDto>> kakaoLogin(
        @RequestParam String code,
        HttpServletResponse response) throws JsonProcessingException {
        log.info("API request OK: {}", code);
        // code: 카카오 서버로부터 받은 인가 코드 Service 전달 후 인증 처리 및 JWT 반환
        return ResponseUtil.findOk(authService.kakaoLogin(code));
    }
}
