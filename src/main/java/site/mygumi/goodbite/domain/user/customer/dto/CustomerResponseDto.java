package site.mygumi.goodbite.domain.user.customer.dto;

import site.mygumi.goodbite.domain.user.customer.entity.Customer;

public record CustomerResponseDto(String email, String nickname, String phoneNumber) {

    public static CustomerResponseDto from(Customer customer) {
        return new CustomerResponseDto(
            customer.getEmail(),
            customer.getNickname(),
            customer.getPhoneNumber());
    }
}