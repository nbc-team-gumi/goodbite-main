package site.mygumi.goodbite.auth.security.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.io.IOException;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import site.mygumi.goodbite.auth.dto.LoginRequestDto;
import site.mygumi.goodbite.auth.dto.LoginSuccessResponseDto;
import site.mygumi.goodbite.auth.security.util.JwtUtil;
import site.mygumi.goodbite.common.response.ResponseUtil;
import site.mygumi.goodbite.domain.user.entity.EmailUserDetails;
import site.mygumi.goodbite.domain.user.entity.UserRole;

/**
 * JWT 인증을 처리하는 커스텀 필터 클래스입니다.
 * <p>
 * 이 클래스는 사용자가 로그인을 시도할 때 인증을 수행하고, 성공 시 JWT 액세스 토큰과 리프레시 토큰을 생성하여 응답 헤더에 추가합니다. 유효성 검증 및 로그인 실패 시
 * 오류 메시지와 상태 코드도 반환합니다.
 * </p>
 *
 * @author a-white-bit
 */
// JWT 인증 필터
@Slf4j(topic = "로그인 및 JWT 생성")
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final EmailUserDetailsService userDetailsService;
    private final Validator validator;

    /**
     * JwtAuthenticationFilter 생성자
     *
     * @param authenticationManager 인증 관리자
     * @param userDetailsService    사용자 상세 정보 서비스
     * @param validator             요청 DTO 검증기
     */
    public JwtAuthenticationFilter(AuthenticationManager authenticationManager,
        EmailUserDetailsService userDetailsService, Validator validator) {
        super.setAuthenticationManager(authenticationManager);
        this.userDetailsService = userDetailsService;
        this.validator = validator;

        // 이 필터가 다음 엔드포인트 POST 요청을 처리하도록 설정
        setFilterProcessesUrl("/users/login");
    }

    /**
     * 인증을 시도하고, 사용자 자격 증명을 검증하여 {@link Authentication} 객체를 반환합니다.
     * <p>
     * 로그인 요청 DTO를 검증하고, {@code EmailUserDetailsService}를 통해 사용자 정보를 조회한 후 인증 토큰을 생성하여 인증을 시도합니다.
     * </p>
     *
     * @param request  HTTP 요청 객체
     * @param response HTTP 응답 객체
     * @return 인증 성공 시 인증 객체, 실패 시 {@code null}
     * @throws AuthenticationException 인증 실패 시 발생하는 예외
     */
    @Override
    public Authentication attemptAuthentication(
        HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        try {
            // 이 필터가 서블릿 요청을 가로채 로그인 처리를 수행
            LoginRequestDto requestDto = new ObjectMapper().readValue(request.getInputStream(),
                LoginRequestDto.class);

            // Dto 유효성 검증
            String validationErrors = validateDto(requestDto);
            if (!validationErrors.isEmpty()) {
                log.error("유효성 검증 실패: {}", validationErrors);
                ResponseUtil.servletApi(response, HttpStatus.BAD_REQUEST.value(),
                    "유효성 검증 실패: " + validationErrors);
                return null;
            }

            String role;
            if (requestDto.isOwner()) {
                role = UserRole.OWNER.getAuthority();
            } else {
                role = UserRole.CUSTOMER.getAuthority();
            }

            UserDetails userDetails = userDetailsService.loadUserByEmail(requestDto.getEmail(),
                role);
            if (userDetails == null) {
                log.error("사용자를 찾을 수 없습니다.");
                ResponseUtil.servletApi(response, HttpStatus.BAD_REQUEST.value(),
                    "사용자를 찾을 수 없습니다.");
                return null;
            }

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                userDetails, requestDto.getPassword(), userDetails.getAuthorities());

            return this.getAuthenticationManager().authenticate(authenticationToken);

        } catch (IOException e) {
            log.error("로그인 시도 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 로그인 요청 DTO의 유효성을 검증합니다.
     *
     * @param requestDto 검증할 {@link LoginRequestDto} 객체
     * @return 유효하지 않은 필드가 있을 경우 오류 메시지, 유효한 경우 빈 문자열
     */
    private String validateDto(LoginRequestDto requestDto) {
        Set<ConstraintViolation<LoginRequestDto>> violations = validator.validate(requestDto);
        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<LoginRequestDto> violation : violations) {
                sb.append(violation.getMessage()).append(" ");
            }
            return sb.toString().trim();
        }
        return "";
    }

    /**
     * 인증 성공 시 호출되며, JWT 액세스 토큰과 리프레시 토큰을 생성하여 응답 헤더에 추가합니다.
     *
     * @param request    HTTP 요청 객체
     * @param response   HTTP 응답 객체
     * @param chain      필터 체인 객체
     * @param authResult 인증 성공 결과 객체
     * @throws IOException      입출력 예외
     * @throws ServletException 서블릿 예외
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request,
        HttpServletResponse response, FilterChain chain, Authentication authResult)
        throws IOException, ServletException {

        log.debug("로그인 성공 및 액세스 토큰, 리프레시 토큰 생성");

        String email = ((EmailUserDetails) authResult.getPrincipal()).getEmail();
        String role = ((EmailUserDetails) authResult.getPrincipal()).getRole();

        String accessToken = JwtUtil.createAccessToken(email, role);
        String refreshToken = JwtUtil.createRefreshToken(email, role);

        JwtUtil.addJwtToHeader(accessToken, response);
        JwtUtil.addJwtToHeader(refreshToken, response);

        // 사용자 역할 정보를 포함한 응답 생성
        LoginSuccessResponseDto responseDto = LoginSuccessResponseDto.from("로그인 성공", role);

        ResponseUtil.servletApi(response, HttpStatus.OK.value(), responseDto);
    }

    /**
     * 인증 실패 시 호출되며, 실패 메시지와 상태 코드를 응답으로 전송합니다.
     *
     * @param request  HTTP 요청 객체
     * @param response HTTP 응답 객체
     * @param failed   인증 실패 예외 객체
     * @throws IOException      입출력 예외
     * @throws ServletException 서블릿 예외
     */
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request,
        HttpServletResponse response, AuthenticationException failed)
        throws IOException, ServletException {

        log.info("로그인 실패: {}", failed.getMessage());

        int status = switch (failed) {
            case BadCredentialsException badCredentialsException -> HttpStatus.UNAUTHORIZED.value();
            case DisabledException disabledException -> HttpStatus.FORBIDDEN.value();
            case LockedException lockedException -> HttpStatus.LOCKED.value();
            case null, default -> HttpStatus.INTERNAL_SERVER_ERROR.value();
        };

        ResponseUtil.servletApi(response, status, "로그인 실패");
    }
}