package com.sparta.goodbite.domain.menu.dto;

import com.sparta.goodbite.domain.menu.entity.Menu;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CreateMenuRequestDto {

//    @NotNull(message = "레스토랑 ID를 입력해 주세요.")
//    private Long restaurant_id;

    @NotNull(message = "메뉴의 가격을 입력해 주세요.")
    private int price;

    @NotBlank(message = "메뉴의 이름을 입력해 주세요.")
    private String name;

    @NotBlank(message = "메뉴의 설명을 입력해 주세요.")
    private String description;

    public Menu toEntity() {
        return Menu.builder().price(price).name(name).description(description).build();
    }
}