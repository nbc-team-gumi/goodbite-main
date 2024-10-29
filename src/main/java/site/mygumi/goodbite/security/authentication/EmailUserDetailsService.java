package site.mygumi.goodbite.security.authentication;

import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import site.mygumi.goodbite.domain.user.customer.entity.Customer;
import site.mygumi.goodbite.domain.user.customer.repository.CustomerRepository;
import site.mygumi.goodbite.domain.user.entity.EmailUserDetails;
import site.mygumi.goodbite.domain.user.entity.UserRole;
import site.mygumi.goodbite.domain.user.owner.entity.Owner;
import site.mygumi.goodbite.domain.user.owner.entity.OwnerStatus;
import site.mygumi.goodbite.domain.user.owner.repository.OwnerRepository;

/**
 * 사용자 이메일과 역할을 기반으로 사용자 정보를 조회하고 인증된 {@link UserDetails} 객체를 반환하는 커스텀 UserDetailsService 구현체입니다.
 *
 * <p>이 클래스는 {@code Customer}와 {@code Owner}라는 두 가지 사용자 유형을 다루며,
 * 각각의 역할에 따라 저장소에서 정보를 조회하여 인증된 사용자 객체를 생성합니다.</p>
 *
 * <p>사용 예시:
 * <pre>
 * UserDetailsService userService = new EmailUserDetailsService(customerRepository, ownerRepository);
 * UserDetails user = userService.loadUserByEmail("user@example.com", "CUSTOMER");
 * </pre>
 * </p>
 *
 * @author a-white-bit
 */
@Slf4j(topic = "EmailUserDetailsService")
@Service
@RequiredArgsConstructor
public class EmailUserDetailsService implements UserDetailsService {

    private final CustomerRepository customerRepository;
    private final OwnerRepository ownerRepository;

    /**
     * 이메일과 역할에 따라 사용자 정보를 조회하고, 인증된 사용자 객체를 반환합니다.
     *
     * <p>입력된 역할이 {@code CUSTOMER}일 경우, {@code CustomerRepository}에서 사용자 정보를 조회하여
     * {@code EmailUserDetails} 객체를 생성하고 반환합니다.</p>
     *
     * <p>입력된 역할이 {@code OWNER}일 경우, {@code OwnerRepository}에서 사용자 정보를 조회하고,
     * {@code OwnerStatus}가 {@code VERIFIED}인 경우에만 {@code EmailUserDetails} 객체를 반환합니다.</p>
     *
     * @param email 조회할 사용자의 이메일
     * @param role  사용자의 역할 (예: "CUSTOMER", "OWNER")
     * @return 인증된 {@link EmailUserDetails} 객체, 조회된 사용자가 없거나 역할이 일치하지 않을 경우 {@code null}
     * @throws UsernameNotFoundException 이메일로 사용자를 찾을 수 없는 경우 발생하는 예외
     */
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

    /**
     * 사용자 이름(username)으로 사용자 정보를 로드합니다.
     *
     * <p>현재 구현에서는 이메일 기반 인증을 사용하므로, 이 메서드는 {@code null}을 반환합니다.
     * </p>
     *
     * @param username 조회할 사용자의 이름
     * @return {@code null}
     * @throws UsernameNotFoundException 사용자 이름으로 사용자를 찾을 수 없는 경우 발생하는 예외
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return null;
    }
}