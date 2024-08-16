package com.sparta.goodbite.domain.customer.repository;

import com.sparta.goodbite.domain.customer.entity.Customer;
import com.sparta.goodbite.exception.customer.CustomerErrorCode;
import com.sparta.goodbite.exception.customer.detail.CustomerNotFoundException;
import com.sparta.goodbite.exception.customer.detail.DuplicateEmailException;
import com.sparta.goodbite.exception.customer.detail.DuplicateNicknameException;
import com.sparta.goodbite.exception.customer.detail.DuplicatePhoneNumberException;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByNickname(String nickname);

    Optional<Customer> findByEmail(String email);

    Optional<Customer> findByEmailAndDeletedAtIsNotNull(String email);

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