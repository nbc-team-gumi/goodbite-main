package site.mygumi.goodbite.security;

import site.mygumi.goodbite.common.response.ResponseUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class GlobalAuthenticationEntryPoint implements AuthenticationEntryPoint {

    // 인증되지 않은 사용자가 접근 시에 호출됩니다.
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
        AuthenticationException authException) throws IOException, ServletException {
        ResponseUtil.servletApi(response, HttpStatus.UNAUTHORIZED.value(), "사용자 인증이 필요합니다.");
    }
}