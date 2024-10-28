package site.mygumi.goodbite.security.authentication;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

/**
 * 이메일 기반 인증 처리를 위한 커스텀 AuthenticationProvider 구현체입니다. 입력된 {@code Authentication} 객체를 검증 없이 그대로 반환하는
 * 구조입니다.
 * <p>
 * 이 클래스는 Spring Security의 {@link AuthenticationProvider} 인터페이스를 구현하며, 사용자로부터 받은
 * {@link Authentication} 객체를 처리하여 인증을 수행합니다.
 * </p>
 *
 * @author a-white-bit
 */
@Component
@RequiredArgsConstructor
public class EmailAuthenticationProvider implements AuthenticationProvider {

    /**
     * 입력된 Authentication 객체를 인증 처리하여 반환합니다.
     * <p>
     * 현재는 입력된 {@code Authentication} 객체를 검증 없이 그대로 반환하지만, 이메일 인증에 필요한 추가 로직을 포함할 수 있습니다.
     * </p>
     *
     * @param authentication 인증할 Authentication 객체
     * @return 검증된 Authentication 객체
     * @throws AuthenticationException 인증 처리에 실패할 경우 발생하는 예외
     */
    @Override
    public Authentication authenticate(Authentication authentication)
        throws AuthenticationException {
        return authentication;
    }

    /**
     * 지원하는 인증 타입을 확인합니다.
     * <p>
     * 이 구현체는 {@link UsernamePasswordAuthenticationToken} 타입의 인증 객체만 지원합니다.
     * </p>
     *
     * @param authentication 인증 타입 클래스
     * @return {@code true} if the authentication type is supported, {@code false} otherwise
     */
    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}