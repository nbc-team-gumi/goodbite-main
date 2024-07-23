package com.sparta.goodbite.domain.Customer.repository;

import com.sparta.goodbite.domain.Customer.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

}
