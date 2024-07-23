package com.sparta.goodbite.domain.customer.repository;

import com.sparta.goodbite.domain.customer.entity.Customer;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer,Long> {
    //Optional<Customer> findById(Long customerId);
    Optional<Customer> findByNickname(String nickname);
    Optional<Customer> findByEmail(String email);
    Optional<Customer> findByPhoneNumber(String phonenumber);
}
