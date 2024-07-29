package com.sparta.goodbite.domain.restaurant.dto;

import com.sparta.goodbite.domain.owner.entity.Owner;
import com.sparta.goodbite.domain.restaurant.entity.Restaurant;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class RestaurantRequestDto {

    @NotNull(message = "가게 이름을 입력해주세요.")
    private String name;

    @NotNull(message = "가게 사진을 넣어주세요.")
    private String imageUrl;

    @NotNull(message = "가게 주소를 입력해주세요.")
    private String address;

    @NotNull(message = "가게 지역을 입력해주세요.")
    private String area;

    @NotNull(message = "가게 전화번호를 입력해주세요.")
    @Pattern(regexp = "^(0[2-8][0-5]?|01[01346-9])-?([1-9]{1}[0-9]{2,3})-?([0-9]{4})$", message = "전화번호 형식에 맞게 입력해주세요.")
    private String phoneNumber;

    @NotNull(message = "카테고리를 입력해주세요.")
    private String category;

    public Restaurant toEntity(Owner owner) {
        return Restaurant.builder()
            .owner(owner)
            .name(name)
            .imageUrl(imageUrl)
            .address(address)
            .area(area)
            .phoneNumber(phoneNumber)
            .category(category)
            .build();
    }

}
