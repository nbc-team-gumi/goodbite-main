package com.sparta.goodbite.auth.security;

import com.sparta.goodbite.auth.UserRoleEnum;
import com.sparta.goodbite.auth.dummy.UserCredentials;
import com.sparta.goodbite.domain.user.admin.repository.AdminRepository;
import com.sparta.goodbite.domain.user.customer.repository.CustomerRepository;
import com.sparta.goodbite.domain.user.owner.repository.OwnerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailUserDetailsService implements UserDetailsService {

    private final CustomerRepository customerRepository;
    private final OwnerRepository ownerRepository;
    private final AdminRepository adminRepository;

    public UserDetails loadUserByEmail(String email, String role) throws UsernameNotFoundException {

        UserCredentials user;

        switch (UserRoleEnum.valueOf(role)) {
            case CUSTOMER -> user = (UserCredentials) customerRepository.findByEmailOrThrow(email);
            case OWNER -> user = (UserCredentials) ownerRepository.findByEmailOrThrow(email);
            case ADMIN -> user = (UserCredentials) adminRepository.findByEmailOrThrow(email);
            default -> throw new IllegalArgumentException("잘못된 사용자 역할입니다.");
        }

        return new EmailUserDetails(user, role);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        throw new UsernameNotFoundException("사용되지 않는 메서드입니다.");
    }
}