package site.mygumi.goodbite.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * HTTP 응답 쿠키에 `SameSite=None; Secure` 속성을 추가하는 필터입니다.
 * <p>
 * 이 클래스는 {@code OncePerRequestFilter}를 확장하여 모든 요청에 대해 한 번씩 실행됩니다. 응답에 설정된 쿠키가 존재할 경우
 * `SameSite=None; Secure` 속성을 추가하여 크로스 사이트 요청 시에도 쿠키가 전송될 수 있도록 설정합니다.
 * </p>
 *
 * @author a-white-bit
 */
public class SameSiteCookieFilter extends OncePerRequestFilter {

    /**
     * 요청을 필터링하며, 응답에 설정된 쿠키가 있을 경우 `SameSite=None; Secure` 속성을 추가합니다.
     * <p>
     * 이 메서드는 응답의 "Set-Cookie" 헤더가 존재할 경우 해당 헤더에 {@code SameSite=None; Secure} 속성을 추가합니다. 이를 통해 쿠키가
     * 크로스 사이트 요청에서도 전송될 수 있도록 설정합니다.
     * </p>
     *
     * @param request     HTTP 요청 객체
     * @param response    HTTP 응답 객체
     * @param filterChain 필터 체인 객체
     * @throws ServletException 서블릿 예외
     * @throws IOException      입출력 예외
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain)
        throws ServletException, IOException {
        filterChain.doFilter(request, response);

        if (response.getHeader("Set-Cookie") != null) {
            String header = response.getHeader("Set-Cookie");
            header = header + "; SameSite=None; Secure";
            response.setHeader("Set-Cookie", header);
        }
    }
}