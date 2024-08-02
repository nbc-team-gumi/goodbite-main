package com.sparta.goodbite.domain.auth.service;

import com.sparta.goodbite.common.util.JwtUtil;
import com.sparta.goodbite.exception.auth.AuthErrorCode;
import com.sparta.goodbite.exception.auth.detail.InvalidRefreshTokenException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    public void updateAccessToken(HttpServletRequest request,
        HttpServletResponse response) {
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
        JwtUtil.addJwtToCookie(newAccessToken, response);
    }
}
