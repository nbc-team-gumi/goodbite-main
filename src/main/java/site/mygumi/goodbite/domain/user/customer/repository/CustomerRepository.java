package site.mygumi.goodbite.domain.user.customer.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import site.mygumi.goodbite.domain.user.customer.entity.Customer;
import site.mygumi.goodbite.domain.user.customer.exception.CustomerErrorCode;
import site.mygumi.goodbite.domain.user.customer.exception.detail.CustomerNotFoundException;
import site.mygumi.goodbite.domain.user.customer.exception.detail.DuplicateEmailException;
import site.mygumi.goodbite.domain.user.customer.exception.detail.DuplicateNicknameException;
import site.mygumi.goodbite.domain.user.customer.exception.detail.DuplicatePhoneNumberException;

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
        findByNickname(nickname).ifPresent(_customer -> {
            throw new DuplicateNicknameException(CustomerErrorCode.DUPLICATE_NICKNAME);
        });
    }

    default void validateDuplicateEmail(String email) {
        findByEmail(email).ifPresent(_customer -> {
            throw new DuplicateEmailException(CustomerErrorCode.DUPLICATE_EMAIL);
        });
    }

    default void validateDuplicatePhoneNumber(String phoneNumber) {
        findByPhoneNumber(phoneNumber).ifPresent(_customer -> {
            throw new DuplicatePhoneNumberException(CustomerErrorCode.DUPLICATE_PHONE_NUMBER);
        });
    }
}