package com.sparta.goodbite.auth.security;

import com.sparta.goodbite.auth.util.JwtUtil;
import com.sparta.goodbite.common.response.ResponseUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

public class EmailLogoutSuccessHandler implements LogoutSuccessHandler {

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) throws IOException, ServletException {

        if (JwtUtil.getAccessTokenFromRequest(request) == null) {
            ResponseUtil.servletApi(response, HttpStatus.UNAUTHORIZED.value(), "로그인 상태가 아닙니다.");
        } else {
            ResponseUtil.servletApi(response, HttpStatus.OK.value(), "로그아웃 완료");
        }
    }
}
