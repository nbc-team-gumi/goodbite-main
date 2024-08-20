package com.sparta.goodbite.auth.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.goodbite.auth.UserRole;
import com.sparta.goodbite.auth.dto.KakaoUserResponseDto;
import com.sparta.goodbite.auth.util.JwtUtil;
import com.sparta.goodbite.common.UserCredentials;
import com.sparta.goodbite.domain.customer.repository.CustomerRepository;
import com.sparta.goodbite.domain.owner.repository.OwnerRepository;
import com.sparta.goodbite.exception.auth.AuthErrorCode;
import com.sparta.goodbite.exception.auth.detail.InvalidRefreshTokenException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j(topic = "AuthService")
@Service
@RequiredArgsConstructor
public class AuthService {

    private final RestTemplate restTemplate;
    private final CustomerRepository customerRepository;
    private final OwnerRepository ownerRepository;

    @Value("${KAKAO_API_KEY}")
    private String kakaoApiKey;

    @Value("${DOMAIN_URL}")
    private String domainUrl;

    public void updateAccessToken(HttpServletRequest request,
        HttpServletResponse response) throws UnsupportedEncodingException {
        String refreshToken = JwtUtil.getRefreshTokenFromRequest(request);

        // 리프레시 토큰이 만료되거나 존재하지 않습니다.
        // 액세스 토큰 재발급 불가
        // 재로그인 요청

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

    private String getToken(String code) throws JsonProcessingException {
        // 구성해야하는 HTTP 구성 정보는 https://developers.kakao.com/ 에서 확인

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
        body.add("client_id", kakaoApiKey);
        body.add("redirect_uri", domainUrl + "/kakao/callback");
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
