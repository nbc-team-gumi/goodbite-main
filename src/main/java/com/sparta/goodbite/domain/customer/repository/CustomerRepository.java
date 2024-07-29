package com.sparta.goodbite.domain.customer.repository;

import com.sparta.goodbite.domain.customer.entity.Customer;
import com.sparta.goodbite.exception.customer.CustomerErrorCode;
import com.sparta.goodbite.exception.customer.detail.CustomerNotFoundException;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByNickname(String nickname);

    Optional<Customer> findByEmail(String email);

    Optional<Customer> findByPhoneNumber(String phoneNumber);

    default Customer findByEmailOrThrow(String email) {
        return findByEmail(email).orElseThrow(() -> new CustomerNotFoundException(
            CustomerErrorCode.CUSTOMER_NOT_FOUND));
    }

    default Customer findByIdOrThrow(Long customerId) {
        return findById(customerId).orElseThrow(
            () -> new CustomerNotFoundException(CustomerErrorCode.CUSTOMER_NOT_FOUND));
    }
}