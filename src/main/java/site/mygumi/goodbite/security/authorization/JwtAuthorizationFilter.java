package site.mygumi.goodbite.security.authorization;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import site.mygumi.goodbite.common.response.ResponseUtil;
import site.mygumi.goodbite.security.authentication.EmailUserDetailsService;
import site.mygumi.goodbite.security.util.JwtUtil;

/**
 * JWT를 검증하고 인가 처리하는 사용자 정의 필터 클래스입니다.
 * <p>
 * 이 클래스는 {@code OncePerRequestFilter}를 확장하여 한 번의 요청당 한 번만 실행됩니다. 액세스 토큰을 검증하고 유효할 경우 인증을 설정하여
 * {@code SecurityContext}에 저장합니다. 특정 요청 경로는 인가 검증을 제외하도록 설정할 수 있습니다.
 * </p>
 *
 * @author a-whit-bit
 */
@Slf4j(topic = "JWT 검증 및 인가")
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final EmailUserDetailsService userDetailsService;
    private final PathMatcher pathMatcher = new AntPathMatcher();

    /**
     * 요청을 처리하며, JWT 검증 및 인가를 수행합니다.
     * <p>
     * 특정 경로에 대해 필터링을 제외하며, 만료되지 않은 토큰이 존재할 경우 인증을 설정합니다.
     * </p>
     *
     * @param req         HTTP 요청 객체
     * @param res         HTTP 응답 객체
     * @param filterChain 필터 체인 객체
     * @throws ServletException 서블릿 예외
     * @throws IOException      입출력 예외
     */
    @Override
    protected void doFilterInternal(
        HttpServletRequest req, HttpServletResponse res, FilterChain filterChain)
        throws ServletException, IOException {

        String requestPath = req.getRequestURI();
        String method = req.getMethod();
        String accessToken = JwtUtil.getAccessTokenFromRequest(req);

        if (isExcludedPath(requestPath) || isGetMethodExcludedPath(requestPath, method)
            || accessToken == null) {
            filterChain.doFilter(req, res);
            return;
        }

        // prefix 제거
        accessToken = JwtUtil.substringToken(accessToken);

        // 유효하지 않은 액세스 토큰
        try {
            if (JwtUtil.isTokenValidOrExpired(accessToken)) {
                setAuthentication(
                    JwtUtil.getEmailFromToken(accessToken),
                    JwtUtil.getAuthorityFromToken(accessToken)
                );
            }
        } catch (ExpiredJwtException e) {
            ResponseUtil.servletApi(res, HttpStatus.UNAUTHORIZED.value(),
                "토큰이 만료되었습니다. 토큰을 재발급해주세요.");
            JwtUtil.deleteAccessTokenFromCookie(res);
            return;
        }

        filterChain.doFilter(req, res);
    }

    /**
     * 요청 경로가 인가 검증을 제외해야 하는지 검사합니다.
     *
     * @param requestPath 요청 경로
     * @return 요청 경로가 제외 경로에 해당할 경우 {@code true}, 그렇지 않으면 {@code false}
     */
    private boolean isExcludedPath(String requestPath) {
        List<String> excludedPaths = List.of(
            "/customers/signup",
            "/owners/signup",
            "/users/login",
            "/users/refresh",
            "/"
        );
        return excludedPaths.stream()
            .anyMatch(path -> pathMatcher.match(path, requestPath));
    }

    /**
     * GET 메서드 요청이 인가 검증을 제외해야 하는지 검사합니다.
     * <p>
     * GET 메서드로 요청된 특정 경로는 인가 검증을 제외하여 인증을 수행하지 않습니다.
     * </p>
     *
     * @param requestPath 요청 경로
     * @param method      요청 메서드 (GET 요청만 적용)
     * @return 요청 경로가 인가 제외 경로에 해당할 경우 {@code true}, 그렇지 않으면 {@code false}
     */
    private boolean isGetMethodExcludedPath(String requestPath, String method) {
        if (!"GET".equalsIgnoreCase(method)) {
            return false; // GET 메서드가 아닌 경우 인가 필요
        }

        // 인가가 필요한 경로
        List<String> authorizedPaths = List.of(
            "/restaurants/my",
            "/restaurants/{restaurantId}/waitings",
            "/restaurants/{restaurantId}/reservations",
            "/reviews/my",
            "/waitings",
            "/waitings/{waitingId}",
            "/reservations/{reservationId}",
            "/reservations/my",
            "/owners",
            "/customers"
        );

        // 인가가 필요한 경로에 해당하지 않는 GET 메서드는 인가 패스
        return authorizedPaths.stream()
            .noneMatch(path -> pathMatcher.match(path, requestPath));
    }

    /**
     * 인증을 설정하여 {@code SecurityContext}에 저장합니다.
     *
     * @param email 인증할 사용자의 이메일
     * @param role  인증할 사용자의 역할
     */
    private void setAuthentication(String email, String role) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = createAuthentication(email, role);
        context.setAuthentication(authentication);

        SecurityContextHolder.setContext(context);
    }

    /**
     * 주어진 사용자 정보를 기반으로 {@link Authentication} 객체를 생성합니다.
     *
     * @param email 인증할 사용자의 이메일
     * @param role  인증할 사용자의 역할
     * @return 생성된 인증 객체
     */
    private Authentication createAuthentication(String email, String role) {
        UserDetails userDetails = userDetailsService.loadUserByEmail(email, role);
        return new UsernamePasswordAuthenticationToken(
            userDetails,
            null,
            userDetails.getAuthorities()
        );
    }
}
