package site.mygumi.goodbite.domain.user.service;

import site.mygumi.goodbite.domain.auth.UserRole;
import site.mygumi.goodbite.domain.user.customer.entity.Customer;
import site.mygumi.goodbite.domain.user.customer.repository.CustomerRepository;
import site.mygumi.goodbite.domain.user.owner.entity.Owner;
import site.mygumi.goodbite.domain.user.owner.entity.OwnerStatus;
import site.mygumi.goodbite.domain.user.owner.repository.OwnerRepository;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import site.mygumi.goodbite.domain.user.entity.EmailUserDetails;

@Slf4j(topic = "EmailUserDetailsService")
@Service
@RequiredArgsConstructor
public class EmailUserDetailsService implements UserDetailsService {

    private final CustomerRepository customerRepository;
    private final OwnerRepository ownerRepository;

    public EmailUserDetails loadUserByEmail(String email, String role)
        throws UsernameNotFoundException {

        if (Objects.equals(role, UserRole.CUSTOMER.getAuthority())) {
            Optional<Customer> customer = customerRepository.findByEmailAndDeletedAtIsNull(email);
            if (customer.isPresent()) {
                return new EmailUserDetails(customer.get(), role);
            }
        } else if (Objects.equals(role, UserRole.OWNER.getAuthority())) {
            Optional<Owner> owner = ownerRepository.findByEmailAndDeletedAtIsNull(email);
            if (owner.isPresent() && Objects.equals(owner.get().getOwnerStatus(),
                OwnerStatus.VERIFIED)) {
                return new EmailUserDetails(owner.get(), role);
            }
        }
        return null;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return null;
    }
}