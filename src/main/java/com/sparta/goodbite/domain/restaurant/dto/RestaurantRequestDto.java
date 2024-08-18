package com.sparta.goodbite.domain.restaurant.dto;

import com.sparta.goodbite.domain.owner.entity.Owner;
import com.sparta.goodbite.domain.restaurant.entity.Restaurant;
import com.sparta.goodbite.domain.restaurant.enums.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class RestaurantRequestDto {

    @NotBlank(message = "가게 이름을 입력해주세요.")
    private String name;

//    @NotBlank(message = "가게 사진을 넣어주세요.")
//    private String imageUrl;

    @NotBlank(message = "가게 주소를 입력해주세요.")
    private String address;

    @NotBlank(message = "가게 지역을 입력해주세요.")
    private String area;

    @NotBlank(message = "가게 전화번호를 입력해주세요.")
    @Pattern(regexp = "^(0[2-8][0-5]?|01[01346-9])-?([1-9]{1}[0-9]{2,3})-?([0-9]{4})$", message = "전화번호 형식에 맞게 입력해주세요.")
    private String phoneNumber;

    @NotNull(message = "카테고리를 입력해주세요.")
    private Category category;

    @NotNull(message = "최대 수용 인원을 입력해주세요.")
    private Integer capacity;

    public Restaurant toEntity(Owner owner, String image) {
        return Restaurant.builder()
            .owner(owner)
            .name(name)
            .imageUrl(image)
            .address(address)
            .area(area)
            .phoneNumber(phoneNumber)
            .category(category)
            .capacity(capacity)
            .build();
    }
}