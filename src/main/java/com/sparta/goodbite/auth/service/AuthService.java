package com.sparta.goodbite.auth.service;

import com.sparta.goodbite.auth.util.JwtUtil;
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

        if (refreshToken == null || !JwtUtil.isTokenValid(refreshToken)) {
            throw new InvalidRefreshTokenException(AuthErrorCode.INVALID_REFRESH_TOKEN);
        }

        String email = JwtUtil.getEmailFromToken(refreshToken);
        String authority = JwtUtil.getAuthorityFromToken(refreshToken);

        String newAccessToken = JwtUtil.createAccessToken(email, authority);
        JwtUtil.addJwtToCookie(newAccessToken, response);
    }

    public void updateRefreshToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = JwtUtil.getRefreshTokenFromRequest(request);

        if (refreshToken == null || !JwtUtil.isTokenValid(refreshToken)) {
            throw new InvalidRefreshTokenException(AuthErrorCode.INVALID_REFRESH_TOKEN);
        }

        String newRefreshToken = JwtUtil.createRefreshToken(
            JwtUtil.getEmailFromToken(refreshToken));
        JwtUtil.addJwtToCookie(newRefreshToken, response);
    }
}
