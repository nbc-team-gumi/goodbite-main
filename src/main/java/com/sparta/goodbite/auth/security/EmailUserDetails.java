package com.sparta.goodbite.auth.security;

import com.sparta.goodbite.auth.UserRoleEnum;
import com.sparta.goodbite.auth.dummy.UserCredentials;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@RequiredArgsConstructor
public class EmailUserDetails implements UserDetails {

    private final UserCredentials user;
    private final String role;

    // principal - email
    public String getEmail() {
        return user.getEmail();
    }

    // principal - email
    @Override
    public String getUsername() {
        return user.getEmail();
    }

    // credentials
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    // authorities - role
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String authority = "";
        if (Objects.equals(role, UserRoleEnum.CUSTOMER.name())) {
            authority = UserRoleEnum.CUSTOMER.getAuthority();
        } else if (Objects.equals(role, UserRoleEnum.OWNER.name())) {
            authority = UserRoleEnum.OWNER.getAuthority();
        } else if (Objects.equals(role, UserRoleEnum.ADMIN.name())) {
            authority = UserRoleEnum.ADMIN.getAuthority();
        }

        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(authority);
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