package site.mygumi.goodbite.domain.auth.security;

import site.mygumi.goodbite.common.UserCredentials;
import java.util.ArrayList;
import java.util.Collection;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

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