package site.mygumi.goodbite.security.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import site.mygumi.goodbite.common.response.ResponseUtil;

/**
 * 인증된 사용자가 접근 권한이 없는 자원에 접근하려 할 때 호출되는 {@link AccessDeniedHandler} 구현체입니다.
 * <p>
 * 이 클래스는 권한이 없는 사용자에게 접근이 금지된 자원에 대해 {@code 403 Forbidden} 상태와 "허가되지 않은 페이지 접근입니다."라는 메시지를 응답으로
 * 전송합니다.
 * </p>
 *
 * @author a-white-bit
 */
@Component
public class GlobalAccessDeniedHandler implements AccessDeniedHandler {

    /**
     * 인증된 사용자가 인가되지 않은 자원에 접근할 때 호출됩니다.
     * <p>
     * 접근이 금지된 자원에 대해 {@code 403 Forbidden} 상태 코드와 함께 적절한 메시지를 응답으로 전송합니다.
     * </p>
     *
     * @param request               HTTP 요청 객체
     * @param response              HTTP 응답 객체
     * @param accessDeniedException 접근 거부 예외
     * @throws IOException      입출력 예외
     * @throws ServletException 서블릿 예외
     */
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
        AccessDeniedException accessDeniedException) throws IOException, ServletException {
        ResponseUtil.servletApi(response, HttpStatus.FORBIDDEN.value(), "허가되지 않은 페이지 접근입니다.");
    }
}