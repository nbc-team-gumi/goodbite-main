package site.mygumi.goodbite.domain.auth.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import site.mygumi.goodbite.domain.auth.dto.KakaoUserResponseDto;
import site.mygumi.goodbite.domain.auth.service.AuthService;
import site.mygumi.goodbite.common.response.DataResponseDto;
import site.mygumi.goodbite.common.response.MessageResponseDto;
import site.mygumi.goodbite.common.response.ResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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

    @GetMapping("/")
    public ResponseEntity<MessageResponseDto> checkConnection() {
        return ResponseUtil.of(HttpStatus.OK, "통신 성공");
    }

    @PostMapping("/users/refresh")
    public ResponseEntity<MessageResponseDto> updateAccessToken(HttpServletRequest request,
        HttpServletResponse response) throws UnsupportedEncodingException {
        authService.updateAccessToken(request, response);
        return ResponseUtil.createOk();
    }

    // 카카오 서버로부터 받은 인가 코드 전달 후 인증 처리 및 JWT 반환
    @GetMapping("/users/kakao/callback")
    public ResponseEntity<DataResponseDto<KakaoUserResponseDto>> kakaoLogin(
        @RequestParam String code, @RequestParam Boolean owner,
        HttpServletResponse response) throws JsonProcessingException, UnsupportedEncodingException {
        return ResponseUtil.findOk(authService.kakaoLogin(code, owner, response));
    }
}
