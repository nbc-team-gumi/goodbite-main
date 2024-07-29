package com.sparta.goodbite.auth.security;

import com.sparta.goodbite.auth.UserRole;
import com.sparta.goodbite.domain.customer.entity.Customer;
import com.sparta.goodbite.domain.customer.repository.CustomerRepository;
import com.sparta.goodbite.domain.owner.entity.Owner;
import com.sparta.goodbite.domain.owner.entity.OwnerStatus;
import com.sparta.goodbite.domain.owner.repository.OwnerRepository;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j(topic = "EmailUserDetailsService")
@Service
@RequiredArgsConstructor
public class EmailUserDetailsService implements UserDetailsService {

    private final CustomerRepository customerRepository;
    private final OwnerRepository ownerRepository;
//    private final AdminRepository adminRepository;

//    public EmailUserDetails loadUserByEmail(String email, String role)
//        throws UsernameNotFoundException {
//
//        UserCredentials user;
//
//        switch (UserRole.valueOf(role)) {
//            case CUSTOMER -> user = (UserCredentials) customerRepository.findByEmailOrThrow(email);
//            case OWNER -> user = (UserCredentials) ownerRepository.findByEmailOrThrow(email);
//            case ADMIN -> user = (UserCredentials) adminRepository.findByEmailOrThrow(email);
//            default -> throw new IllegalArgumentException("잘못된 사용자 역할입니다.");
//        }
//
//        log.debug("user role case 통과!");
//
//        return new EmailUserDetails(user, UserRole.valueOf(role).getAuthority());
//    }

    /*
     * 구현의 복잡함으로 인해 우선적으로 아래 방법을 사용합니다.
     * 추후에 해결 방법을 찾아 봅시다.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<Customer> customer = customerRepository.findByEmail(username);
        if (customer.isPresent()) {
            return new EmailUserDetails(customer.get(), UserRole.CUSTOMER.getAuthority());
        }

        Optional<Owner> owner = ownerRepository.findByEmail(username);
        if (owner.isPresent() && Objects.equals(owner.get().getOwnerStatus(),
            OwnerStatus.VERIFIED)) {
            return new EmailUserDetails(owner.get(), UserRole.OWNER.getAuthority());
        }

//        Optional<Admin> admin = adminRepository.findByEmail(username);
//        if (admin.isPresent()) {
//            return new EmailUserDetails(admin.get(), UserRole.ADMIN.getAuthority());
//        }

        return null;
    }
}