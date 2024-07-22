package com.sparta.goodbite.domain.restaurant.dto;

import com.sparta.goodbite.domain.restaurant.entity.Restaurant;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class RestaurantRequestDto {

    @NotNull(message = "사장님의 Id를 입력해주세요.")
    private Long ownerId;
    @NotNull(message = "가게 이름을 입력해주세요.")
    private String name;
    @NotNull(message = "가게 사진을 넣어주세요.")
    private String picture;
    @NotNull(message = "가게 주소를 입력해주세요.")
    private String address;
    @NotNull(message = "가게 지역을 입력해주세요.")
    private String area;
    @NotNull(message = "가게 전화번호를 입력해주세요.")
    private String telno;
    @NotNull(message = "카테고리를 입력해주세요.")
    private String category;

    public Restaurant toEntity() {
        return Restaurant.builder().ownerId(ownerId).name(name).picture(picture).address(address)
            .area(area).telno(telno).category(category).build();
    }

}
