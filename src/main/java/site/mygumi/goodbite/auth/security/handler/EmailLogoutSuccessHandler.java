package site.mygumi.goodbite.auth.security.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import site.mygumi.goodbite.auth.security.util.JwtUtil;
import site.mygumi.goodbite.common.response.ResponseUtil;

/**
 * 로그아웃 성공 시 동작을 정의하는 {@link LogoutSuccessHandler} 구현체입니다.
 * <p>
 * 이 클래스는 로그아웃 요청에 대해 HTTP 응답을 설정하며, 요청에 액세스 토큰이 없으면 "로그인 상태가 아닙니다"라는 메시지를 반환하고, 액세스 토큰이 있을 경우 "로그아웃
 * 완료" 메시지를 반환합니다.
 * </p>
 *
 * @author a-white-bit
 */
public class EmailLogoutSuccessHandler implements LogoutSuccessHandler {

    /**
     * 로그아웃 성공 시 호출되어 적절한 메시지를 응답으로 전송합니다.
     * <p>
     * 요청에 액세스 토큰이 없으면 "로그인 상태가 아닙니다"라는 메시지와 함께 {@code 401 UNAUTHORIZED} 상태를 반환하고, 액세스 토큰이 있을 경우
     * "로그아웃 완료" 메시지와 함께 {@code 200 OK} 상태를 반환합니다.
     * </p>
     *
     * @param request        HTTP 요청 객체
     * @param response       HTTP 응답 객체
     * @param authentication 인증 객체 (로그아웃 시 사용되지 않음)
     * @throws IOException      입출력 예외
     * @throws ServletException 서블릿 예외
     */
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
