package site.mygumi.goodbite.domain.auth.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import site.mygumi.goodbite.common.response.DataResponseDto;
import site.mygumi.goodbite.common.response.MessageResponseDto;
import site.mygumi.goodbite.common.response.ResponseUtil;
import site.mygumi.goodbite.domain.auth.dto.KakaoUserResponseDto;
import site.mygumi.goodbite.domain.auth.service.AuthService;

/**
 * 사용자의 인증-인가 처리 요청을 서비스 클래스로 전달하는 컨트롤러 클래스입니다. 이 클래스는 토큰 리프레시, 카카오 콜백 처리 등의 메서드를 포함하고 있습니다.
 */
@Slf4j(topic = "AuthController")
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("/")
    public ResponseEntity<MessageResponseDto> checkConnection() {
        return ResponseUtil.of(HttpStatus.OK, "통신 성공");
    }

    /**
     * 사용자 액세스 토큰의 리프레시를 요청합니다.
     *
     * @param request  서블릿 요청 객체
     * @param response 서블릿 반환 객체
     * @return 반환 코드, 메시지를 담은 DTO
     * @throws UnsupportedEncodingException 토큰 문자열이 인코딩 불가능한 경우
     */
    @PostMapping("/users/refresh")
    public ResponseEntity<MessageResponseDto> updateAccessToken(HttpServletRequest request,
        HttpServletResponse response) throws UnsupportedEncodingException {
        authService.updateAccessToken(request, response);
        return ResponseUtil.createOk();
    }

    /**
     * 카카오 서버로부터 받은 인가 코드를 전달 후 인증 처리 및 JWT 반환합니다.
     *
     * @param code     카카오 서버발 인가 코드
     * @param isOwner  true: owner, false: customer
     * @param response 서블릿 반환 객체
     * @return 반환 코드, 메시지를 담은 DTO
     * @throws JsonProcessingException      직렬화/역직렬화 등의 JSON 처리 발생 예외
     * @throws UnsupportedEncodingException 토큰 문자열이 인코딩 불가능한 경우
     */
    @GetMapping("/users/kakao/callback")
    public ResponseEntity<DataResponseDto<KakaoUserResponseDto>> kakaoLogin(
        @RequestParam String code, @RequestParam Boolean isOwner,
        HttpServletResponse response) throws JsonProcessingException, UnsupportedEncodingException {
        return ResponseUtil.findOk(authService.kakaoLogin(code, isOwner, response));
    }
}
