package com.sparta.goodbite.domain.owner.dto;

import com.sparta.goodbite.domain.owner.entity.Owner;

public record OwnerResponseDto(String email, String nickname, String phoneNumber,
                               String businessNumber) {

    public static OwnerResponseDto from(Owner owner) {
        return new OwnerResponseDto(
            owner.getEmail(),
            owner.getNickname(),
            owner.getPhoneNumber(),
            owner.getBusinessNumber());
    }
}
