package site.mygumi.goodbite.domain.user.customer.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import site.mygumi.goodbite.domain.user.customer.entity.Customer;
import site.mygumi.goodbite.exception.customer.CustomerErrorCode;
import site.mygumi.goodbite.exception.customer.detail.CustomerNotFoundException;
import site.mygumi.goodbite.exception.customer.detail.DuplicateEmailException;
import site.mygumi.goodbite.exception.customer.detail.DuplicateNicknameException;
import site.mygumi.goodbite.exception.customer.detail.DuplicatePhoneNumberException;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByNickname(String nickname);

    Optional<Customer> findByEmail(String email);

    Optional<Customer> findByEmailAndDeletedAtIsNull(String email);

    Optional<Customer> findByPhoneNumber(String phoneNumber);

    default Customer findByIdOrThrow(Long customerId) {
        return findById(customerId).orElseThrow(
            () -> new CustomerNotFoundException(CustomerErrorCode.CUSTOMER_NOT_FOUND));
    }

    default void validateDuplicateNickname(String nickname) {
        findByNickname(nickname).ifPresent(ignored -> {
            throw new DuplicateNicknameException(CustomerErrorCode.DUPLICATE_NICKNAME);
        });
    }

    default void validateDuplicateEmail(String email) {
        findByEmail(email).ifPresent(ignored -> {
            throw new DuplicateEmailException(CustomerErrorCode.DUPLICATE_EMAIL);
        });
    }

    default void validateDuplicatePhoneNumber(String phoneNumber) {
        findByPhoneNumber(phoneNumber).ifPresent(ignored -> {
            throw new DuplicatePhoneNumberException(CustomerErrorCode.DUPLICATE_PHONE_NUMBER);
        });
    }
}