package com.sparta.goodbite.domain.customer.dto;

import com.sparta.goodbite.domain.customer.entity.Customer;

public record CustomerResponseDto(String email, String nickname, String phonenumber) {

    public static CustomerResponseDto from(Customer customer) {
        return new CustomerResponseDto(customer.getEmail(), customer.getNickname(),
            customer.getPhoneNumber());
    }
}