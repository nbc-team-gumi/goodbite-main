package site.mygumi.goodbite.domain.auth.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import site.mygumi.goodbite.domain.auth.dto.KakaoUserResponseDto;
import site.mygumi.goodbite.domain.user.customer.repository.CustomerRepository;
import site.mygumi.goodbite.domain.user.entity.UserCredentials;
import site.mygumi.goodbite.domain.user.entity.UserRole;
import site.mygumi.goodbite.domain.user.owner.repository.OwnerRepository;
import site.mygumi.goodbite.exception.auth.AuthErrorCode;
import site.mygumi.goodbite.exception.auth.detail.InvalidRefreshTokenException;
import site.mygumi.goodbite.security.util.JwtUtil;

/**
 * 사용자의 인증 인가 처리 요청을 수행하는 클래스입니다. 이 클래스는 토큰 리프레시, 카카오 콜백 처리 등의 메서드를 포함하고 있습니다.
 */
@Slf4j(topic = "AuthService")
@Service
@RequiredArgsConstructor
public class AuthService {

    private final RestTemplate restTemplate;
    private final CustomerRepository customerRepository;
    private final OwnerRepository ownerRepository;
    private final Dotenv dotenv;

    /**
     * 사용자 리프레시 토큰이 유효한지 검증하고 액세스 토큰을 재발급합니다. 만료되거나, 존재하지 않는 경우 토큰 재발급이 불가능합니다.
     *
     * @param request  서블릿 요청 객체
     * @param response 서블릿 응답 객체
     * @throws UnsupportedEncodingException 토큰 문자열이 인코딩 불가능한 경우
     * @throws InvalidRefreshTokenException 리프레시 토큰 유효 검증 실패
     */
    public void updateAccessToken(HttpServletRequest request,
        HttpServletResponse response) throws UnsupportedEncodingException {
        String refreshToken = JwtUtil.getRefreshTokenFromRequest(request);

        // 유효성 검증
        if (refreshToken == null || !JwtUtil.isTokenValid(refreshToken)) {
            if (refreshToken != null) {
                JwtUtil.deleteAccessTokenFromCookie(response);
                JwtUtil.deleteRefreshTokenFromCookie(response);
            }
            throw new InvalidRefreshTokenException(AuthErrorCode.INVALID_REFRESH_TOKEN);
        }

        // 액세스 토큰 재발급
        String email = JwtUtil.getEmailFromToken(refreshToken);
        String authority = JwtUtil.getAuthorityFromToken(refreshToken);

        String newAccessToken = JwtUtil.createAccessToken(email, authority);
        JwtUtil.addJwtToHeader(newAccessToken, response);
    }

    /**
     * 카카오 서버로부터 받은 인가 코드를 전달 후 인증 처리 및 JWT 반환합니다.
     *
     * @param code     카카오 로그인 API 요청 코드
     * @param isOwner  true: owner, false: customer
     * @param response 서블릿 응답 객체
     * @return 사용자 카카오 닉네임, 이메일
     * @throws JsonProcessingException      직렬화/역직렬화 등의 JSON 처리 발생 예외
     * @throws UnsupportedEncodingException 토큰 문자열이 인코딩 불가능한 경우
     */
    public KakaoUserResponseDto kakaoLogin(String code, Boolean isOwner,
        HttpServletResponse response)
        throws JsonProcessingException, UnsupportedEncodingException {

        // 카카오 액세스 토큰 요청
        String kakaoAccessToken = getToken(code);

        // 토큰으로 카카오 API 호출 : "액세스 토큰"으로 "카카오 사용자 정보" 가져오기
        KakaoUserResponseDto userResponseDto = getKakaoUserInfo(kakaoAccessToken);
        UserCredentials user;
        String role;

        if (isOwner) {
            user = ownerRepository.findByEmail(userResponseDto.email()).orElse(null);
            role = UserRole.OWNER.getAuthority();
        } else {
            user = customerRepository.findByEmail(userResponseDto.email()).orElse(null);
            role = UserRole.CUSTOMER.getAuthority();
        }

        if (user != null) {
            String accessToken = JwtUtil.createAccessToken(user.getEmail(), role);
            String refreshToken = JwtUtil.createRefreshToken(user.getEmail(), role);

            JwtUtil.addJwtToHeader(accessToken, response);
            JwtUtil.addJwtToHeader(refreshToken, response);
        }

        return userResponseDto;
    }

    /**
     * 카카오 서버로부터 받은 인가 코드로 액세스 토큰을 생성합니다. 구성해야하는 HTTP 구성 정보는 https://developers.kakao.com/ 에서 확인할 수
     * 있습니다.
     *
     * @param code 카카오 로그인 API 요청 코드
     * @return 카카오 로그인 액세스 토큰
     * @throws JsonProcessingException 직렬화/역직렬화 등의 JSON 처리 발생 예외
     */
    private String getToken(String code) throws JsonProcessingException {

        // 요청 URL
        URI uri = UriComponentsBuilder
            .fromUriString("https://kauth.kakao.com")
            .path("/oauth/token")
            .encode()
            .build()
            .toUri();

        // HTTP Header
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP Body
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", dotenv.get("KAKAO_API_KEY"));
        body.add("redirect_uri", dotenv.get("DOMAIN_URL") + "/kakao/callback");
        body.add("code", code);

        RequestEntity<MultiValueMap<String, String>> requestEntity = RequestEntity
            .post(uri)
            .headers(headers)
            .body(body);

        // 카카오 서버에 HTTP 요청
        ResponseEntity<String> response = restTemplate.exchange(
            requestEntity,
            String.class
        );

        // HTTP 응답 (JSON) -> 액세스 토큰 파싱
        JsonNode jsonNode = new ObjectMapper().readTree(response.getBody());
        return jsonNode.get("access_token").asText();
    }

    /**
     * 카카오 로그인 액세스 토큰으로 카카오 서버에게 사용자 정보를 요청합니다.
     *
     * @param accessToken 카카오 로그인 액세스 토큰
     * @return 사용자 카카오 닉네임, 이메일
     * @throws JsonProcessingException 직렬화/역직렬화 등의 JSON 처리 발생 예외
     */
    private KakaoUserResponseDto getKakaoUserInfo(String accessToken)
        throws JsonProcessingException {

        // 요청 URL
        URI uri = UriComponentsBuilder
            .fromUriString("https://kapi.kakao.com")
            .path("/v2/user/me")
            .encode()
            .build()
            .toUri();

        // HTTP Header
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        RequestEntity<MultiValueMap<String, String>> requestEntity = RequestEntity
            .post(uri)
            .headers(headers)
            .body(new LinkedMultiValueMap<>());

        // 카카오 서버에 HTTP 요청
        ResponseEntity<String> response = restTemplate.exchange(
            requestEntity,
            String.class
        );

        JsonNode jsonNode = new ObjectMapper().readTree(response.getBody());
        String nickname = jsonNode.get("properties")
            .get("nickname").asText();
        String email = jsonNode.get("kakao_account")
            .get("email").asText();

        return KakaoUserResponseDto.from(nickname, email);
    }

}
