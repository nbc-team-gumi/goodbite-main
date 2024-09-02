package site.mygumi.goodbite.domain.auth.security;

import site.mygumi.goodbite.common.response.ResponseUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Component
public class GlobalAccessDeniedHandler implements AccessDeniedHandler {

    // 인증된 사용자이지만, 인가되지 않은 접근일 때 호출됩니다.
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
        AccessDeniedException accessDeniedException) throws IOException, ServletException {
        ResponseUtil.servletApi(response, HttpStatus.FORBIDDEN.value(), "허가되지 않은 페이지 접근입니다.");
    }
}