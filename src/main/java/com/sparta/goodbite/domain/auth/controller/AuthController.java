package com.sparta.goodbite.domain.auth.controller;

import com.sparta.goodbite.domain.auth.service.AuthService;
import com.sparta.goodbite.common.response.MessageResponseDto;
import com.sparta.goodbite.common.util.ResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/users/refresh")
    public ResponseEntity<MessageResponseDto> updateAccessToken(HttpServletRequest request,
        HttpServletResponse response) {
        authService.updateAccessToken(request, response);
        return ResponseUtil.createOk();
    }
}
