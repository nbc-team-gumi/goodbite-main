package site.mygumi.goodbite.security.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import site.mygumi.goodbite.common.response.ResponseUtil;

/**
 * 인증되지 않은 사용자가 보호된 자원에 접근할 때 호출되는 {@link AuthenticationEntryPoint} 구현체입니다.
 * <p>
 * 이 클래스는 인증이 필요한 자원에 접근하려는 요청에 대해 {@code 401 Unauthorized} 상태와 "사용자 인증이 필요합니다."라는 메시지를 응답으로 전송합니다.
 * </p>
 *
 * @author a-white-bit
 */
@Component
public class GlobalAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /**
     * 인증되지 않은 사용자가 접근할 때 호출되어 인증 요구 응답을 전송합니다.
     * <p>
     * 보호된 자원에 대해 인증이 필요하다는 응답으로 {@code 401 Unauthorized} 상태 코드와 메시지를 전송하여 사용자에게 인증 필요성을 알립니다.
     * </p>
     *
     * @param request       HTTP 요청 객체
     * @param response      HTTP 응답 객체
     * @param authException 인증 예외 객체
     * @throws IOException      입출력 예외
     * @throws ServletException 서블릿 예외
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
        AuthenticationException authException) throws IOException, ServletException {
        ResponseUtil.servletApi(response, HttpStatus.UNAUTHORIZED.value(), "사용자 인증이 필요합니다.");
    }
}