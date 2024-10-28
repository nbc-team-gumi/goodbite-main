package site.mygumi.goodbite.domain.user.entity;

import java.util.ArrayList;
import java.util.Collection;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * 이 클래스는 스프링 시큐리티에서 사용됩니다. 사용자 인증-인가에 필요한 정보를 구성합니다. 사용자 이메일, 닉네임, 패스워드, 역할로 구성되어 있습니다.
 *
 * @author a-white-bit
 */
@Slf4j(topic = "EmailUserDetails")
@Getter
@RequiredArgsConstructor
public class EmailUserDetails implements UserDetails {

    private final UserCredentials user;
    private final String role;

    // principal - email
    public String getEmail() {
        return user.getEmail();
    }

    @Override
    public String getUsername() {
        return getEmail();
    }

    // credentials
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    // authorities - role
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(role);
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(simpleGrantedAuthority);

        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}